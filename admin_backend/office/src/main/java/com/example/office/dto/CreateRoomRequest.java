package com.example.office.dto;

import lombok.Data;

@Data
public class CreateRoomRequest {
    private Long workspaceId;
    private String name;
    private String bgImageUrl;
    private Integer width;
    private Integer height;
}