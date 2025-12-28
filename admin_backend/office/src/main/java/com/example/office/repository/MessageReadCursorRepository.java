package com.example.office.repository;

import com.example.office.model.MessageReadCursor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageReadCursorRepository extends JpaRepository<MessageReadCursor, Long> {
    Optional<MessageReadCursor> findByChannelIdAndUserId(Long channelId, Long userId);
}