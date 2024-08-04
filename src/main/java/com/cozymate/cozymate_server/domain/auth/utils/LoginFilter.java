package com.cozymate.cozymate_server.domain.auth.utils;

import com.cozymate.cozymate_server.domain.auth.Token;
import com.cozymate.cozymate_server.domain.auth.repository.TokenRepository;
import com.cozymate.cozymate_server.domain.auth.utils.jwt.JwtUtil;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String clientId = obtainUsername(request).toLowerCase();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(clientId,
                null);

        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시 jwt 발급
    @Override
    @Transactional
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {

        String clientId = authentication.getName().toLowerCase();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();
        Member member = memberRepository.findByClientId(clientId).orElseThrow();
        MemberDetails memberDetails = new MemberDetails(member);

        String access_token = jwtUtil.generateAccessToken(memberDetails);
        String refresh_token = jwtUtil.generateRefreshToken(memberDetails);

        Token newToken = new Token(clientId,refresh_token);

        tokenRepository.save(newToken);

        //로그인 성공 시 memberId와 clientId를 httpResponse body에 담아서 전달
        String result = objectMapper.writeValueAsString(memberDetails);

        //응답 설정
        response.setHeader("Authorization", access_token);
        response.getWriter().write(result);
        response.addCookie(createCookie("refresh_token", refresh_token));
        response.setStatus(HttpStatus.OK.value()); //성공하면 200 상태코드
    }

    //로그인 실패시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401
        response.getWriter().write("login fail");
    }

    private Cookie createCookie(String key, String value) { //(key값, JWT)
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true); // javascript에서 접근 불가
        cookie.setSecure(true); //https 사용시 추가
        cookie.setMaxAge(24 * 60 * 60); //쿠키 생명 주기
        cookie.setPath("/");
        return cookie;
    }
}
