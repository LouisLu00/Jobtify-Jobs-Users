package com.example.user_service.controller;

import com.example.user_service.model.User;
import com.example.user_service.service.UserService;
import com.example.user_service.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.io.IOException;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

import static com.example.user_service.util.JwtUtils.generateToken;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

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
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        String correlationId = MDC.get("X-Correlation-ID");
        logger.info("Registering user: {}, Correlation-ID={}", user.getUsername(), correlationId);

        try {
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                logger.warn("Password is missing for registration: {}", user.getUsername());
                return ResponseEntity.badRequest().body("Password is required for regular registration");
            }

            User registeredUser = userService.registerUser(user.getUsername(), user.getPassword(), user.getEmail());
            logger.info("User successfully registered: {}, Correlation-ID={}", user.getUsername(), correlationId);
            return ResponseEntity.status(HttpStatus.CREATED).body(generateUserHateoasResponse(registeredUser));
        } catch (Exception e) {
            logger.error("Registration failed for user: {}. Error: {}, Correlation-ID={}", user.getUsername(), e.getMessage(), correlationId, e);
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/google-register")
    public ResponseEntity<?> googleRegister(@RequestBody Map<String, String> body) {
        String correlationId = MDC.get("X-Correlation-ID");
        logger.info("Google registration attempt, Correlation-ID={}", correlationId);

        try {
            String googleId = body.get("googleId");
            String email = body.get("email");
            String name = body.get("name");

            if (googleId == null || email == null || name == null) {
                logger.warn("Google registration missing required fields, Correlation-ID={}", correlationId);
                return ResponseEntity.badRequest().body("Missing required fields for Google registration");
            }

            User googleUser = userService.handleGoogleLogin(googleId, email, name);
            logger.info("Google user successfully registered: {}, Correlation-ID={}", email, correlationId);
            return ResponseEntity.status(HttpStatus.CREATED).body(generateUserHateoasResponse(googleUser));
        } catch (Exception e) {
            logger.error("Google registration failed. Error: {}, Correlation-ID={}", e.getMessage(), correlationId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Google registration failed: " + e.getMessage());
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

                // Generate JWT token with only the username
                String token = generateToken(authenticatedUser.getUsername());
                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "user", generateUserHateoasResponse(authenticatedUser)
                ));
            } else {
                logger.warn("Invalid login credentials for user: {}, Correlation-ID={}", user.getUsername(), correlationId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            logger.error("Error during login for user: {}. Error: {}, Correlation-ID={}", user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {
        String idToken = body.get("idToken");

        if (idToken == null || idToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID token is required");
        }

        try {
            String googleClientId = System.getenv("GOOGLE_CLIENT_ID");
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken != null) {
                GoogleIdToken.Payload payload = googleIdToken.getPayload();

                if (!"accounts.google.com".equals(payload.getIssuer()) &&
                        !"https://accounts.google.com".equals(payload.getIssuer())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid issuer");
                }

                String email = payload.getEmail();
                Boolean emailVerified = payload.getEmailVerified();
                if (emailVerified == null || !emailVerified) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unverified email");
                }

                String name = (String) payload.get("name");
                String googleId = payload.getSubject();

                User user = userService.handleGoogleLogin(googleId, email, name);

                // Generate JWT token with only the username
                String token = generateToken(user.getUsername());
                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "user", generateUserHateoasResponse(user)
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID token");
            }
        } catch (GeneralSecurityException | IOException e) {
            logger.error("Error verifying Google ID token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Token verification failed");
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

    @GetMapping("/{userId}/exists")
    public ResponseEntity<Boolean> checkUserExists(@PathVariable Long userId) {
        String correlationId = MDC.get("X-Correlation-ID");
        logger.info("Checking if user exists for ID: {}, Correlation-ID={}", userId, correlationId);

        boolean exists = userService.userExists(userId);
        logger.info("User existence check: id={}, exists={}, Correlation-ID={}", userId, exists, correlationId);
        return ResponseEntity.ok(exists);
    }
}
