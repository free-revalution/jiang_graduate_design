package com.example.office.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private ChannelDTO channel;
    private UserDTO sender;
    private String content;
    private String msgType;
    private MessageDTO replyTo;
    private LocalDateTime createdAt;
}