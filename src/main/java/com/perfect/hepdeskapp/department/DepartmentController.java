package com.perfect.hepdeskapp.department;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class DepartmentController {
    @Autowired
    DepartmentRepository departmentRepository;
    @PostMapping("/admin/department/add")
    @ResponseBody
    public String createDepartment(@RequestParam("department_name") String department_name){
        Department department = new Department();
        department.setName(department_name);
        departmentRepository.saveAndFlush(department);
        return "Successful";
    }
    @PostMapping("/admin/department/delete")
    @ResponseBody
    public String deleteDepartment(@RequestParam("department_id") Long department_id){
        Department department = departmentRepository.findDepartmentById(department_id);
        departmentRepository.delete(department);
        return "Successful";
    }
    @GetMapping("/admin/getAllDepartments")
    @ResponseBody
    public List<Department> getAllDepartments(){
        return departmentRepository.findAll();
    }
    @PostMapping("/admin/department/edit")
    @ResponseBody
    public Department editDepartment(@RequestParam("department_id") Long department_id, @RequestParam("department_name") String department_name){
        Department department = departmentRepository.findDepartmentById(department_id);
        department.setName(department_name);
        departmentRepository.saveAndFlush(department);
        return department;
    }
}
