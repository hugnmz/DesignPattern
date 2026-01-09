package com.hungnguyen.coffee.restapitjava.service.impl;

import com.hungnguyen.coffee.restapitjava.service.JWTService;
import com.hungnguyen.coffee.restapitjava.utils.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoder;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.hungnguyen.coffee.restapitjava.utils.TokenType.REFRESH_TOKEN;
import static com.hungnguyen.coffee.restapitjava.utils.TokenType.RESET_TOKEN;

@Service
public class JWTServiceImpl implements JWTService {

    @Value("{jwt.timeout}")
    private String expiryTime;

    @Value("{jwt.expiryDate}")
    private String expiryDate;

    @Value("{jwt.secretKey}")
    private String secretKey;

    @Value("{jwt.refreshKey}")
    private String refreshKey;
    @Value("{jwt.resetKey}")
    private String resetKey;

    @Override
    public String generateToken(UserDetails userDetails) {
        // TODO: ...
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public String resetToken(UserDetails userDetails) {
       return generateResetToken(new HashMap<>(), userDetails);
    }

    @Override
    public String extractUsername(String token, TokenType tokenType) {
        return extractClaim(token, tokenType, Claims::getSubject);
    }

    @Override
    public boolean isValid(String token, UserDetails userDetails, TokenType tokenType) {
        final String username = extractUsername(token, tokenType);


        return username.equals(userDetails.getUsername()) && !isTokenExpired(token, tokenType);
    }

    private boolean isTokenExpired(String token, TokenType tokenType) {
        return extractExpiration(token, tokenType).before(new Date());
    }

    private Date extractExpiration(String token, TokenType tokenType) {
        return extractClaim(token,tokenType, Claims::getExpiration);
    }

    @Override
    public String generateRefrestToken(UserDetails user) {
         return generateRefreshToken(new HashMap<>(), user);
    }

    private String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims) // thong tin trong phan payload
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() + Long.parseLong(expiryTime))) // token ton tai 1 days
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey(TokenType type) {

        switch (type){
            case ACCESS_TOKEN ->{   return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));}
            case REFRESH_TOKEN ->{return
                Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
            }
            case RESET_TOKEN ->{return
                Keys.hmacShaKeyFor(Decoders.BASE64.decode(resetKey));
            }
            default -> throw new IllegalArgumentException("Invalid token type") ;
        }

    }

    private String generateResetToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims) // thong tin trong phan payload
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() + Long.parseLong(expiryTime))) // token ton tai 1 days
                .signWith(getKey(RESET_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    private <T> T extractClaim(String token, TokenType type,Function<Claims, T> claimsResolver) {
        final Claims claims = extraAllClaim(token, type);

        return claimsResolver.apply(claims);
    }

    private Claims extraAllClaim(String token, TokenType type) {
        return Jwts.parserBuilder().setSigningKey(getKey(type)).build().parseClaimsJws(token).getBody();
    }

    private String generateRefreshToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims) // thong tin trong phan payload
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() + 1000 *60 * 60 *24 + expiryDate
                )) // token ton tai 1 days
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
