package com.example.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
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

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // Disable CSRF protection globally
//                .authorizeHttpRequests(auth -> auth
//                        // Permit access to the Swagger endpoints
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
//                        // Permit access to the registration and login endpoints
//                        .requestMatchers("/api/users/register", "/api/users/login", "/api/users/google-login").permitAll()
//                        // Permit other endpoints as necessary, e.g., for health checks
//                        .requestMatchers("/", "/error").permitAll()
//                        // All other requests require authentication
//                        .anyRequest().authenticated())
//                .oauth2Login(oauth2 -> oauth2
//                        .defaultSuccessUrl("/api/users/google-login", true) // Redirect to this after successful Google login
//                        .failureUrl("/login?error=true")); // Redirect on failure
//
//        return http.build();
//    }
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable()) // Disable CSRF protection globally
            .authorizeHttpRequests(auth -> auth
                    // Permit access to these endpoints
                    .requestMatchers(
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/v3/api-docs.yaml",
                            "/api/users/register",
                            "/api/users/login",
                            "/api/users/google-login",// Add new endpoint if necessary
                            "/api/users/{id}" // Ensure this endpoint is also permitted
                    ).permitAll()
                    .anyRequest().authenticated())
            .cors(cors -> cors.configurationSource(request -> {
                var config = new org.springframework.web.cors.CorsConfiguration();
                config.setAllowedOrigins(List.of("*")); // Adjust allowed origins as needed
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                // config.setAllowCredentials(true);
                return config;
            }));
//            .oauth2Login(oauth2 -> oauth2
//                    .defaultSuccessUrl("/api/users/google-login", true)
//                    .failureUrl("/login?error=true"));

    return http.build();
}

}
