package com.friedo.authentication.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface IJwtService {
    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String generateToken(UserDetails userDetails);

    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    long getExpirationTime();

    String buildToken(Map<String , Object> extraClaims, UserDetails userDetails, long expiration);

    boolean isTokenValid(String token, UserDetails userDetails);

//NB : Les interfaces n'acceptent que les méthodes de visibilité Public
}
