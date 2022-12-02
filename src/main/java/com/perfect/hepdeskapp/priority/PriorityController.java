package com.perfect.hepdeskapp.priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PriorityController {
    final PriorityRepository priorityRepository;

    public PriorityController(PriorityRepository priorityRepository) {
        this.priorityRepository = priorityRepository;
    }

    @GetMapping("/manager/api/getAllPriorities")
    @ResponseBody
    public List<Priority> getAllPriorities(){
        return  priorityRepository.findAll();
    }
}
