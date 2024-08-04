package com.example.chatapp.service;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String SECRET_KEY = "a3f7b9d3c4f3e8d24f0b2c4a9e7f1d6c8a9e2b7d3c4f5a6b7c8d9e0f1a2b3c4d"; // Debes usar una clave segura
    private final TokenBlacklistService tokenBlacklistService;

    public JwtAuthenticationFilter(TokenBlacklistService tokenBlacklistService) {
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Eliminar "Bearer " del encabezado

            System.out.println("Received Token: " + token);

            if (tokenBlacklistService.isBlacklisted(token)) {
                System.out.println("Token is blacklisted");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            try {
                String username = Jwts.parser()
                        .setSigningKey(SECRET_KEY)
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject();

                System.out.println("Parsed Username: " + username);

                if (username != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("Authentication set for user: " + username);
                }
            } catch (Exception e) {
                System.out.println("JWT token validation failed: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

}

