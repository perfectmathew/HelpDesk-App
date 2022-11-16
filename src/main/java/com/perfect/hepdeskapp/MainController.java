package com.perfect.hepdeskapp;

import com.perfect.hepdeskapp.user.User;
import com.perfect.hepdeskapp.user.UserDetail;
import com.perfect.hepdeskapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class MainController {
    @Autowired
    UserRepository userRepository;
    @RequestMapping("/")
    public String index(Model model){
        return "index";
    }
    @RequestMapping("/home")
    public String index(){
        return "index";
    }

    @RequestMapping("/404")
    public String error404() { return "error"; }
    @RequestMapping("/direct")
    public String directRedirect() {
        return "redirect:/home";
    }

}
