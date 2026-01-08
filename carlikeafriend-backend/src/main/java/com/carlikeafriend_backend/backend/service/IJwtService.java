package com.carlikeafriend_backend.backend.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface IJwtService {

    String extractUsername(String token);
    String generateToken(UserDetails userDetails);
    String generateToken(Map<String, Object> extractClaims, UserDetails userDatails, Long expirationTime);
    boolean isTokenValid(String token, UserDetails userDetails);
   <T> T extractClaim(String token, Function<Claims, T> claimsTFunction);
    Claims extractAllClaims(String token);

}
