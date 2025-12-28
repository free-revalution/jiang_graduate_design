package com.example.office.controller;

import com.example.office.dto.UserDTO;
import com.example.office.service.AuthService;
import com.example.office.service.JwtService;
import com.example.office.repository.UserRepository;
import com.example.office.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtService jwtService;
    
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserDTO userDTO = convertToUserDTO(user);
        return ResponseEntity.ok(userDTO);
    }
    
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateUserRequest request) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setAvatarUrl(request.getAvatarUrl());
        
        User savedUser = userRepository.save(user);
        UserDTO userDTO = convertToUserDTO(savedUser);
        
        return ResponseEntity.ok(userDTO);
    }
    
    private UserDTO convertToUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getStatus()
        );
    }
    
    // DTO类
    public static class UpdateUserRequest {
        private String username;
        private String email;
        private String avatarUrl;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }
}