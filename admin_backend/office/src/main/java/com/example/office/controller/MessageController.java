package com.example.office.controller;

import com.example.office.dto.MessageDTO;
import com.example.office.dto.SendMessageRequest;
import com.example.office.service.JwtService;
import com.example.office.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private JwtService jwtService;
    
    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SendMessageRequest request) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        MessageDTO message = messageService.sendMessage(
                request.getChannelId(),
                userId,
                request.getContent(),
                request.getMsgType(),
                request.getReplyToId());
        
        return ResponseEntity.ok(message);
    }
    
    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<MessageDTO>> getChannelMessages(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long channelId,
            @RequestParam(defaultValue = "50") int limit) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        List<MessageDTO> messages = messageService.getChannelMessages(channelId, userId, limit);
        
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/channel/{channelId}/before/{lastMessageId}")
    public ResponseEntity<List<MessageDTO>> getChannelMessagesBefore(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long channelId,
            @PathVariable Long lastMessageId,
            @RequestParam(defaultValue = "20") int limit) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        List<MessageDTO> messages = messageService.getChannelMessages(channelId, userId, lastMessageId, limit);
        
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/{messageId}")
    public ResponseEntity<MessageDTO> getMessageById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long messageId) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        MessageDTO message = messageService.getMessageById(messageId, userId);
        
        return ResponseEntity.ok(message);
    }
    
    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long messageId) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        messageService.deleteMessage(messageId, userId);
        
        return ResponseEntity.ok().build();
    }
}