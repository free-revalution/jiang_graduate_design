package com.example.office.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MeetingDTO {
    private Long id;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private WorkspaceDTO workspace;
    private RoomDTO room;
    private UserDTO createdBy;
    private Integer participantCount;
}