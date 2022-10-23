package com.perfect.hepdeskapp.user;

import com.perfect.hepdeskapp.department.Department;
import com.perfect.hepdeskapp.department.DepartmentRepository;
import com.perfect.hepdeskapp.role.Role;
import com.perfect.hepdeskapp.role.RoleRepository;
import com.perfect.hepdeskapp.ticket.Ticket;
import com.perfect.hepdeskapp.ticket.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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
        return "admin/admin_hr";
    }
    @RequestMapping("/admin/hr/workers")
    public String adminHrWorkersPanel(Model model){
        model.addAttribute("departments",departmentRepository.findAll());
        model.addAttribute("roles",roleRepository.findAll());
        model.addAttribute("users",userRepository.findUserByRoleName("WORKER"));
        return "admin/admin_hr";
    }
    @RequestMapping("/admin/hr/administrators")
    public String adminHrAdministratorsPanel(Model model){
        model.addAttribute("departments",departmentRepository.findAll());
        model.addAttribute("roles",roleRepository.findAll());
        model.addAttribute("users",userRepository.findUserByRoleName("ADMIN"));
        return "admin/admin_hr";
    }
    @RequestMapping("/admin/hr/users")
    public String adminHrUsersPanel(Model model){
        model.addAttribute("departments",departmentRepository.findAll());
        model.addAttribute("roles",roleRepository.findAll());
        model.addAttribute("users",userRepository.findUserByRoleName("USER"));
        return "admin/admin_hr";
    }
    // API SECTION

    @PostMapping("/api/addUser")
    public String addNewUser(@RequestParam("name") String name, @RequestParam("surname") String surname,
    @RequestParam("phone_number") String phone_number, @RequestParam("email") String email,@RequestParam("password") String password , @RequestParam("department") Long department, @RequestParam("role") Long role){
        Department departmentObject = departmentRepository.findDepartmentById(department);
        Role roleObject = roleRepository.findRoleById(role);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(name,surname,phone_number,email,encodedPassword,departmentObject,roleObject);
        userRepository.saveAndFlush(user);
        return "redirect:/admin/hr/users";
    }
    @PostMapping("/api/checkEmail")
    @ResponseBody
    public boolean checkEmail(@RequestParam("email") String email){
        User user = userRepository.findUserByEmail(email);

        if(user!=null){
            return false;
        }
        return  true;
    }
    @PostMapping("/api/editUser")
    public String editUser(){

        return "redirect:/admin/hr/users";
    }

}
