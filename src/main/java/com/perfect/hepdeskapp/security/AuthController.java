package com.perfect.hepdeskapp.security;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    @RequestMapping("/auth")
    public String auth(String error, String logout, RedirectAttributes redirectAttributes){
        if(error!= null){
            redirectAttributes.addFlashAttribute("error","Podany adres email nie istnieje, lub zostało wprowadzone błędne hasło!");
        }
        if(logout != null){
            redirectAttributes.addFlashAttribute("success","Pomyślnie wylogowano z konta!");
        }
        return "auth";
    }
}
