package za.co.rubhub.controller;

import za.co.rubhub.model.User;
import za.co.rubhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // GET - Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        try {
            Optional<User> user = userService.findById(id);
            return user.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        try {
            Optional<User> user = userService.findByEmail(email);
            return user.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get user by phone number
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<User> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            Optional<User> user = userService.findByPhoneNumber(phoneNumber);
            return user.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get users by user type
    @GetMapping("/type/{userType}")
    public ResponseEntity<List<User>> getUsersByType(@PathVariable String userType) {
        try {
            List<User> users = userService.findByUserType(userType);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get users by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<User>> getUsersByStatus(@PathVariable String status) {
        try {
            List<User> users = userService.findByStatus(status);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Create new user
    @PostMapping
    public ResponseEntity<?> createUser(@javax.validation.Valid @RequestBody User user, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }

            // Check if email already exists
            if (userService.existsByEmail(user.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with email " + user.getEmail() + " already exists");
            }

            // Check if phone number already exists
            if (userService.existsByPhoneNumber(user.getPhoneNumber())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with phone number " + user.getPhoneNumber() + " already exists");
            }

            User savedUser = userService.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating user: " + e.getMessage());
        }
    }

    // PUT - Update user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }

            Optional<User> existingUser = userService.findById(id);
            if (existingUser == null ) {
                return ResponseEntity.notFound().build();
            }

            User user = existingUser.get();
            
            // Update fields
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setPhone(userDetails.getPhone());
            user.setProfileImageUrl(userDetails.getProfileImageUrl());
            user.setDateOfBirth(userDetails.getDateOfBirth());
            user.setAddress(userDetails.getAddress());
            user.setUserType(userDetails.getUserType());
            user.setStatus(userDetails.getStatus());
            user.setIsVerified(userDetails.getIsVerified());
            user.setTwoFactorEnabled(userDetails.getTwoFactorEnabled());

            // Only update email if it's different and not already taken
            if (!user.getEmail().equals(userDetails.getEmail())) {
                if (userService.existsByEmail(userDetails.getEmail())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("User with email " + userDetails.getEmail() + " already exists");
                }
                user.setEmail(userDetails.getEmail());
            }

            // Only update phone number if it's different and not already taken
            if (!user.getPhoneNumber().equals(userDetails.getPhoneNumber())) {
                if (userService.existsByPhoneNumber(userDetails.getPhoneNumber())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("User with phone number " + userDetails.getPhoneNumber() + " already exists");
                }
                user.setPhoneNumber(userDetails.getPhoneNumber());
            }

            User updatedUser = userService.save(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating user: " + e.getMessage());
        }
    }


    // POST - Update loyalty points
    @PostMapping("/{id}/loyalty")
    public ResponseEntity<?> updateLoyaltyPoints(@PathVariable Long id, @RequestParam Integer points) {
        try {
            Optional<User> existingUser = userService.findById(id);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            User user = existingUser.get();
            user.setLoyaltyPoints(points);
            User updatedUser = userService.save(user);
            
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating loyalty points: " + e.getMessage());
        }
    }

    // POST - Update user status
    @PostMapping("/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, 
                                            @RequestParam String status,
                                            @RequestParam(required = false) String reason) {
        try {
            Optional<User> existingUser = userService.findById(id);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            User user = existingUser.get();
            // user.setStatus(status);
            if (reason != null) {
                user.setFlaggedReason(reason);
            }
            User updatedUser = userService.save(user);
            
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating user status: " + e.getMessage());
        }
    }
}