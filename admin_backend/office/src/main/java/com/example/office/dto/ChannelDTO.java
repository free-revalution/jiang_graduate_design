package com.example.office.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChannelDTO {
    private Long id;
    private String name;
    private String type;
    private WorkspaceDTO workspace;
    private UserDTO createdBy;
    private Integer memberCount;
}