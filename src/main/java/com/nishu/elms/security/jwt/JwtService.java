package com.nishu.elms.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;


    //create signing key
    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    //generate jwt token
    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis()+jwtExpiration
                )
                        ).signWith(getSigningKey()).compact();
    }

    //extract any clain from token
    public <T> T extractClaim(
            String token,
            Function<Claims,T> claimsResolver
    ){
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //extract all claims
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //extract username/email
    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }

    //extract expiration date
    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    //check if token is expired
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

//    validate token
    public boolean isTokenValid(String token,UserDetails userDetails){
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }
}
