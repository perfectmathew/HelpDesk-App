package com.perfect.hepdeskapp.security;

import com.perfect.hepdeskapp.config.CustomErrorException;
import com.perfect.hepdeskapp.config.Utility;
import com.perfect.hepdeskapp.department.DepartmentRepository;
import com.perfect.hepdeskapp.notification.NotifyService;
import com.perfect.hepdeskapp.role.RoleRepository;
import com.perfect.hepdeskapp.user.User;
import com.perfect.hepdeskapp.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class AuthController {
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final Environment environment;
    final
    NotifyService notify;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public AuthController(DepartmentRepository departmentRepository,
                          UserRepository userRepository,
                          RoleRepository roleRepository, Environment environment, NotifyService notify) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.environment  = environment;
        this.notify = notify;
    }

    @RequestMapping("/auth")
    public String auth(@RequestParam(value = "error", defaultValue = "false") boolean error, @RequestParam(value = "logout", defaultValue = "false") boolean logout, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(error){
            model.addAttribute("authMsg","The email address you entered does not exist, or the wrong password was entered!");
        }
        if(logout){
            model.addAttribute("authLogoutMessage","Successfully logged out of your account!");
        }
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) return "auth";
        return "index";
    }
    @PostMapping("/signup")
    @ResponseBody
    public User createAccount(HttpServletRequest request, @RequestParam String name,
                              @RequestParam String surname,
                              @RequestParam String email,
                              @RequestParam String phone_number,
                              @RequestParam String password) throws IOException {
        if (userRepository.findUserByEmail(email) != null) throw new CustomErrorException("User with this email already exists in application!");
        if (password.length() < 6) throw new CustomErrorException("Password must have at least 6 letters!");
        passwordEncoder = new BCryptPasswordEncoder();
        User user = new User(name,surname,phone_number,email,passwordEncoder.encode(password),departmentRepository.findDepartmentByName("UNALLOCATED"),roleRepository.findRoleByName("USER"));
        user.setEnabled(false);
        userRepository.saveAndFlush(user);
        List<User> administratorsList = userRepository.findUserByRoleName("ADMIN");
        List<String> tempEmails = new ArrayList<>();
        for (User user_tmp : administratorsList){
            tempEmails.add(user_tmp.getEmail());
        }
        String[] cc = tempEmails.toArray(new String[0]);

        if(!Objects.requireNonNull(environment.getProperty("smtp.status")).equals("OFF")) {
            try {
                notify.sendEmail(user.getEmail(), "HelpDesk | Your account has been created", "<p>Welcome, "+user.getName()+"</p>"
                        + "<p>we are created account for you!</p>"
                        + "<p>Unfortunately, you have to wait for administrator approval.</p>"
                        + "<p>When this happens we will notify you.</p>"
                        + "<br>"
                        + "<p>Thank you for your trust. "
                        + "Helpdesk system.</p>");
                if (cc.length > 0){
                    notify.sendEmail(cc[0],cc,"HelpDesk | New account has been created!","<p>Welcome administrator, new account has been created on yours app.</p>" +
                            "</p>Account information:</p>" +
                            "<p>Name: "+ user.getName() + " "+ user.getSurname() + "</p>" +
                            "<p>E-mail: "+ user.getEmail() +"</p>" +
                            "<p>Phone number: "+ user.getPhone_number()+"</p>" +
                            "<p>We ask you to take action.</p>" +
                            "<p>From here you can <strong><a href='"+ Utility.getSiteURL(request)+"/admin/api/hr/users/"+user.getId()+"/approve'>approve</a></strong> the account or <strong><a href='"+Utility.getSiteURL(request)+"/admin/api/hr/users/"+user.getId()+"/reject'>reject</a></strong> thereby deleting this account.\n" +
                            "You can also do this in the <strong><a href='"+Utility.getSiteURL(request)+"/admin/hr/users'> admin panel</a></strong>.</p>" );
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return user;
    }
}
