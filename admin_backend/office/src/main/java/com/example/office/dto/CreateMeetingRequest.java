package com.example.office.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateMeetingRequest {
    private Long workspaceId;
    private String title;
    private Long roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}