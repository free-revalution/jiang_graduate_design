package com.example.office.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_read_cursor", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cursor", columnNames = {"channel_id", "user_id"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageReadCursor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cur_ch"))
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cur_user"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cur_msg"))
    private Message message;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}