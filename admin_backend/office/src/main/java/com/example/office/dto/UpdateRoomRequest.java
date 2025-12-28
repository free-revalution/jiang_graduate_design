package com.example.office.dto;

import lombok.Data;

@Data
public class UpdateRoomRequest {
    private String name;
    private String bgImageUrl;
    private Integer width;
    private Integer height;
}