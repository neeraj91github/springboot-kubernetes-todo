package com.neeraj.todo_backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTUtil {

    private static final long EXPIRATION_TIME = 1000L * 60 * 60 * 10; // 10 hours

    private final SecretKey key;
    private final EncryptionUtil encryptionUtil;

    public JWTUtil(@Value("${app.jwt.secret}") String jwtSecret, EncryptionUtil encryptionUtil) {
        try {
            this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "app.jwt.secret must be a Base64-encoded key of at least 256 bits", e);
        }
        this.encryptionUtil = encryptionUtil;
    }

    public String generateToken(Long userId, String email) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", encryptionUtil.encrypt(email))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    public String getEmailFromToken(String token) {
        String encryptedEmail = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
        return encryptionUtil.decrypt(encryptedEmail);
    }
}
