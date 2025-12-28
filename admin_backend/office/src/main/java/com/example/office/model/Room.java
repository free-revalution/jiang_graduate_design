package com.example.office.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false, foreignKey = @ForeignKey(name = "fk_room_ws"))
    private Workspace workspace;

    @Column(nullable = false)
    private String name;

    private String bgImageUrl;

    @Column(name = "pos_x", columnDefinition = "INT DEFAULT 0")
    private Integer posX;

    @Column(name = "pos_y", columnDefinition = "INT DEFAULT 0")
    private Integer posY;

    @Column(columnDefinition = "INT DEFAULT 1600")
    private Integer width;

    @Column(columnDefinition = "INT DEFAULT 900")
    private Integer height;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}