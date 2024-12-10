package com.example.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class OAuthConfig {
    @Bean(name = "oauthSecurityFilterChain")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String clientId = EnvConfig.get("GOOGLE_CLIENT_ID");
        String clientSecret = EnvConfig.get("GOOGLE_CLIENT_SECRET");
        String redirectUri = "http://localhost:8080/login/oauth2/code/google";

        System.out.println("Client ID: " + clientId);
        System.out.println("Client Secret: " + clientSecret);

        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF for stateless authentication
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/error").permitAll()  // Public endpoints
                        .anyRequest().authenticated())                       // Secure all others
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/home", true)                     // Redirect after success
                        .failureUrl("/login?error=true"));                    // Redirect after failure

        return http.build();
    }
}


