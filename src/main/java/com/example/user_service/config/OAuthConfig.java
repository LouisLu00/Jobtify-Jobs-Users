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
        String redirectUri = "https://ec2-13-58-61-231.us-east-2.compute.amazonaws.com/login/oauth2/code/google";

        System.out.println("Client ID: " + clientId);
        System.out.println("Client Secret: " + clientSecret);

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/error").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/users/google-login", true)
                        .failureUrl("/login?error=true"));

        return http.build();
    }
}


