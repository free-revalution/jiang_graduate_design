package com.example.office.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "channel_members", uniqueConstraints = {
        @UniqueConstraint(name = "uk_ch_user", columnNames = {"channel_id", "user_id"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cm_ch"))
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cm_user"))
    private User user;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime joinedAt;
}