package com.example.office.service;

import com.example.office.dto.MeetingDTO;
import com.example.office.model.Meeting;
import com.example.office.model.MeetingParticipant;
import com.example.office.model.Room;
import com.example.office.model.User;
import com.example.office.model.Workspace;
import com.example.office.repository.MeetingParticipantRepository;
import com.example.office.repository.MeetingRepository;
import com.example.office.repository.WorkspaceMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MeetingService {
    
    @Autowired
    private MeetingRepository meetingRepository;
    
    @Autowired
    private MeetingParticipantRepository meetingParticipantRepository;
    
    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;
    
    public MeetingDTO createMeeting(Long workspaceId, String title, Long roomId, LocalDateTime startTime, LocalDateTime endTime, Long creatorId) {
        // 检查用户是否有权限在此工作空间创建会议
        boolean hasAccess = workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, creatorId);
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }
        
        Workspace workspace = new Workspace();
        workspace.setId(workspaceId);
        
        User creator = new User();
        creator.setId(creatorId);
        
        Meeting meeting = new Meeting();
        meeting.setWorkspace(workspace);
        meeting.setTitle(title);
        meeting.setCreatedBy(creator);
        meeting.setStartTime(startTime);
        meeting.setEndTime(endTime);
        meeting.setStatus("SCHEDULED");
        
        if (roomId != null) {
            Room room = new Room();
            room.setId(roomId);
            meeting.setRoom(room);
        }
        
        Meeting savedMeeting = meetingRepository.save(meeting);
        
        // 创建者自动成为参会者
        MeetingParticipant participant = new MeetingParticipant();
        participant.setMeeting(savedMeeting);
        participant.setUser(creator);
        meetingParticipantRepository.save(participant);
        
        return convertToDTO(savedMeeting);
    }
    
    public List<MeetingDTO> getWorkspaceMeetings(Long workspaceId, Long userId) {
        // 检查用户是否有权限访问此工作空间
        boolean hasAccess = workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }
        
        List<Meeting> meetings = meetingRepository.findByWorkspaceId(workspaceId);
        return meetings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MeetingDTO> getRoomMeetings(Long roomId, Long userId) {
        // 检查用户是否有权限访问此房间
        List<Meeting> meetings = meetingRepository.findByRoomId(roomId);
        return meetings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public MeetingDTO getMeetingById(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));
        
        // 检查用户是否有权限访问此会议
        boolean hasAccess = workspaceMemberRepository.existsByWorkspaceIdAndUserId(
                meeting.getWorkspace().getId(), userId);
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }
        
        return convertToDTO(meeting);
    }
    
    public MeetingDTO updateMeeting(Long meetingId, Long userId, String title, LocalDateTime startTime, LocalDateTime endTime, String status) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));
        
        // 检查用户是否有权限更新此会议
        if (!meeting.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Only the meeting creator can update this meeting");
        }
        
        if (title != null) meeting.setTitle(title);
        if (startTime != null) meeting.setStartTime(startTime);
        if (endTime != null) meeting.setEndTime(endTime);
        if (status != null) meeting.setStatus(status);
        
        return convertToDTO(meetingRepository.save(meeting));
    }
    
    public void deleteMeeting(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));
        
        // 检查用户是否有权限删除此会议
        if (!meeting.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Only the meeting creator can delete this meeting");
        }
        
        meetingRepository.delete(meeting);
    }
    
    public void joinMeeting(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));
        
        // 检查用户是否已经是参会者
        boolean isParticipant = meetingParticipantRepository.existsByMeetingIdAndUserId(meetingId, userId);
        if (isParticipant) {
            throw new RuntimeException("Already a participant of this meeting");
        }
        
        // 检查用户是否有权限加入此会议
        boolean hasAccess = workspaceMemberRepository.existsByWorkspaceIdAndUserId(
                meeting.getWorkspace().getId(), userId);
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }
        
        User user = new User();
        user.setId(userId);
        
        MeetingParticipant participant = new MeetingParticipant();
        participant.setMeeting(meeting);
        participant.setUser(user);
        meetingParticipantRepository.save(participant);
    }
    
    private MeetingDTO convertToDTO(Meeting meeting) {
        return new MeetingDTO(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getStartTime(),
                meeting.getEndTime(),
                meeting.getStatus(),
                null, // WorkspaceDTO will be set in controller if needed
                null, // RoomDTO will be set in controller if needed
                null, // CreatedBy UserDTO will be set in controller if needed
                null  // Participants count will be set in controller if needed
        );
    }
}