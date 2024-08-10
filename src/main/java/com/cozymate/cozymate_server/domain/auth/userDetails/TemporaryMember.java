package com.cozymate.cozymate_server.domain.auth.userDetails;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class TemporaryMember implements UserDetails {
    @NonNull
    String clientId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return clientId;
    }

}
