package com.perfect.hepdeskapp.department;
import com.perfect.hepdeskapp.config.EmailExistsException;
import com.perfect.hepdeskapp.role.RoleRepository;
import com.perfect.hepdeskapp.ticket.TicketRepository;
import com.perfect.hepdeskapp.user.User;
import com.perfect.hepdeskapp.user.UserDetail;
import com.perfect.hepdeskapp.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DepartmentController {
    final
    DepartmentRepository departmentRepository;
    final
    UserRepository userRepository;
    final
    RoleRepository roleRepository;
    final
    TicketRepository ticketRepository;

    public DepartmentController(DepartmentRepository departmentRepository, UserRepository userRepository, RoleRepository roleRepository, TicketRepository ticketRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.ticketRepository = ticketRepository;
    }
    @RequestMapping("/admin/api/departments")
    public String departments(Model model){
        model.addAttribute("departments",departmentRepository.findAll());
        return "admin/admin_departments";
    }
    @PostMapping("/admin/api/department/add")
    @ResponseBody
    public String createDepartment(@RequestParam("department_name") String department_name){
        if(departmentRepository.findDepartmentByName(department_name.toUpperCase()) != null)  { throw  new EmailExistsException("There is already a department with that name!"); }
        Department department = new Department();
        department.setName(department_name.toUpperCase());
        departmentRepository.saveAndFlush(department);
        return "Successful";
    }
    @DeleteMapping("/admin/api/department/delete")
    @ResponseBody
    public String deleteDepartment(@RequestParam("department_id") Long department_id){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        User cuerrentUser = userRepository.findUserByEmail(((UserDetail)principal).getUsername());
        if(cuerrentUser.getRoleSet().contains(roleRepository.findRoleByName("ADMIN"))){
            if(!ticketRepository.findTicketsByDepartment(department_id).isEmpty()) { throw  new EmailExistsException("This department has assigned tickets to itself!"); }
            if(!userRepository.findUserByDepartment(department_id).isEmpty()) { throw  new EmailExistsException("This department has assigned users to itself!"); }
            Department department = departmentRepository.findDepartmentById(department_id);
            departmentRepository.delete(department);
            return "Successful";
        }else {
            throw new EmailExistsException("You don't have permission for that action!");
        }
    }
    @GetMapping("/admin/api/getAllDepartments")
    @ResponseBody
    public List<Department> getAllDepartments(){
        return departmentRepository.findAll();
    }
    @PatchMapping("/admin/api/department/edit")
    @ResponseBody
    public Department editDepartment(@RequestParam("department_id") Long department_id, @RequestParam("department_name") String department_name){
        Department department = departmentRepository.findDepartmentById(department_id);
        if(departmentRepository.findDepartmentByName(department_name.toUpperCase()) != null)  { throw  new EmailExistsException("There is already a department with that name!"); }
        department.setName(department_name.toUpperCase());
        departmentRepository.saveAndFlush(department);
        return department;
    }
}
