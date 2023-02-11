package com.perfect.hepdeskapp.department;
import com.perfect.hepdeskapp.config.CustomErrorException;
import com.perfect.hepdeskapp.role.RoleRepository;
import com.perfect.hepdeskapp.ticket.TicketRepository;
import com.perfect.hepdeskapp.user.User;
import com.perfect.hepdeskapp.user.UserDetail;
import com.perfect.hepdeskapp.user.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    public String departments(Model model){
        model.addAttribute("departments",departmentRepository.findAll());
        return "admin/admin_departments";
    }

    @PostMapping("/admin/api/department/add")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String createDepartment(@RequestParam("department_name") String department_name){
        if(departmentRepository.findDepartmentByName(department_name.toUpperCase()) != null)  { throw  new CustomErrorException("There is already a department with that name!"); }
        if (department_name.equals("") || department_name.equals(" ")) throw new CustomErrorException("You can't create a department without a name!");
        Department department = new Department();
        department.setName(department_name.toUpperCase());
        departmentRepository.saveAndFlush(department);
        return "Successful";
    }
    @DeleteMapping("/admin/api/department/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String deleteDepartment(@RequestParam("department_id") Long department_id){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        User cuerrentUser = userRepository.findUserByEmail(((UserDetail)principal).getUsername());
        if(cuerrentUser.getRoleSet().contains(roleRepository.findRoleByName("ADMIN"))){
            if(!ticketRepository.findTicketsByDepartment(department_id).isEmpty())  throw  new CustomErrorException("This department has assigned tickets to itself!");
            if(!userRepository.findUserByDepartment(department_id).isEmpty())  throw  new CustomErrorException("This department has assigned users to itself!");
            Department department = departmentRepository.findDepartmentById(department_id);
            departmentRepository.delete(department);
            return "Successful";
        }else {
            throw new CustomErrorException("You don't have permission for that action!");
        }
    }
    @PatchMapping("/admin/api/department/edit")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Department editDepartment(@RequestParam("department_id") Long department_id, @RequestParam("department_name") String department_name){
        Object principal = SecurityContextHolder. getContext(). getAuthentication(). getPrincipal();
        User cuerrentUser = userRepository.findUserByEmail(((UserDetail)principal).getUsername());
        if(cuerrentUser.getRoleSet().contains(roleRepository.findRoleByName("ADMIN"))) {
            Department department = departmentRepository.findDepartmentById(department_id);
            if (departmentRepository.findDepartmentByName(department_name.toUpperCase()) != null) {
                throw new CustomErrorException("There is already a department with that name!");
            }
            if (department_name.equals("") || department_name.equals(" ")) {
                throw new CustomErrorException("You can't create a department without a name!");
            }
            department.setName(department_name.toUpperCase());
            departmentRepository.saveAndFlush(department);
            return department;
        }else {
            throw new CustomErrorException("You don't have permission for that action!");
        }
    }
    @GetMapping("/admin/api/getAllDepartments")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public List<Department> getAllDepartments(){
        return departmentRepository.findAll();
    }

}
