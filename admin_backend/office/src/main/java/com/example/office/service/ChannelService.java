package com.example.office.service;

import com.example.office.dto.ChannelDTO;
import com.example.office.model.Channel;
import com.example.office.model.ChannelMember;
import com.example.office.model.User;
import com.example.office.model.Workspace;
import com.example.office.repository.ChannelMemberRepository;
import com.example.office.repository.ChannelRepository;
import com.example.office.repository.WorkspaceMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChannelService {
    
    @Autowired
    private ChannelRepository channelRepository;
    
    @Autowired
    private ChannelMemberRepository channelMemberRepository;
    
    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;
    
    public ChannelDTO createChannel(Long workspaceId, String name, String type, Long creatorId) {
        // 检查用户是否有权限在此工作空间创建频道
        boolean hasAccess = workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, creatorId);
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }
        
        Workspace workspace = new Workspace();
        workspace.setId(workspaceId);
        
        User creator = new User();
        creator.setId(creatorId);
        
        Channel channel = new Channel();
        channel.setWorkspace(workspace);
        channel.setName(name);
        channel.setType("PRIVATE".equalsIgnoreCase(type) ? "PRIVATE" : "PUBLIC");
        channel.setCreatedBy(creator);
        
        Channel savedChannel = channelRepository.save(channel);
        
        // 创建者自动加入频道
        ChannelMember member = new ChannelMember();
        member.setChannel(savedChannel);
        member.setUser(creator);
        channelMemberRepository.save(member);
        
        return convertToDTO(savedChannel);
    }
    
    public List<ChannelDTO> getWorkspaceChannels(Long workspaceId, Long userId) {
        // 检查用户是否有权限访问此工作空间
        boolean hasAccess = workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }
        
        List<Channel> channels = channelRepository.findByWorkspaceId(workspaceId);
        return channels.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public ChannelDTO getChannelById(Long channelId, Long userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Channel not found"));
        
        // 检查用户是否有权限访问此频道
        boolean hasAccess = channelMemberRepository.existsByChannelIdAndUserId(channelId, userId);
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }
        
        return convertToDTO(channel);
    }
    
    public void joinChannel(Long channelId, Long userId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Channel not found"));
        
        // 检查用户是否有权限加入此频道
        boolean hasAccess = channelMemberRepository.existsByChannelIdAndUserId(channelId, userId);
        if (hasAccess) {
            throw new RuntimeException("Already a member of this channel");
        }
        
        // 对于公共频道，任何工作空间成员都可以加入
        if ("PUBLIC".equals(channel.getType())) {
            boolean workspaceAccess = workspaceMemberRepository.existsByWorkspaceIdAndUserId(
                    channel.getWorkspace().getId(), userId);
            if (!workspaceAccess) {
                throw new RuntimeException("Access denied");
            }
        } else {
            // 对于私人频道，需要工作空间成员权限
            boolean workspaceAccess = workspaceMemberRepository.existsByWorkspaceIdAndUserId(
                    channel.getWorkspace().getId(), userId);
            if (!workspaceAccess) {
                throw new RuntimeException("Access denied");
            }
        }
        
        User user = new User();
        user.setId(userId);
        
        ChannelMember member = new ChannelMember();
        member.setChannel(channel);
        member.setUser(user);
        channelMemberRepository.save(member);
    }
    
    private ChannelDTO convertToDTO(Channel channel) {
        return new ChannelDTO(
                channel.getId(),
                channel.getName(),
                channel.getType(),
                null, // workspace DTO will be set in controller if needed
                null, // createdBy UserDTO will be set in controller if needed
                null  // memberCount will be set in controller if needed
        );
    }
}