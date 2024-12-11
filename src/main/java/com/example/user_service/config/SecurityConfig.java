package com.example.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final Environment environment;

    public SecurityConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())  // Disables CSRF globally
//
//                // Permit all requests temporarily for debugging
//                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
//
//        return http.build();
//    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        String clientId = EnvConfig.get("GOOGLE_CLIENT_ID");
//        String clientSecret = EnvConfig.get("GOOGLE_CLIENT_SECRET");
//        String redirectUri = "https://ec2-13-58-61-231.us-east-2.compute.amazonaws.com/login/oauth2/code/google";
//
//        System.out.println("Client ID: " + clientId);
//        System.out.println("Client Secret: " + clientSecret);

        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection globally
                .authorizeHttpRequests(auth -> auth
                        // Permit access to the registration and login endpoints
                        .requestMatchers("/api/users/register", "/api/users/login", "/api/users/google-login").permitAll()
                        // Permit other endpoints as necessary, e.g., for health checks
                        .requestMatchers("/", "/error").permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/users/google-login", true) // Redirect to this after successful Google login
                        .failureUrl("/login?error=true")); // Redirect on failure

        return http.build();
    }

}
