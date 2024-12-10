package com.example.user_service.service;

import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String username, String password, String email) {
        // Encode password only during registration
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword, email);
        return userRepository.save(user);
    }

    public User handleGoogleLogin(String googleId, String email, String name) {
        // Check if a user with this email already exists
        Optional<User> existingUser = findByEmail(email);

        if (existingUser.isPresent()) {
            // Return the existing user
            return existingUser.get();
        }

        // Create a new user for Google Login
        User newUser = new User();
        newUser.setUsername(name); // Set username to name for Google users
        newUser.setEmail(email);
        newUser.setGoogleId(googleId); // Save Google ID
        newUser.setPassword(null); // Password is null for Google Login users
        return saveUser(newUser);
    }



    public User loginUser(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        // Check plaintext password against the encoded password in the database
        return optionalUser.filter(user -> passwordEncoder.matches(password, user.getPassword())).orElse(null);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean userExists(Long userId) {
        // Logic to check if the user exists in the database
        return userRepository.existsById(userId);
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }


}
