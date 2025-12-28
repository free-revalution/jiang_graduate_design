package com.example.office.repository;

import com.example.office.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChannelIdOrderByCreatedAtDesc(Long channelId);
    List<Message> findByChannelIdAndIdLessThanOrderByCreatedAtDesc(Long channelId, Long lastMessageId, int limit);
}