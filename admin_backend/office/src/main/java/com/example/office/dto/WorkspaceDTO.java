package com.example.office.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkspaceDTO {
    private Long id;
    private String name;
    private String description;
    private UserDTO createdBy;
}