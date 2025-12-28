package com.example.office.service;

import com.example.office.dto.WorkspaceDTO;
import com.example.office.model.User;
import com.example.office.model.Workspace;
import com.example.office.model.WorkspaceMember;
import com.example.office.repository.WorkspaceMemberRepository;
import com.example.office.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkspaceService {
    
    @Autowired
    private WorkspaceRepository workspaceRepository;
    
    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;
    
    @Autowired
    private JwtService jwtService;
    
    public WorkspaceDTO createWorkspace(String name, String description, Long creatorId) {
        User creator = new User();
        creator.setId(creatorId);
        
        Workspace workspace = new Workspace();
        workspace.setName(name);
        workspace.setDescription(description);
        workspace.setCreatedBy(creator);
        
        Workspace savedWorkspace = workspaceRepository.save(workspace);
        
        // 创建者自动成为空间所有者
        WorkspaceMember owner = new WorkspaceMember();
        owner.setWorkspace(savedWorkspace);
        owner.setUser(creator);
        owner.setRole("OWNER");
        workspaceMemberRepository.save(owner);
        
        return convertToDTO(savedWorkspace);
    }
    
    public List<WorkspaceDTO> getUserWorkspaces(Long userId) {
        List<Workspace> workspaces = workspaceRepository.findByCreatedByIdOrMembersUserId(userId);
        return workspaces.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public WorkspaceDTO getWorkspaceById(Long workspaceId, Long userId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));
        
        // 检查用户是否有权限访问此工作空间
        boolean hasAccess = workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
        if (!hasAccess && !workspace.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        return convertToDTO(workspace);
    }
    
    public WorkspaceDTO updateWorkspace(Long workspaceId, Long userId, String name, String description) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));
        
        // 只有创建者或管理员可以更新工作空间
        if (!workspace.getCreatedBy().getId().equals(userId)) {
            WorkspaceMember member = workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
                    .orElseThrow(() -> new RuntimeException("Access denied"));
            
            if (!"ADMIN".equals(member.getRole()) && !"OWNER".equals(member.getRole())) {
                throw new RuntimeException("Insufficient permissions");
            }
        }
        
        workspace.setName(name);
        workspace.setDescription(description);
        
        return convertToDTO(workspaceRepository.save(workspace));
    }
    
    private WorkspaceDTO convertToDTO(Workspace workspace) {
        return new WorkspaceDTO(
                workspace.getId(),
                workspace.getName(),
                workspace.getDescription(),
                null // createdBy UserDTO will be set in controller if needed
        );
    }
}