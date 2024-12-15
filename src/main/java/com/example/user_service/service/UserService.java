package com.example.user_service.service;

import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    public User registerUser(String username, String password, String email) {
//        String correlationId = MDC.get("X-Correlation-ID");
//        logger.info("Registering user: username={}, Correlation-ID={}", username, correlationId);
//
//        User user = new User(username, passwordEncoder.encode(password), email);
//        return userRepository.save(user);
//    }
    public User registerUser(String username, String password, String email) {
        String correlationId = MDC.get("X-Correlation-ID");
        logger.info("Registering user: username={}, Correlation-ID={}", username, correlationId);

        if (password == null || password.isEmpty()) {
            logger.error("Password is null or empty for regular registration: username={}, Correlation-ID={}", username, correlationId);
            throw new IllegalArgumentException("Password cannot be null or empty for regular registration");
        }

        String hashedPassword = passwordEncoder.encode(password);
        logger.debug("Hashed password for user: {}", username);

        User user = new User(username, hashedPassword, email);
        return userRepository.save(user);
    }

//    public User handleGoogleLogin(String googleId, String email, String name) {
//        String correlationId = MDC.get("X-Correlation-ID");
//        logger.info("Handling Google login for email: {}, Correlation-ID={}", email, correlationId);
//
//        Optional<User> existingUser = findByEmail(email);
//        if (existingUser.isPresent()) {
//            logger.info("Google user exists: email={}, Correlation-ID={}", email, correlationId);
//            return existingUser.get();
//        }
//
//        User newUser = new User(name, null, email);
//        newUser.setGoogleId(googleId);
//        return userRepository.save(newUser);
//    }
    public User handleGoogleLogin(String googleId, String email, String name) {
        String correlationId = MDC.get("X-Correlation-ID");
        logger.info("Handling Google login for email: {}, Correlation-ID={}", email, correlationId);

        Optional<User> existingUser = findByEmail(email);
        if (existingUser.isPresent()) {
            logger.info("Google user exists: email={}, Correlation-ID={}", email, correlationId);
            return existingUser.get();
        }

        User newUser = new User(name, null, email); // Ensure password is null
        newUser.setGoogleId(googleId);
        return userRepository.save(newUser);
    }

//    public User loginUser(String username, String password) {
//        String correlationId = MDC.get("X-Correlation-ID");
//        logger.info("Attempting to login user: {}, Correlation-ID={}", username, correlationId);
//
//        Optional<User> optionalUser = userRepository.findByUsername(username);
//        return optionalUser.filter(user -> passwordEncoder.matches(password, user.getPassword()))
//                .orElse(null);
//    }
    public User loginUser(String username, String password) {
        String correlationId = MDC.get("X-Correlation-ID");
        logger.info("Attempting to login user: {}, Correlation-ID={}", username, correlationId);

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            logger.warn("User not found: {}, Correlation-ID={}", username, correlationId);
            return null;
        }

        User user = optionalUser.get();
        logger.debug("User found: {}", username);

        // Skip password validation for Google users
        if (user.getPassword() == null) {
            logger.info("Google user login successful: {}, Correlation-ID={}", username, correlationId);
            return user;
        }

        // Validate password for regular users
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Invalid password for user: {}, Correlation-ID={}", username, correlationId);
            return null;
        }

        logger.info("Regular user login successful: {}, Correlation-ID={}", username, correlationId);
        return user;
    }

    public Optional<User> findById(Long id) {
        String correlationId = MDC.get("X-Correlation-ID");
        logger.info("Finding user by ID: {}, Correlation-ID={}", id, correlationId);
        return userRepository.findById(id);
    }

    public boolean userExists(Long userId) {
        String correlationId = MDC.get("X-Correlation-ID");
        logger.info("Checking if user exists for ID: {}, Correlation-ID={}", userId, correlationId);
        return userRepository.existsById(userId);
    }

    public Optional<User> findByEmail(String email) {
        String correlationId = MDC.get("X-Correlation-ID");
        logger.info("Looking up user by email: {}, Correlation-ID={}", email, correlationId);
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        String correlationId = MDC.get("X-Correlation-ID");
        logger.info("Saving user: email={}, Correlation-ID={}", user.getEmail(), correlationId);
        return userRepository.save(user);
    }


}