package za.co.rubhub.controller;

import za.co.rubhub.dto.LoginRequest;
import za.co.rubhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

// Your controller class here
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user using UserService
            boolean isAuthenticated = userService.authenticate(
                loginRequest.getEmail(), 
                loginRequest.getPassword()
            );
            
            if (!isAuthenticated) {
                return ResponseEntity.status(401).body("Invalid email or password");
            }
            
            // Get user details (assuming UserService has a method to get user by email)
            Object user = userService.findByEmail(loginRequest.getEmail());
            
            // Generate token or session (depending on your authentication strategy)
            // String token = userService.generateAuthToken(loginRequest.getEmail());
            
            // Return successful response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            // response.put("token", token);
            response.put("user", user);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                e.getMessage());
        }
    }
}