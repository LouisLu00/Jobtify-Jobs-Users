package com.example.user_service.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    // private static final Dotenv dotenv = Dotenv.load();
    private static Dotenv dotenv;
    static {
        dotenv = Dotenv.configure()
                .directory("/") // Look in the root of the classpath
                .filename(".env") // Specify the file name
                .load();
    }

    public static String get(String key) {
        return dotenv.get(key);
    }
}

