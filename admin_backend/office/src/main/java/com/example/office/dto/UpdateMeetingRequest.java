package com.example.office.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateMeetingRequest {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}