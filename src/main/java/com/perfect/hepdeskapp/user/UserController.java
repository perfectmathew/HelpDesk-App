package com.perfect.hepdeskapp.user;

import com.perfect.hepdeskapp.config.EmailExistsException;
import com.perfect.hepdeskapp.config.Utility;
import com.perfect.hepdeskapp.department.Department;
import com.perfect.hepdeskapp.department.DepartmentRepository;
import com.perfect.hepdeskapp.notification.NotifyService;
import com.perfect.hepdeskapp.role.Role;
import com.perfect.hepdeskapp.role.RoleRepository;
import com.perfect.hepdeskapp.status.StatusRepository;
import com.perfect.hepdeskapp.ticket.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
public class UserController {
    final
    TicketRepository ticketRepository;
    final
    UserRepository userRepository;
    final
    RoleRepository roleRepository;
    final
    StatusRepository statusRepository;
    final
    DepartmentRepository departmentRepository;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    final
    NotifyService notify;
    final
    Environment environment;

    public UserController(TicketRepository ticketRepository, UserRepository userRepository, RoleRepository roleRepository, StatusRepository statusRepository, DepartmentRepository departmentRepository, NotifyService notify, Environment environment) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.statusRepository = statusRepository;
        this.departmentRepository = departmentRepository;
        this.notify = notify;
        this.environment = environment;
    }

    // REQUESTS TO HR SECTIONS
    @RequestMapping("/admin/hr/managers")
    public String adminHrManagersPanel(Model model){
        model.addAttribute("roles",roleRepository.findAll());
        model.addAttribute("departments",departmentRepository.findAll());
        model.addAttribute("users",userRepository.findUserByRoleName("DEPARTMENT_BOSS"));
        model.addAttribute("type","Managers");
        return "admin/admin_hr";
    }
    @RequestMapping("/admin/hr/workers")
    public String adminHrWorkersPanel(Model model){
        model.addAttribute("departments",departmentRepository.findAll());
        model.addAttribute("roles",roleRepository.findAll());
        model.addAttribute("users",userRepository.findUserByRoleName("WORKER"));
        model.addAttribute("type","Workers");
        return "admin/admin_hr";
    }
    @RequestMapping("/admin/hr/administrators")
    public String adminHrAdministratorsPanel(Model model){
        model.addAttribute("departments",departmentRepository.findAll());
        model.addAttribute("roles",roleRepository.findAll());
        model.addAttribute("users",userRepository.findUserByRoleName("ADMIN"));
        model.addAttribute("type","Administrators");
        return "admin/admin_hr";
    }
    @RequestMapping("/manager/hr/workers")
    public String managerWorkersPanel(Model model){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        String email = ((UserDetail)principal).getUsername();
        User user = userRepository.findUserByEmail(email);
        model.addAttribute("users",userRepository.findUserByDepartmentAndRole(user.getDepartment(),"WORKER"));
        model.addAttribute("department",user.getDepartment());
        return "manager/manager_hr";
    }

    // User Pagination And Search
    @GetMapping("/manager/hr/{type}/{value}")
    @ResponseBody
    public List<User> searchUserByEmail(@PathVariable String type, @PathVariable String value){
        if (type.equals("managers")){
            if (!userRepository.findUserByEmailAndRoleName(value,"DEPARTMENT_BOSS").isEmpty()){
               return userRepository.findUserByEmailAndRoleName(value,"DEPARTMENT_BOSS");
            }
            else throw new EmailExistsException("No records was found!");
        } else if (type.equals("workers")) {
            if (!userRepository.findUserByEmailAndRoleName(value,"WORKER").isEmpty()){
                return userRepository.findUserByEmailAndRoleName(value,"WORKER");
            }
            else throw new EmailExistsException("No users was found!");
        }else {
            return userRepository.findAll();
        }
    }
    @GetMapping("/admin/api/hr/{type}/getAllUsers")
    @ResponseBody
    public List<User> getAllUsersByRole(@PathVariable String type){
        return switch (type) {
            case "managers" ->
                    userRepository.findUserByRoleName(roleRepository.findRoleByName("DEPARTMENT_BOSS").getName());
            case "workers" -> userRepository.findUserByRoleName(roleRepository.findRoleByName("WORKER").getName());
            default -> userRepository.findAll();
        };
    }
    @GetMapping("/admin/hr/{type}/getAll")
    @ResponseBody
    public Page<User> getAllUsersPageableByRole(@PathVariable String type, @RequestParam int page, @RequestParam int size){
        Pageable paging = PageRequest.of(page, size);
        return switch (type) {
            case "managers" ->
                    userRepository.findUserByRoleNameAndPageable(roleRepository.findRoleByName("DEPARTMENT_BOSS").getName(),paging);
            case "workers" -> userRepository.findUserByRoleNameAndPageable(roleRepository.findRoleByName("WORKER").getName(),paging);
            default -> userRepository.findAll(paging);
        };
    }
    @GetMapping("/manager/api/hr/getDepartmentWorkers")
    @ResponseBody
    public Page<User> getDepartmentWorkersByRole(@RequestParam int page, @RequestParam int size){
        Pageable paging = PageRequest.of(page, size);
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        String email = ((UserDetail)principal).getUsername();
        User user = userRepository.findUserByEmail(email);
        return userRepository.findUserByDepartmentAndRole(user.getDepartment(),"WORKER",paging);
    }
    @GetMapping("/manager/api/getWorkersToAssign")
    @ResponseBody
    public List<User> getWorkersToAssign(@RequestParam("search_term") String search_term, @RequestParam("department_name") String department_name){
        if (search_term!=null && !search_term.equals(""))
        return userRepository.findUserByNameAndSurnameAndDepartmentAndRole(search_term,departmentRepository.findDepartmentByName(department_name),"WORKER");
        else return userRepository.findUserByDepartmentAndRole(departmentRepository.findDepartmentByName(department_name),"WORKER");
    }
    // API SECTION
    @PostMapping("/admin/hr/user/lock")
    @ResponseBody
    public boolean lockAccount(@RequestParam("operation") boolean operation, @RequestParam("userid") Long userid){
        User user = userRepository.findUserById(userid);
        user.setEnabled(operation);
        userRepository.saveAndFlush(user);
        return operation;
    }
    @PostMapping( "/manager/api/addUser")
    @ResponseBody public User addNewUser(HttpServletRequest request, @RequestParam("name") String name,
                                         @RequestParam("surname") String surname, @RequestParam("phone_number") String phone_number,
                                         @RequestParam("email") String email, @RequestParam("password") String password ,
                                         @RequestParam(value = "department",required = false) Long department,
                                         @RequestParam(value = "role", required = false) Long role) throws IOException, EmailExistsException {
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        User userCheck = userRepository.findUserByEmail(((UserDetail)principal).getUsername());
        String url = Utility.getSiteURL(request) + "/auth";
       if(checkEmail(email)) throw new EmailExistsException("User with this email already exist.");
        Department departmentObject = departmentRepository.findDepartmentById(department);
        passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        User user;
        if(role!=null) {
           user = new User(name, surname, phone_number, email,encodedPassword, departmentObject, roleRepository.findRoleById(role));
           user.setEnabled(true);
           userRepository.saveAndFlush(user);
           if (!Objects.requireNonNull(environment.getProperty("smtp.status")).equals("OFF")) {
               try {
                   notify.sendEmail(email, "HelpDesk | The administrator has created an account for you!", "<p>Hello, the helpdesk system administrator has created your account. Here are your access credentials.</p>" +
                           "<p>Your login: " + email + "</p>" +
                           "<p>Your password: " + password + "</p>" +
                           "<p> Click on this <a href='" + url + "'> link</a> to go to the login page.</p>" +
                           "<p>We wish you to enjoy using our system.</p>");
               } catch (MessagingException me) {
                   me.printStackTrace();
                    throw new EmailExistsException("User added successfully, but there was an error with the smtp server.");
               }
           }

        }else {
            if(checkEmail(email)) throw new EmailExistsException("User with this email already exist.");
           user = new User(name, surname, phone_number, email,encodedPassword, userCheck.getDepartment(),roleRepository.findRoleByName("WORKER"));
           user.setEnabled(true);
           userRepository.saveAndFlush(user);
           if (!Objects.requireNonNull(environment.getProperty("smtp.status")).equals("OFF")) {
               try {
                   notify.sendEmail(email, "HelpDesk | The manager has created an account for you!", "<p>Hello, the manager has created your account. Here are your access credentials.</p>" +
                           "<p>Your login: " + email + "</p>" +
                           "<p>Your password: " + password + "</p>" +
                           "<p> Click on this <a href='" + url + "'> link</a> to go to the login page.</p>" +
                           "<p>We wish you to enjoy using our system.</p>");
               } catch (MessagingException me) {
                   me.printStackTrace();
                   throw new EmailExistsException("User added successfully, but there was an error with the smtp server.");
               }
           }
        }
        return user;
    }
    @GetMapping("/manager/api/checkEmail")
    @ResponseBody public boolean checkEmail(@RequestParam("email") String email){
        User user = userRepository.findUserByEmail(email);
        return user != null;
    }
    @PatchMapping("/manager/api/editUser")
    @ResponseBody public User editUser(HttpServletRequest request, @RequestParam("userid") Long userid, @RequestParam(value = "name",required = false) String name,
                           @RequestParam(value = "surname",required = false) String surname, @RequestParam(value = "phone_number",required = false) String phone_number,
                           @RequestParam(value = "email") String email,@RequestParam(value = "password",required = false) String password ,
                           @RequestParam(value = "department",required = false) Long department, @RequestParam(value = "role",required = false) Long role) throws  IOException{
        User user = userRepository.findUserById(userid);
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        User userCheck = userRepository.findUserByEmail(((UserDetail)principal).getUsername());
        if(user.getRoleSet().contains(roleRepository.findRoleByName("ADMIN")) && !userCheck.getRoleSet().contains(roleRepository.findRoleByName("ADMIN"))){
            throw new EmailExistsException("You don't have permission for that action!");
        }
        user.setName(name);
        user.setSurname(surname);
        user.setPhone_number(phone_number);
        if(!user.getEmail().equals(email)){
            User userByEmail = userRepository.findUserByEmail(email);
            if (userByEmail != null){ throw new EmailExistsException("This email is already taken!"); }
            else user.setEmail(email);
        }
        passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        if(!password.equals("")) user.setPassword(encodedPassword);
        if(department!=null) user.setDepartment(departmentRepository.findDepartmentById(department));
        if(role!=null) user.setRoleSet(roleRepository.findRoleById(role));
        userRepository.saveAndFlush(user);
        String url = Utility.getSiteURL(request) + "/auth";
        if(!Objects.requireNonNull(environment.getProperty("smtp.status")).equals("OFF")) {
            try {
                if(!password.equals("")){
                    notify.sendEmail(user.getEmail(), "HelpDesk | Your data has been changed.", "<p>Hello, your data has been changed by the administrator/manager...</p>" +
                            "<p>Your login: " + email + "</p>" +
                            "<p>Your password: " + password + "</p>" +
                            "<p> Click on this <a href='" + url + "'> link</a> to go to the login page.</p>" +
                            "<p>Helpdesk system notification - please don't reply to this message.</p>");
                }
                else {
                    notify.sendEmail(user.getEmail(), "HelpDesk | Your data has been changed.", "<p>Hello, your data has been changed by the administrator/manager...</p>" +
                            "<p>Your login credentials have not changed</p>" +
                            "<p> Click on this <a href='" + url + "'> link</a> to go to the login page.</p>" +
                            "<p>Helpdesk system notification - please don't reply to this message.</p>");
                }
            } catch (MessagingException me) {
                me.printStackTrace();
                throw new EmailExistsException("User data updated successfully, but there is a SMTP server error!");
            }
        }
       return user;
    }
    @PostMapping("/admin/api/deleteUser")
    @ResponseBody
    public String deleteUser(@RequestParam("userid") Long userid) throws IOException{
        User user =  userRepository.findUserById(userid);
        if(user!=null){
            if(!user.getRoleSet().isEmpty()) user.getRoleSet().clear();
            if(!user.getUserTickets().isEmpty()) user.getUserTickets().clear();
            userRepository.delete(user);
            return "Successful";
        }
        if(!Objects.requireNonNull(environment.getProperty("smtp.status")).equals("OFF")) {
            try {
                notify.sendEmail(user.getEmail(), "HelpDesk | Your account has been deactivated!", "<p>Hello, your account has been deactivated. To re-enable it, contact your administrator..</p>" +
                        "<p>Helpdesk system notification - please don't reply to this message.</p>");
            } catch (MessagingException me) {
                me.printStackTrace();
            }
        }
        return "User not found";
    }

    @PostMapping("/manager/api/deleteUser")
    @ResponseBody
    public String deleteUserFromDepartment(@RequestParam("userid") Long userid) throws IOException{
        User user = userRepository.findUserById(userid);
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        User userCheck = userRepository.findUserByEmail(((UserDetail)principal).getUsername());
        if(user.getRoleSet().contains(roleRepository.findRoleByName("ADMIN")) && !userCheck.getRoleSet().contains(roleRepository.findRoleByName("ADMIN"))){
            return "BAD PERMISSION";
        }
        user.setDepartment(departmentRepository.findDepartmentByName("UNALLOCATED"));
        user.setEnabled(false);
        if(!Objects.requireNonNull(environment.getProperty("smtp.status")).equals("OFF")) {
            try {
                notify.sendEmail(user.getEmail(), "HelpDesk | Your account has been deactivated!", "<p>Hello, your account has been deactivated. To re-enable it, contact your administrator..</p>" +
                        "<p>Helpdesk system notification - please don't reply to this message.</p>");
            } catch (MessagingException me) {
                me.printStackTrace();
            }
        }
        userRepository.saveAndFlush(user);
        return "Successful";
    }
    @GetMapping("/manager/api/getUserRoles")
    @ResponseBody
    public Set<Role> userRoleSet(@RequestParam("userid") Long userid){
        User user = userRepository.findUserById(userid);
        return user.getRoleSet();
    }
    @GetMapping("/manager/api/getUserDetails")
    @ResponseBody
    public User getUserDetails(@RequestParam("userid") Long userid){
        return userRepository.findUserById(userid);
    }
    @PostMapping("/manager/api/deleteUserRole")
    @ResponseBody
    public String deleteUserRole(@RequestParam("role") Long role, @RequestParam("userid") Long userid){
        User user = userRepository.findUserById(userid);
        if(user != null) {
            user.removeRole(roleRepository.findRoleById(role));
            userRepository.saveAndFlush(user);
            return "Success";
        }
        else return  "Error! User not found";
    }



    // Profiles section
    // Request Paths to users profile and manage it.
    @GetMapping("/api/profile")
    public String userProfile(Model model){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        String email = ((UserDetail)principal).getUsername();
        User user = userRepository.findUserByEmail(email);
        model.addAttribute("user",user);
        return "user_profile";
    }
    @GetMapping("/api/getUserDetails")
    @ResponseBody
    public User getCurrentLoggedUserDetails(){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        String email = ((UserDetail)principal).getUsername();
        return userRepository.findUserByEmail(email);
    }

    @PatchMapping("/api/updateUserDetails")
    @ResponseBody
    public String updateUserDetails(@RequestParam("user-name") String user_name,
                                  @RequestParam("user-surname") String user_surname,
                                  @RequestParam("user-email") String user_email,
                                  @RequestParam("user-phone") String user_phone,
                                  @RequestParam(value = "user-password",required = false) String user_new_password)
    {
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        String email = ((UserDetail)principal).getUsername();
        User user = userRepository.findUserByEmail(email);
        if(!user.getEmail().equals(user_email)){
            User checkIfUserExists = userRepository.findUserByEmail(user_email);
            if(checkIfUserExists != null){
                user.setEmail(user_email);
                user.setName(user_name);
                user.setSurname(user_surname);
                user.setPhone_number(user_phone);
                if(!user_new_password.equals("null")){
                    passwordEncoder = new BCryptPasswordEncoder();
                    String encodedPassword = passwordEncoder.encode(user_new_password);
                    user.setPassword(encodedPassword);
                }
                userRepository.saveAndFlush(user);
                return "Data has been updated successfully";
            } else {
                return "This email address already exists";
            }
        }else {
            user.setName(user_name);
            user.setSurname(user_surname);
            user.setPhone_number(user_phone);
            if(!user_new_password.equals("null")){
                passwordEncoder = new BCryptPasswordEncoder();
                String encodedPassword = passwordEncoder.encode(user_new_password);
                user.setPassword(encodedPassword);
            }
            userRepository.saveAndFlush(user);
            return "Data has been updated successfully";
        }
    }

}
