package com.example.office.dto;

import lombok.Data;

@Data
public class SendMessageRequest {
    private Long channelId;
    private String content;
    private String msgType; // TEXT, IMAGE, FILE, SYSTEM
    private Long replyToId;
}