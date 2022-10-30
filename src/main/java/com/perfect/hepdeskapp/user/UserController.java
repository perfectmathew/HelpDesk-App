package com.perfect.hepdeskapp.user;

import com.perfect.hepdeskapp.config.Utility;
import com.perfect.hepdeskapp.department.Department;
import com.perfect.hepdeskapp.department.DepartmentRepository;
import com.perfect.hepdeskapp.notification.NotifyService;
import com.perfect.hepdeskapp.role.Role;
import com.perfect.hepdeskapp.role.RoleRepository;
import com.perfect.hepdeskapp.ticket.Ticket;
import com.perfect.hepdeskapp.ticket.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

@Controller
public class UserController {
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Autowired
    NotifyService notify;
    @Autowired
    Environment environment;
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
    // API SECTION

    @PostMapping( "/api/addUser")
    public String addNewUser(HttpServletRequest request, @RequestParam("name") String name, @RequestParam("surname") String surname,
                             @RequestParam("phone_number") String phone_number, @RequestParam("email") String email, @RequestParam("password") String password , @RequestParam("department") Long department, @RequestParam("role") Long role) throws IOException {
        Department departmentObject = departmentRepository.findDepartmentById(department);
        Role roleObject = roleRepository.findRoleById(role);
        passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(name,surname,phone_number,email,encodedPassword,departmentObject,roleObject);
        userRepository.saveAndFlush(user);
        String url = Utility.getSiteURL(request) + "/auth";
        if(!environment.getProperty("smtp.status").equals("OFF")) {
            try {
                notify.sendEmail(email,"HelpDesk | The administrator has created an account for you!","<p>Hello, the helpdesk system administrator has created your account. Here are your access credentials.</p>" +
                        "<p>Your login: "+ email+"</p>" +
                        "<p>Your password: "+password+"</p>" +
                        "<p> Click on this <a href='"+url+"'> link</a> to go to the login page.</p>" +
                        "<p>We wish you to enjoy using our system.</p>");
            }catch (MessagingException me){
                me.printStackTrace();
            }
        }
        return "redirect:/admin/hr/users";
    }
    @PostMapping("/api/checkEmail")
    @ResponseBody
    public boolean checkEmail(@RequestParam("email") String email){
        User user = userRepository.findUserByEmail(email);
        if(user!=null) return false;
        return  true;
    }
    @PostMapping("/api/editUser")
    public String editUser(@RequestParam("userid") Long userid, @RequestParam(value = "name",required = false) String name, @RequestParam(value = "surname",required = false) String surname,
                           @RequestParam(value = "phone_number",required = false) String phone_number, @RequestParam(value = "email",required = true) String email,@RequestParam(value = "password",required = false) String password , @RequestParam(value = "department",required = false) Long department, @RequestParam(value = "role",required = false) Long role){
        User user = userRepository.findUserById(userid);
        user.setName(name);
        user.setSurname(surname);
        user.setPhone_number(phone_number);
        passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        if(password!=null) user.setPassword(encodedPassword);
        if(department!=null) user.setDepartment(departmentRepository.findDepartmentById(department));
        if(role!=null) user.setRoleSet(roleRepository.findRoleById(role));
        userRepository.saveAndFlush(user);
        if(!environment.getProperty("smtp.status").equals("OFF")) {

        }
        return "redirect:/admin/hr/users";
    }
    @GetMapping("/api/getUserRoles")
    @ResponseBody
    public Set<Role> userRoleSet(@RequestParam("userid") Long userid){
        User user = userRepository.findUserById(userid);
        return user.getRoleSet();
    }
    @PostMapping("/api/deleteUserRole")
    @ResponseBody
    public String deleteRole(@RequestParam("role") Long role, @RequestParam("userid") Long userid){
        User user = userRepository.findUserById(userid);
        if(user != null) {
            user.removeRole(roleRepository.findRoleById(role));
            userRepository.saveAndFlush(user);
            return "Success";
        }
        else return  "Error user not found";
    }

}
