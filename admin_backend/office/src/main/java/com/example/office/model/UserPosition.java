package com.example.office.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_positions", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_room", columnNames = {"user_id", "room_id"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_up_user"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false, foreignKey = @ForeignKey(name = "fk_up_room"))
    private Room room;

    @Column(name = "pos_x", nullable = false)
    private Integer posX;

    @Column(name = "pos_y", nullable = false)
    private Integer posY;

    @Column(columnDefinition = "VARCHAR(30) DEFAULT 'online'", nullable = false)
    private String status;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}