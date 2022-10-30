package com.perfect.hepdeskapp.department;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DepartmentController {
    final
    DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @RequestMapping("/admin/departments")
    public String manageDepartments(Model model){
        model.addAttribute("departments",departmentRepository.findAll());
        return "admin/admin_departments";
    }
}
