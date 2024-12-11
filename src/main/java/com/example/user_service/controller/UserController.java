package com.example.user_service.controller;

import com.example.user_service.model.User;
import com.example.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
            User registeredUser = userService.registerUser(user.getUsername(), user.getPassword(), user.getEmail());
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

    @GetMapping("/{userId}/exists")
    public ResponseEntity<Boolean> checkUserExists(@PathVariable Long userId) {
        String correlationId = MDC.get("X-Correlation-ID");
        logger.info("Checking if user exists for ID: {}, Correlation-ID={}", userId, correlationId);

        boolean exists = userService.userExists(userId);
        logger.info("User existence check: id={}, exists={}, Correlation-ID={}", userId, exists, correlationId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/google-login")
    public ResponseEntity<?> googleLogin(@AuthenticationPrincipal OAuth2User principal) {
        String correlationId = MDC.get("X-Correlation-ID");
        String googleId = principal.getAttribute("sub");
        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");

        logger.info("Google login attempt: email={}, name={}, Correlation-ID={}", email, name, correlationId);

        User user = userService.handleGoogleLogin(googleId, email, name);
        logger.info("Google login successful for user: email={}, Correlation-ID={}", email, correlationId);
        return ResponseEntity.ok(generateUserHateoasResponse(user));
    }

    private EntityModel<User> generateUserHateoasResponse(User user) {
        EntityModel<User> resource = EntityModel.of(user);
        resource.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).getUser(user.getId())).withSelfRel());
        return resource;
    }
}
