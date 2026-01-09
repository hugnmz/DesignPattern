package com.hungnguyen.coffee.restapitjava.service;

import com.hungnguyen.coffee.restapitjava.utils.TokenType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


public interface JWTService {
    String generateToken(UserDetails userDetails);

    String resetToken(UserDetails userDetails);

    String extractUsername(String token, TokenType tokenType);
    boolean isValid(String token, UserDetails userDetails, TokenType tokenType);

    String generateRefrestToken(UserDetails user);
}
