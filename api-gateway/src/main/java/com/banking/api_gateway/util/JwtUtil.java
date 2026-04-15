package com.banking.api_gateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;



    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }



    public boolean isTokenValid(String token){
        try {
            getClaims(token).getSubject();
            return true;
        }catch (Exception e){
            return false;
        }

    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token){
        return getClaims(token).getSubject();
    }

}
