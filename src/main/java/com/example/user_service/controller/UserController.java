//package com.example.user_service.controller;
//
//import com.example.user_service.model.User;
//import com.example.user_service.service.UserService;
//import com.example.user_service.config.EnvConfig;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.hateoas.EntityModel;
//import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import java.security.GeneralSecurityException;
//import java.util.Collections;
//import java.util.Map;
//import java.io.IOException;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.http.javanet.NetHttpTransport;
//
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.MDC;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/users")
//public class UserController {
//
//    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
//
//    @Autowired
//    private UserService userService;
//
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody User user) {
//        String correlationId = MDC.get("X-Correlation-ID");
//        logger.info("Registering user: {}, Correlation-ID={}", user.getUsername(), correlationId);
//
//        try {
//            User registeredUser = userService.registerUser(user.getUsername(), user.getPassword(), user.getEmail());
//            logger.info("User successfully registered: {}, Correlation-ID={}", user.getUsername(), correlationId);
//            return ResponseEntity.status(HttpStatus.CREATED).body(generateUserHateoasResponse(registeredUser));
//        } catch (Exception e) {
//            logger.error("Registration failed for user: {}. Error: {}, Correlation-ID={}", user.getUsername(), e.getMessage(), correlationId);
//            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
//        }
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> loginUser(@RequestBody User user) {
//        String correlationId = MDC.get("X-Correlation-ID");
//        logger.info("User login attempt: {}, Correlation-ID={}", user.getUsername(), correlationId);
//
//        try {
//            User authenticatedUser = userService.loginUser(user.getUsername(), user.getPassword());
//            if (authenticatedUser != null) {
//                logger.info("User successfully logged in: {}, Correlation-ID={}", user.getUsername(), correlationId);
//                return ResponseEntity.ok(generateUserHateoasResponse(authenticatedUser));
//            } else {
//                logger.warn("Invalid login credentials for user: {}, Correlation-ID={}", user.getUsername(), correlationId);
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//            }
//        } catch (Exception e) {
//            logger.error("Error during login for user: {}. Error: {}, Correlation-ID={}", user.getUsername(), e.getMessage(), correlationId);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
//        }
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getUser(@PathVariable Long id) {
//        String correlationId = MDC.get("X-Correlation-ID");
//        logger.info("Fetching user by ID: {}, Correlation-ID={}", id, correlationId);
//
//        Optional<User> user = userService.findById(id);
//        if (user.isPresent()) {
//            logger.info("User found for ID: {}, Correlation-ID={}", id, correlationId);
//            return ResponseEntity.ok(generateUserHateoasResponse(user.get()));
//        } else {
//            logger.warn("User not found for ID: {}, Correlation-ID={}", id, correlationId);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        }
//    }
//
//    @GetMapping("/{userId}/exists")
//    public ResponseEntity<Boolean> checkUserExists(@PathVariable Long userId) {
//        String correlationId = MDC.get("X-Correlation-ID");
//        logger.info("Checking if user exists for ID: {}, Correlation-ID={}", userId, correlationId);
//
//        boolean exists = userService.userExists(userId);
//        logger.info("User existence check: id={}, exists={}, Correlation-ID={}", userId, exists, correlationId);
//        return ResponseEntity.ok(exists);
//    }
//
//    private static final String CLIENT_ID = "980242448046-qrve5hbo75iqfpp0q33uhtbud4hutped.apps.googleusercontent.com";
//
////    @PostMapping("/google-login")
////    public ResponseEntity<?> googleLogin(@RequestParam String idToken) {
////        try {
////            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
////                    .setAudience(Collections.singletonList(CLIENT_ID))
////                    .build();
////
////            GoogleIdToken googleIdToken = verifier.verify(idToken);
////
////            if (googleIdToken != null) {
////                GoogleIdToken.Payload payload = googleIdToken.getPayload();
////                String email = payload.getEmail();
////                String name = (String) payload.get("name");
////
////                // Check if user exists, register if not, and return token/session details
////                // Example: findOrCreateUser(email, name);
////
////                return ResponseEntity.ok("Login successful for: " + email);
////            } else {
////                return ResponseEntity.badRequest().body("Invalid ID token.");
////            }
////        } catch (Exception e) {
////            return ResponseEntity.status(500).body("Google login failed: " + e.getMessage());
////        }
////    }
//    //private static final Logger log = LoggerFactory.getLogger(UserController.class);
//
//    @PostMapping("/google-login")
//    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {
//        String idToken = body.get("idToken");
//
//        if (idToken == null || idToken.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID token is required");
//        }
//
//        try {
//            // Retrieve Google Client ID from environment
//            String googleClientId = EnvConfig.get("GOOGLE_CLIENT_ID");
//
//            // Debug output to verify environment variable
//            System.out.println("GOOGLE_CLIENT_ID: " + googleClientId);
//            logger.info("GOOGLE_CLIENT_ID retrieved: {}", googleClientId);
//            // Initialize verifier
//            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
//                    .setAudience(Collections.singletonList(EnvConfig.get("GOOGLE_CLIENT_ID"))) // Ensure client ID matches
//                    .build();
//
//            // Verify the ID token
//            GoogleIdToken googleIdToken = verifier.verify(idToken);
//            if (googleIdToken != null) {
//                GoogleIdToken.Payload payload = googleIdToken.getPayload();
//
//                // Check required claims
//                if (!"accounts.google.com".equals(payload.getIssuer()) &&
//                        !"https://accounts.google.com".equals(payload.getIssuer())) {
//                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid issuer");
//                }
//
//                String email = payload.getEmail();
//                Boolean emailVerified = payload.getEmailVerified(); // Fixed the type error
//                if (emailVerified == null || !emailVerified) { // Proper null check
//                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unverified email");
//                }
//
//                String name = (String) payload.get("name");
//                String googleId = payload.getSubject();
//
//                // Handle Google login or registration
//                User user = userService.handleGoogleLogin(googleId, email, name);
//
//                // Return user details or session token
//                return ResponseEntity.ok(Map.of("userId", user.getId()));
//            } else {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID token");
//            }
//        } catch (GeneralSecurityException | IOException e) {
//            logger.error("Error verifying Google ID token: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Token verification failed");
//        }
//    }
//
//
//
//
//    private EntityModel<User> generateUserHateoasResponse(User user) {
//        EntityModel<User> resource = EntityModel.of(user);
//        resource.add(WebMvcLinkBuilder.linkTo(
//                WebMvcLinkBuilder.methodOn(UserController.class).getUser(user.getId())).withSelfRel());
//        return resource;
//    }
//}
package com.example.user_service.controller;

import com.example.user_service.model.User;
import com.example.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        String correlationId = MDC.get("X-Correlation-ID");
        logger.info("Registering user: {}, Correlation-ID={}", user.getUsername(), correlationId);

        try {
            // Encrypt password and email
            String encryptedPassword = userService.encryptPassword(user.getPassword());
            String encryptedEmail = userService.encryptEmail(user.getEmail());

            user.setPassword(encryptedPassword);
            user.setEmail(encryptedEmail);

            // Register the user
            User registeredUser = userService.registerUser(user);
            logger.info("User successfully registered: {}, Correlation-ID={}", user.getUsername(), correlationId);

            return ResponseEntity.status(HttpStatus.CREATED).body(generateUserHateoasResponse(registeredUser));
        } catch (Exception e) {
            logger.error("Registration failed for user: {}. Error: {}, Correlation-ID={}", user.getUsername(), e.getMessage(), correlationId);
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        String correlationId = MDC.get("X-Correlation-ID");
        logger.info("User login attempt: {}, Correlation-ID={}", user.getUsername(), correlationId);

        try {
            User authenticatedUser = userService.loginUser(user.getUsername(), user.getPassword());
            if (authenticatedUser != null) {
                logger.info("User successfully logged in: {}, Correlation-ID={}", user.getUsername(), correlationId);
                return ResponseEntity.ok(generateUserHateoasResponse(authenticatedUser));
            } else {
                logger.warn("Invalid login credentials for user: {}, Correlation-ID={}", user.getUsername(), correlationId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            logger.error("Error during login for user: {}. Error: {}, Correlation-ID={}", user.getUsername(), e.getMessage(), correlationId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        String correlationId = MDC.get("X-Correlation-ID");
        logger.info("Fetching user by ID: {}, Correlation-ID={}", id, correlationId);

        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            logger.info("User found for ID: {}, Correlation-ID={}", id, correlationId);
            return ResponseEntity.ok(generateUserHateoasResponse(user.get()));
        } else {
            logger.warn("User not found for ID: {}, Correlation-ID={}", id, correlationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    private EntityModel<User> generateUserHateoasResponse(User user) {
        EntityModel<User> resource = EntityModel.of(user);
        resource.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).getUser(user.getId())).withSelfRel());
        return resource;
    }
}
