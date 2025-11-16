package com.partyst.app.partystapp.auth.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    
    @Value("${application.security.jwt.expiration}")
    private String jwtExpiration;
    
    @Value("${application.security.jwt.refresh-token.expiration}")
    private String refreshExpiration;

    public String extractUserName(String token) {
        Claims jwtToken = Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
        return jwtToken.getSubject();
    }

    public boolean isTokenValid(String token, User user) {
        String username = extractUserName(token);
        return(username.equals(user.getEmail())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration( String token) {
        Claims jwtToken = Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
        return jwtToken.getExpiration();
    }

    public String generateToken(User user){
        return buildToken(user, Long.parseLong(jwtExpiration));
    }

    public String generateRefreshToken(User user){
        return buildToken(user, Long.parseLong(refreshExpiration));
    }

    
    private String buildToken(User user, Long expiration) {
        return Jwts.builder()
            .id(user.getUserId().toString())
            .claim("name", user.getName())
            .claim("email", user.getEmail())
            .subject(user.getEmail())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey())
            .compact();    
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
