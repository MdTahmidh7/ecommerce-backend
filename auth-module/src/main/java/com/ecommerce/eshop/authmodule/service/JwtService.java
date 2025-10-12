package com.ecommerce.eshop.authmodule.service; // Updated package

import com.ecommerce.eshop.authmodule.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {

    // These values will be configured in the main application's application.properties
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationMs}")
    private long jwtExpiration;

    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        // Check if the token was valid and claims were successfully extracted
        if (claims == null) {
            return null;
        }
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token has expired: {}", e.getMessage());
            return null;
        } catch (SignatureException | MalformedJwtException | IllegalArgumentException e) {
            log.warn("Invalid/Malformed Token: {}", e.getMessage());
            return null;
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String phoneNumber = extractPhoneNumber(token);
        return (phoneNumber.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // For generating token with extra claims (like roles, user ID)
    public String generateToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDetails.getId());
        claims.put("phoneNumber", userDetails.getUsername());
        claims.put("name", userDetails.getName());
        claims.put("roles", userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())
        );

        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)              // Changed from setClaims()
                .subject(subject)            // Changed from setSubject()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey())    // No change, but ensure Key is correct
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
