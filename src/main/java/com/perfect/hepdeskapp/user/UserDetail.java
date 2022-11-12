package com.perfect.hepdeskapp.user;

import com.perfect.hepdeskapp.role.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class UserDetail implements UserDetails {
    private User user;
    public UserDetail(User user){
        this.user = user;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = user.getRoleSet();
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        for(Role rola : roles){
            authorityList.add(new SimpleGrantedAuthority(rola.getName()));
        }
        return authorityList;
    }

    public Long gtId() {return user.getId();}
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }


}
