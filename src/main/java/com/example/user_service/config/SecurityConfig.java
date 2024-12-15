package com.example.user_service.config;

import com.example.user_service.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF globally
                .authorizeHttpRequests(auth -> auth
                        // Permit access to public endpoints
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/api/users/register", // Regular registration
                                "/api/users/login",    // Regular login
                                "/api/users/google-login", // Google login
                                "/api/users/google-register" // Google auto-registration
                        ).permitAll()
                        .anyRequest().authenticated() // Protect all other endpoints
                )
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOrigins(List.of("*")); // Adjust as needed
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    return config;
                }))
                // Add the JWT authentication filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
                // OAuth2 login for Google authentication
//                .oauth2Login(oauth2 -> oauth2
//                        .defaultSuccessUrl("/api/users/google-login", true) // Redirect after successful Google login
//                        .failureUrl("/login?error=true") // Redirect on failure
//                );

        return http.build();
    }
}
