package com.example.office.controller;

import com.example.office.dto.WorkspaceDTO;
import com.example.office.service.WorkspaceService;
import com.example.office.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
@CrossOrigin(origins = "*")
public class WorkspaceController {
    
    @Autowired
    private WorkspaceService workspaceService;
    
    @Autowired
    private JwtService jwtService;
    
    @PostMapping
    public ResponseEntity<WorkspaceDTO> createWorkspace(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateWorkspaceRequest request) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        WorkspaceDTO workspace = workspaceService.createWorkspace(
                request.getName(), request.getDescription(), userId);
        
        return ResponseEntity.ok(workspace);
    }
    
    @GetMapping
    public ResponseEntity<List<WorkspaceDTO>> getUserWorkspaces(
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        List<WorkspaceDTO> workspaces = workspaceService.getUserWorkspaces(userId);
        return ResponseEntity.ok(workspaces);
    }
    
    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceDTO> getWorkspace(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long workspaceId) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        WorkspaceDTO workspace = workspaceService.getWorkspaceById(workspaceId, userId);
        return ResponseEntity.ok(workspace);
    }
    
    @PutMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceDTO> updateWorkspace(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long workspaceId,
            @RequestBody UpdateWorkspaceRequest request) {
        
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        
        WorkspaceDTO workspace = workspaceService.updateWorkspace(
                workspaceId, userId, request.getName(), request.getDescription());
        
        return ResponseEntity.ok(workspace);
    }
    
    // DTO类
    public static class CreateWorkspaceRequest {
        private String name;
        private String description;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    public static class UpdateWorkspaceRequest {
        private String name;
        private String description;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}