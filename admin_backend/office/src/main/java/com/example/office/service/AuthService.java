package com.example.office.service;

import com.example.office.dto.*;
import com.example.office.model.RefreshToken;
import com.example.office.model.User;
import com.example.office.repository.RefreshTokenRepository;
import com.example.office.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }
        
        String token = jwtService.generateToken(user);
        RefreshToken refreshToken = generateRefreshToken(user);
        
        return new AuthResponse(token, refreshToken.getToken(), convertToUserDTO(user));
    }
    
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username already in use");
        }
        
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setStatus((byte) 1); // 默认状态为1（正常）
        
        User savedUser = userRepository.save(user);
        
        String token = jwtService.generateToken(savedUser);
        RefreshToken refreshToken = generateRefreshToken(savedUser);
        
        return new AuthResponse(token, refreshToken.getToken(), convertToUserDTO(savedUser));
    }
    
    private RefreshToken generateRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        
        return refreshTokenRepository.save(refreshToken);
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
}