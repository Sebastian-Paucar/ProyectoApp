package com.example.chatapp.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "a3f7b9d3c4f3e8d24f0b2c4a9e7f1d6c8a9e2b7d3c4f5a6b7c8d9e0f1a2b3c4d"; // Cambia esto por una clave segura

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hora de expiración
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            // Manejar errores en la firma, la estructura del token o el parsing
            return true; // Si ocurre una excepción, considera el token como inválido
        }
    }

    public static String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
