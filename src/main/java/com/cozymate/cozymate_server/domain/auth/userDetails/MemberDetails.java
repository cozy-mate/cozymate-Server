package com.cozymate.cozymate_server.domain.auth.userDetails;

import com.cozymate.cozymate_server.domain.member.Member;

import java.util.Collection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public record MemberDetails(Member member) implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return member.getRole().getAuthorities();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return member.getClientId();
    }
}
