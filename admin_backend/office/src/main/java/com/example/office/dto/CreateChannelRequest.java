package com.example.office.dto;

import lombok.Data;

@Data
public class CreateChannelRequest {
    private Long workspaceId;
    private String name;
    private String type; // PUBLIC or PRIVATE
}