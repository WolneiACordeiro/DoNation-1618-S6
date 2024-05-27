package com.fatec.donation.jwt;

import com.fatec.donation.domain.entity.AccessToken;
import com.fatec.donation.domain.entity.User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final SecretKeyGenerator keyGenerator;

    public AccessToken generateToken(User user) {
        SecretKey key = keyGenerator.getKey();
        Date expirationDate = generateExpirationDate();
        Map<String, Object> claims = generateTokenClaims(user);
        String token = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(user.getEmail())
                .setExpiration(expirationDate)
                .addClaims(claims)
                .compact();

        String encryptedToken = Jwts.builder()
                .setSubject(token)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return new AccessToken(encryptedToken);
    }

    public static String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Date generateExpirationDate() {
        int expirationMinutes = 60;
        LocalDateTime now = LocalDateTime.now().plusMinutes(expirationMinutes);
        return Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Map<String, Object> generateTokenClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("roles", user.getRoles());
        return claims;
    }

    public String getEmailFromToken(String encryptedTokenJwt) {
        try {
            SecretKey key = keyGenerator.getKey();

            String innerToken = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(encryptedTokenJwt)
                    .getBody()
                    .getSubject();

            Jws<Claims> jwsClaims = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(innerToken);
            Claims claims = jwsClaims.getBody();

            return claims.getSubject();
        } catch (JwtException e) {
            throw new InvalidTokenException(e.getMessage());
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid token signature");
        }
    }

}
