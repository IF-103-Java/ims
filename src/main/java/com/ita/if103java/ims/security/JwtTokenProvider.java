package com.ita.if103java.ims.security;

import com.ita.if103java.ims.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${security.jwt.token.secretKey}")
    private String secretKey;

    @Value("${security.jwt.token.expiredTime}")
    private long expiredTime;

    public String createToken(String username, Role role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", new SimpleGrantedAuthority(role.name()));

        Date now = new Date();
        Date validatedTime = new Date(now.getTime() + expiredTime);

        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validatedTime)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }
}
