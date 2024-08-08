package com.cozymate.cozymate_server.domain.auth.utils;

import com.cozymate.cozymate_server.domain.auth.enums.TokenType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUtil {

    public final static String TOKEN_PREFIX = "Bearer ";

    public final static String TOKEN_TYPE_CLAIM_NAME = "tokenType";
    public static final String HEADER_ATTRIBUTE_NAME_AUTHORIZATION = "Authorization";

    @Value("${jwt.custom.secretKey}")
    private String SECRET_KEY;

    @Getter
//    @Value("${jwt.access-token.expire-length}")
//  주의!!! 테스트용!! access 토큰 6개월!!!
    private static final Long ACCESS_EXPIRATION = 15778476000L;
    @Getter
    @Value("${jwt.refresh-token.expire-length}")
    private Long REFRESH_EXPIRATION;

    @Getter
    @Value("${jwt.access-token.expire-length}")
    private Long TEMPORARY_EXPIRATION;


    //JWT 토큰에서 subject를 추출하여 사용자 id를 반환
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get(TOKEN_TYPE_CLAIM_NAME, String.class));
    }

    //토큰에서 특정 클레임을 추출
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //사용자 소셜로그인 정보를 바탕으로 임시 토큰을 생성
    public String generateTemporaryToken(UserDetails userDetails) {
        return buildToken(TokenType.TEMPORARY, userDetails, TEMPORARY_EXPIRATION);
    }

    //사용자 정보를 바탕으로 Access 토큰을 생성
    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(TokenType.ACCESS, userDetails, ACCESS_EXPIRATION);
    }


    //사용자 정보를 바탕으로 리프레시 토큰을 생성
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(TokenType.REFRESH, userDetails, REFRESH_EXPIRATION);
    }

    public void validateToken(String token) {
        Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token);
    }

    public Boolean isTemporaryToken(String token) {
        String tokenTypeName =
                Jwts.parserBuilder()
                        .setSigningKey(getSignInKey()) // jwtSecret은 토큰 서명에 사용되는 비밀 키
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .get(TOKEN_TYPE_CLAIM_NAME, String.class);

        return (TokenType.valueOf(tokenTypeName).equals(TokenType.TEMPORARY));
    }


    // 주어진 클레임, 사용자 정보, 그리고 만료 시간을 바탕으로 JWT 토큰을 생성
    private String buildToken(TokenType tokenType, UserDetails userDetails, Long expiration) {
        return Jwts
                .builder()
                .setSubject(userDetails.getUsername())
                .claim(TOKEN_TYPE_CLAIM_NAME, tokenType.toString())
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    //토큰이 유효한지 확인
    public boolean isTokenValid(String token, String userName) {
        return (userName.equals(extractUserName(token))) && !isTokenExpired(token);
    }

    //토큰이 만료되었는지 확인
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    //토큰에서 만료 시간을 추출
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //토큰에서 모든 클레임을 추출
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //서명 키 반환, 토큰 생성하고 검증할 때 사용
    private SecretKey getSignInKey() {
        String key = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
