package com.perfect.hepdeskapp.user;

import com.perfect.hepdeskapp.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    public UserDetailService(){

    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username);
        if (user == null){
            throw new UsernameNotFoundException("User not found!");
        }
        return new UserDetail(user);
    }

    public void updateResetPasswordToken(String token, String email){
        User user = userRepository.findUserByEmail(email);
        if(user != null){
            user.setPassword_token(token);
            userRepository.saveAndFlush(user);
        }else {
            throw new UsernameNotFoundException("User not found!");
        }
    }
    public User getUserByPasswordToken(String token){
        return userRepository.findUserByPassword_token(token);
    }

    public void updateUserPassword(User user, String newPassword){
        BCryptPasswordEncoder Encoder = new BCryptPasswordEncoder();
        String passwordEncoded =  Encoder.encode(newPassword);
        user.setPassword(passwordEncoded);
        user.setPassword_token(null);
        userRepository.saveAndFlush(user);
    }
}
