package com.perfect.hepdeskapp.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {
    @Autowired
    RoleRepository roleRepository;


    public Object getRoleById(Long id){
        Optional<Role> r = roleRepository.findById(id);
        return r;
    }
}
