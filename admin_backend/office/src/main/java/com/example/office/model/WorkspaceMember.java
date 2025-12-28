package com.example.office.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "workspace_members", uniqueConstraints = {
        @UniqueConstraint(name = "uk_ws_user", columnNames = {"workspace_id", "user_id"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false, foreignKey = @ForeignKey(name = "fk_wm_ws"))
    private Workspace workspace;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_wm_user"))
    private User user;

    @Column(nullable = false, columnDefinition = "ENUM('OWNER','ADMIN','MEMBER') DEFAULT 'MEMBER'")
    private String role;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime joinedAt;
}