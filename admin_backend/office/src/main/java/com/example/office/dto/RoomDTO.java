package com.example.office.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomDTO {
    private Long id;
    private String name;
    private String bgImageUrl;
    private Integer posX;
    private Integer posY;
    private Integer width;
    private Integer height;
    private WorkspaceDTO workspace;
}