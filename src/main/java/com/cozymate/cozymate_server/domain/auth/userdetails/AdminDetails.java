package com.cozymate.cozymate_server.domain.auth.userdetails;

import com.cozymate.cozymate_server.domain.member.enums.Role;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record AdminDetails() implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Role.ADMIN.getAuthorities();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "admin";
    }
}