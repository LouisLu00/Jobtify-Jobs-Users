package com.example.user_service.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.user_service.util.JwtKeyProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Extract token
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(JwtKeyProvider.getSecretKey()) // Validate with the same secret key
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String username = claims.getSubject(); // Extract the username (sub)

                if (username != null) {
                    // Create Authentication and set it in SecurityContext
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.emptyList()); // No roles/authorities
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

                request.setAttribute("claims", claims); // Attach claims for further use in controllers
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
