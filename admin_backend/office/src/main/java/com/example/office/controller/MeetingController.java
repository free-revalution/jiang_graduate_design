package com.example.office.controller;

import com.example.office.dto.CreateMeetingRequest;
import com.example.office.dto.MeetingDTO;
import com.example.office.dto.UpdateMeetingRequest;
import com.example.office.service.JwtService;
import com.example.office.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MeetingController {
    
    @Autowired
    private MeetingService meetingService;
    
    @Autowired
    private JwtService jwtService;
    
    @PostMapping
    public ResponseEntity<MeetingDTO> createMeeting(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateMeetingRequest request) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        MeetingDTO meeting = meetingService.createMeeting(
                request.getWorkspaceId(),
                request.getTitle(),
                request.getRoomId(),
                request.getStartTime(),
                request.getEndTime(),
                userId);
        
        return ResponseEntity.ok(meeting);
    }
    
    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<List<MeetingDTO>> getWorkspaceMeetings(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long workspaceId) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        List<MeetingDTO> meetings = meetingService.getWorkspaceMeetings(workspaceId, userId);
        
        return ResponseEntity.ok(meetings);
    }
    
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<MeetingDTO>> getRoomMeetings(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long roomId) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        List<MeetingDTO> meetings = meetingService.getRoomMeetings(roomId, userId);
        
        return ResponseEntity.ok(meetings);
    }
    
    @GetMapping("/{meetingId}")
    public ResponseEntity<MeetingDTO> getMeetingById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long meetingId) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        MeetingDTO meeting = meetingService.getMeetingById(meetingId, userId);
        
        return ResponseEntity.ok(meeting);
    }
    
    @PutMapping("/{meetingId}")
    public ResponseEntity<MeetingDTO> updateMeeting(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long meetingId,
            @RequestBody UpdateMeetingRequest request) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        MeetingDTO meeting = meetingService.updateMeeting(
                meetingId,
                userId,
                request.getTitle(),
                request.getStartTime(),
                request.getEndTime(),
                request.getStatus());
        
        return ResponseEntity.ok(meeting);
    }
    
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<?> deleteMeeting(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long meetingId) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        meetingService.deleteMeeting(meetingId, userId);
        
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{meetingId}/join")
    public ResponseEntity<?> joinMeeting(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long meetingId) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        meetingService.joinMeeting(meetingId, userId);
        
        return ResponseEntity.ok().build();
    }
}