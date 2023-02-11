package com.perfect.hepdeskapp.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    final
    UserRepository userRepository;
    final
    BCryptPasswordEncoder bCryptPasswordEncoder;


    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Page<User> userPagination(String role_name ,int page_num){
        Pageable pageable = PageRequest.of(page_num-1,10);
        return userRepository.findUserByRoleNamePageable(role_name,pageable);
    }

    public String encryptPassword(String password){
        return bCryptPasswordEncoder.encode(password);
    }
}
