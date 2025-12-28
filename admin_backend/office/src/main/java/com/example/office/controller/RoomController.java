package com.example.office.controller;

import com.example.office.dto.RoomDTO;
import com.example.office.dto.CreateRoomRequest;
import com.example.office.dto.UpdateRoomRequest;
import com.example.office.service.JwtService;
import com.example.office.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoomController {
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private JwtService jwtService;
    
    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateRoomRequest request) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        RoomDTO room = roomService.createRoom(
                request.getWorkspaceId(),
                request.getName(),
                request.getBgImageUrl(),
                request.getWidth(),
                request.getHeight(),
                userId);
        
        return ResponseEntity.ok(room);
    }
    
    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<List<RoomDTO>> getWorkspaceRooms(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long workspaceId) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        List<RoomDTO> rooms = roomService.getWorkspaceRooms(workspaceId, userId);
        
        return ResponseEntity.ok(rooms);
    }
    
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoomById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long roomId) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        RoomDTO room = roomService.getRoomById(roomId, userId);
        
        return ResponseEntity.ok(room);
    }
    
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDTO> updateRoom(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long roomId,
            @RequestBody UpdateRoomRequest request) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        RoomDTO room = roomService.updateRoom(
                roomId,
                userId,
                request.getName(),
                request.getBgImageUrl(),
                request.getWidth(),
                request.getHeight());
        
        return ResponseEntity.ok(room);
    }
    
    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoom(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long roomId) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        roomService.deleteRoom(roomId, userId);
        
        return ResponseEntity.ok().build();
    }
}