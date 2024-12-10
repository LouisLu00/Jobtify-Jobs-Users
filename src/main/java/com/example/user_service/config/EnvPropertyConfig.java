package com.example.user_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
public class EnvPropertyConfig {

    public EnvPropertyConfig(ConfigurableEnvironment environment) {
        environment.getSystemProperties().put("spring.security.oauth2.client.registration.google.client-id", EnvConfig.get("GOOGLE_CLIENT_ID"));
        environment.getSystemProperties().put("spring.security.oauth2.client.registration.google.client-secret", EnvConfig.get("GOOGLE_CLIENT_SECRET"));
        /* environment.getSystemProperties().put("spring.security.oauth2.client.registration.google.redirect-uri", EnvConfig.get("GOOGLE_REDIRECT_URI"));*/
    }
}

