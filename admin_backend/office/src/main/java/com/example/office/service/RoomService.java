package com.example.office.service;

import com.example.office.dto.RoomDTO;
import com.example.office.model.Room;
import com.example.office.model.User;
import com.example.office.model.Workspace;
import com.example.office.repository.RoomRepository;
import com.example.office.repository.WorkspaceMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomService {
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;
    
    @Autowired
    private JwtService jwtService;
    
    public RoomDTO createRoom(Long workspaceId, String name, String bgImageUrl, Integer width, Integer height, Long creatorId) {
        // 检查用户是否有权限在此工作空间创建房间
        boolean hasAccess = workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, creatorId);
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }
        
        Workspace workspace = new Workspace();
        workspace.setId(workspaceId);
        
        Room room = new Room();
        room.setWorkspace(workspace);
        room.setName(name);
        room.setBgImageUrl(bgImageUrl);
        room.setPosX(0);
        room.setPosY(0);
        room.setWidth(width != null ? width : 1600);
        room.setHeight(height != null ? height : 900);
        
        return convertToDTO(roomRepository.save(room));
    }
    
    public List<RoomDTO> getWorkspaceRooms(Long workspaceId, Long userId) {
        // 检查用户是否有权限访问此工作空间
        boolean hasAccess = workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }
        
        List<Room> rooms = roomRepository.findByWorkspaceId(workspaceId);
        return rooms.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public RoomDTO getRoomById(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        // 检查用户是否有权限访问此房间
        boolean hasAccess = workspaceMemberRepository.existsByWorkspaceIdAndUserId(
                room.getWorkspace().getId(), userId);
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }
        
        return convertToDTO(room);
    }
    
    public RoomDTO updateRoom(Long roomId, Long userId, String name, String bgImageUrl, Integer width, Integer height) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        // 检查用户是否有权限更新此房间
        boolean hasAccess = workspaceMemberRepository.existsByWorkspaceIdAndUserId(
                room.getWorkspace().getId(), userId);
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }
        
        if (name != null) room.setName(name);
        if (bgImageUrl != null) room.setBgImageUrl(bgImageUrl);
        if (width != null) room.setWidth(width);
        if (height != null) room.setHeight(height);
        
        return convertToDTO(roomRepository.save(room));
    }
    
    public void deleteRoom(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        // 检查用户是否有权限删除此房间（只有工作空间创建者或管理员）
        Long workspaceId = room.getWorkspace().getId();
        if (!workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId)) {
            throw new RuntimeException("Access denied");
        }
        
        roomRepository.delete(room);
    }
    
    private RoomDTO convertToDTO(Room room) {
        return new RoomDTO(
                room.getId(),
                room.getName(),
                room.getBgImageUrl(),
                room.getPosX(),
                room.getPosY(),
                room.getWidth(),
                room.getHeight(),
                null // workspace DTO will be set in controller if needed
        );
    }
}