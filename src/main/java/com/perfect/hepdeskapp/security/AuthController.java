package com.perfect.hepdeskapp.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    @RequestMapping("/auth")
    public String auth(@RequestParam(value = "error", defaultValue = "false") boolean error, @RequestParam(value = "logout", defaultValue = "false") boolean logout, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(error == true){
            model.addAttribute("authMsg","The email address you entered does not exist, or the wrong password was entered!");
        }
        if(logout == true){
            model.addAttribute("authLogoutMessage","Successfully logged out of your account!");
        }
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) return "auth";
        return "index";
    }
}
