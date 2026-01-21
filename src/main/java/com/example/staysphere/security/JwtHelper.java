package com.example.staysphere.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException; 
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import org.springframework.security.core.userdetails.UserDetails;
@Component
@Slf4j
public class JwtHelper{
    
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expirationMs}")
    private Long expirationMs;

    public String generateToken(UserDetails userDetails){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);
        javax.crypto.SecretKey key = new javax.crypto.spec.SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS512.getJcaName());
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUserNameFromToken(String token){
        javax.crypto.SecretKey key = new javax.crypto.spec.SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS512.getJcaName());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token){
        try{
            javax.crypto.SecretKey key = new javax.crypto.spec.SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS512.getJcaName());
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);  
            return true;
        }catch (MalformedJwtException ex){
            System.out.println("Invalid JWT token");
        }catch(ExpiredJwtException ex){
            log.info("Expired JWT token");
        }catch(UnsupportedJwtException ex){
            log.info("Unsupported JWT token");
        }catch(IllegalArgumentException ex){
            log.info("JWT claims string is empty"); 
        }
        return false;
    }


}
