package com.perfect.hepdeskapp;

import com.perfect.hepdeskapp.department.DepartmentRepository;
import com.perfect.hepdeskapp.role.RoleRepository;
import com.perfect.hepdeskapp.status.StatusRepository;
import com.perfect.hepdeskapp.ticket.Ticket;
import com.perfect.hepdeskapp.ticket.TicketRepository;
import com.perfect.hepdeskapp.user.User;
import com.perfect.hepdeskapp.user.UserDetail;
import com.perfect.hepdeskapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
public class MainController {
    final
    UserRepository userRepository;
    final
    StatusRepository statusRepository;
    final
    DepartmentRepository departmentRepository;
    final
    TicketRepository ticketRepository;
    private final RoleRepository roleRepository;

    public MainController(UserRepository userRepository, StatusRepository statusRepository, DepartmentRepository departmentRepository, TicketRepository ticketRepository,
                          RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.statusRepository = statusRepository;
        this.departmentRepository = departmentRepository;
        this.ticketRepository = ticketRepository;
        this.roleRepository = roleRepository;
    }

    @RequestMapping("/")
    public String index(Model model){
        model.addAttribute("departmentList",departmentRepository.findAll());
        return "index";
    }
    @RequestMapping("/home")
    public String home(Model model){
        model.addAttribute("departmentList",departmentRepository.findAll());
        return "index";
    }

    @RequestMapping("/404")
    public String error404() { return "error"; }
    @RequestMapping("/direct")
    public String directRedirect() {
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        User user = userRepository.findUserByEmail(((UserDetail)principal).getUsername());
        if(user.getRoleSet().contains(roleRepository.findRoleByName("ADMIN"))) return "redirect:/admin/api/dashboard";
        if(user.getRoleSet().contains(roleRepository.findRoleByName("DEPARTMENT_BOSS"))) return "redirect:/manager/api/dashboard";
        return "redirect:/home";
    }

    // DASHBOARD REQUESTS ///

    @RequestMapping(value = "/admin/api/dashboard")
    public String adminPanel(Model model){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        User user = userRepository.findUserByEmail(((UserDetail)principal).getUsername());
        model.addAttribute("loggedUser",user);
        model.addAttribute("mostTroubleMakers",userRepository.TopTroubleMakers(PageRequest.of(0,10)));
        model.addAttribute("totalTicketCount",ticketRepository.findAll().stream().count());
        model.addAttribute("ticketByNewest",ticketRepository.findAllByStatus("NEW").stream().count());
        model.addAttribute("ticketByDone",ticketRepository.findAllByStatus("DONE").stream().count());
        model.addAttribute("ticketByUndone",ticketRepository.findAllByStatusNotContainingArchivedAndDone().stream().count());
        model.addAttribute("usersCount",userRepository.findAll().stream().count());
        model.addAttribute("departmentsCount",departmentRepository.findAll().stream().count());
        return "admin/dashboard";
    }
    @RequestMapping(value = "/manager/api/dashboard")
    public String managerPanel(Model model){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        User user = userRepository.findUserByEmail(((UserDetail)principal).getUsername());
        model.addAttribute("loggedUser",user);
        model.addAttribute("totalTicketCount",ticketRepository.findTicketsByDepartment(user.getDepartment().getId()).stream().count());
        model.addAttribute("ticketByNewest",ticketRepository.findTicketsByDepartmentAndStatusNotOrder(user.getDepartment(), statusRepository.findStatusByStatus("NEW")).stream().count());
        model.addAttribute("ticketByDone",ticketRepository.findTicketsByDepartmentAndStatusNotOrder(user.getDepartment(),statusRepository.findStatusByStatus("DONE")).stream().count());
        model.addAttribute("ticketByUndone",ticketRepository.findAllByDepartmentAndStatusNotContainingArchivedAndDone(user.getDepartment()).stream().count());
        return "manager/dashboard";
    }
    @RequestMapping("/register")
    public String register(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) return "register";
        return "redirect:/home";
    }
}
