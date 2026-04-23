package com.banking.user_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    int count=0;

    public int getCount(String methodName) {
        setCount();
        System.out.println(methodName+": has called getCount");
        return count;
    }

    public void setCount() {
        this.count = count++;
    }

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;


    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email,Integer userId){
        return Jwts.builder()
                .signWith(getSigningKey())
                .subject(email)
                .claim("userId",userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+expiration))
                .compact();
    }

    private Integer extractUserId(String token){
        return getClaims(token).get("userId",Integer.class);
    }

    private Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token){
        try {
            getClaims(token).getSubject();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public String extractEmail(String token){
        System.out.println(count+1+"inside extractEmail");
        getCount("extractEmail");
        return getClaims(token).getSubject();
    }

}
