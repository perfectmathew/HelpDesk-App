package com.perfect.hepdeskapp.user;

import com.perfect.hepdeskapp.config.Utility;
import com.perfect.hepdeskapp.department.Department;
import com.perfect.hepdeskapp.department.DepartmentRepository;
import com.perfect.hepdeskapp.notification.NotifyService;
import com.perfect.hepdeskapp.role.Role;
import com.perfect.hepdeskapp.role.RoleRepository;
import com.perfect.hepdeskapp.status.StatusRepository;
import com.perfect.hepdeskapp.ticket.Ticket;
import com.perfect.hepdeskapp.ticket.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
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

    @RequestMapping(value = "/admin")
    public String adminPanel(Model model){
        List<Ticket> ticketList = ticketRepository.findAll();
        model.addAttribute("ticketList",ticketList);
        return "admin/admin_panel";
    }
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
    @RequestMapping("/admin/hr/users")
    public String adminHrUsersPanel(Model model){
        model.addAttribute("departments",departmentRepository.findAll());
        model.addAttribute("roles",roleRepository.findAll());
        model.addAttribute("users",userRepository.findUserByRoleName("USER"));
        model.addAttribute("type","Users");
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
    public String addNewUser(HttpServletRequest request, @RequestParam("name") String name, @RequestParam("surname") String surname,
                             @RequestParam("phone_number") String phone_number, @RequestParam("email") String email, @RequestParam("password") String password , @RequestParam("department") Long department, @RequestParam(value = "role", required = false) Long role) throws IOException {
        Department departmentObject = departmentRepository.findDepartmentById(department);
        passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
       if(role!=null) {
           Role roleObject = roleRepository.findRoleById(role);
           User user = new User(name, surname, phone_number, email,encodedPassword, departmentObject, roleObject);
           user.setEnabled(true);
           userRepository.saveAndFlush(user);
           String url = Utility.getSiteURL(request) + "/auth";
           if (!environment.getProperty("smtp.status").equals("OFF")) {
               try {
                   notify.sendEmail(email, "HelpDesk | The administrator has created an account for you!", "<p>Hello, the helpdesk system administrator has created your account. Here are your access credentials.</p>" +
                           "<p>Your login: " + email + "</p>" +
                           "<p>Your password: " + password + "</p>" +
                           "<p> Click on this <a href='" + url + "'> link</a> to go to the login page.</p>" +
                           "<p>We wish you to enjoy using our system.</p>");
               } catch (MessagingException me) {
                   me.printStackTrace();
               }
           }
           return "redirect:/admin/hr/workers";
       }else {
           Role roleObject = roleRepository.findRoleByName("WORKER");
           User user = new User(name, surname, phone_number, email,encodedPassword, departmentObject,roleObject);
           user.setEnabled(true);
           userRepository.saveAndFlush(user);
           String url = Utility.getSiteURL(request) + "/auth";
           if (!environment.getProperty("smtp.status").equals("OFF")) {
               try {
                   notify.sendEmail(email, "HelpDesk | The manager has created an account for you!", "<p>Hello, the manager has created your account. Here are your access credentials.</p>" +
                           "<p>Your login: " + email + "</p>" +
                           "<p>Your password: " + password + "</p>" +
                           "<p> Click on this <a href='" + url + "'> link</a> to go to the login page.</p>" +
                           "<p>We wish you to enjoy using our system.</p>");
               } catch (MessagingException me) {
                   me.printStackTrace();
               }
           }
           return "redirect:/manager/hr/workers";
       }
    }
    @GetMapping("/manager/api/checkEmail")
    @ResponseBody
    public boolean checkEmail(@RequestParam("email") String email){
        User user = userRepository.findUserByEmail(email);
        if(user!=null){
            return true;
        }else {
            return false;
        }

    }
    @PostMapping("/manager/api/editUser")
    public String editUser(HttpServletRequest request, @RequestParam("userid") Long userid, @RequestParam(value = "name",required = false) String name,
                           @RequestParam(value = "surname",required = false) String surname, @RequestParam(value = "phone_number",required = false) String phone_number,
                           @RequestParam(value = "email") String email,@RequestParam(value = "password",required = false) String password ,
                           @RequestParam(value = "department",required = false) Long department, @RequestParam(value = "role",required = false) Long role) throws  IOException{
        User user = userRepository.findUserById(userid);
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        User userCheck = userRepository.findUserByEmail(((UserDetail)principal).getUsername());
        if(user.getRoleSet().contains(roleRepository.findRoleByName("ADMIN")) && !userCheck.getRoleSet().contains(roleRepository.findRoleByName("ADMIN"))){
            return "redirect:/manager/hr/workers";
        }
        user.setName(name);
        user.setSurname(surname);
        user.setPhone_number(phone_number);
        passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        if(!password.equals("") && password != null) user.setPassword(encodedPassword);
        if(department!=null) user.setDepartment(departmentRepository.findDepartmentById(department));
        if(role!=null) user.setRoleSet(roleRepository.findRoleById(role));
        userRepository.saveAndFlush(user);
        String url = Utility.getSiteURL(request) + "/auth";
        if(!environment.getProperty("smtp.status").equals("OFF")) {
            try {
                if(!password.equals("") && password != null){
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
            }
        }

       return "redirect:/manager/hr/workers";
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
        if(!environment.getProperty("smtp.status").equals("OFF")) {
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

    @RequestMapping("/admin/departments")
    public String departments(Model model){
        model.addAttribute("departments",departmentRepository.findAll());
        return "admin/admin_departments";
    }

    // Profiles section
    // Request Paths to users profile and manage it.
    @RequestMapping("/api/profile")
    public String userProfile(Model model){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        String email = ((UserDetail)principal).getUsername();
        User user = userRepository.findUserByEmail(email);
        model.addAttribute("user",user);
        return "user_profile";
    }

}
