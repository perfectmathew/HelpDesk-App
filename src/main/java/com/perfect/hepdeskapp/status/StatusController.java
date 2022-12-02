package com.perfect.hepdeskapp.status;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class StatusController {
    final
    StatusRepository statusRepository;

    public StatusController(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }
    @GetMapping("/manager/api/getAllStatuses")
    @ResponseBody
    public List<Status> getAllStatuses(){
        return statusRepository.findAll();
    }
}
