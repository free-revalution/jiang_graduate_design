package com.example.office.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPositionDTO {
    private Long id;
    private UserDTO user;
    private RoomDTO room;
    private Integer posX;
    private Integer posY;
    private String status;
}