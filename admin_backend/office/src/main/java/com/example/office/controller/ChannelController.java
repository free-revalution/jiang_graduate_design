package com.example.office.controller;

import com.example.office.dto.ChannelDTO;
import com.example.office.dto.CreateChannelRequest;
import com.example.office.service.ChannelService;
import com.example.office.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ChannelController {
    
    @Autowired
    private ChannelService channelService;
    
    @Autowired
    private JwtService jwtService;
    
    @PostMapping
    public ResponseEntity<ChannelDTO> createChannel(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateChannelRequest request) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        ChannelDTO channel = channelService.createChannel(
                request.getWorkspaceId(),
                request.getName(),
                request.getType(),
                userId);
        
        return ResponseEntity.ok(channel);
    }
    
    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<List<ChannelDTO>> getWorkspaceChannels(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long workspaceId) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        List<ChannelDTO> channels = channelService.getWorkspaceChannels(workspaceId, userId);
        
        return ResponseEntity.ok(channels);
    }
    
    @GetMapping("/{channelId}")
    public ResponseEntity<ChannelDTO> getChannelById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long channelId) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        ChannelDTO channel = channelService.getChannelById(channelId, userId);
        
        return ResponseEntity.ok(channel);
    }
    
    @PostMapping("/{channelId}/join")
    public ResponseEntity<?> joinChannel(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long channelId) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        channelService.joinChannel(channelId, userId);
        
        return ResponseEntity.ok().build();
    }
}