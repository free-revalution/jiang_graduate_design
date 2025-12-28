package com.example.office.service;

import com.example.office.dto.MessageDTO;
import com.example.office.model.Channel;
import com.example.office.model.Message;
import com.example.office.model.User;
import com.example.office.repository.ChannelMemberRepository;
import com.example.office.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ChannelMemberRepository channelMemberRepository;
    
    public MessageDTO sendMessage(Long channelId, Long senderId, String content, String msgType, Long replyToId) {
        // 检查用户是否有权限在此频道发送消息
        boolean hasAccess = channelMemberRepository.existsByChannelIdAndUserId(channelId, senderId);
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }
        
        Channel channel = new Channel();
        channel.setId(channelId);
        
        User sender = new User();
        sender.setId(senderId);
        
        Message message = new Message();
        message.setChannel(channel);
        message.setSender(sender);
        message.setContent(content);
        message.setMsgType(msgType != null ? msgType : "TEXT");
        
        if (replyToId != null) {
            Message replyTo = new Message();
            replyTo.setId(replyToId);
            message.setReplyTo(replyTo);
        }
        
        return convertToDTO(messageRepository.save(message));
    }
    
    public List<MessageDTO> getChannelMessages(Long channelId, Long userId, int limit) {
        // 检查用户是否有权限访问此频道
        boolean hasAccess = channelMemberRepository.existsByChannelIdAndUserId(channelId, userId);
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }
        
        Pageable pageable = PageRequest.of(0, limit > 0 ? limit : 50);
        List<Message> messages = messageRepository.findByChannelIdOrderByCreatedAtDesc(channelId);
        
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MessageDTO> getChannelMessages(Long channelId, Long userId, Long lastMessageId, int limit) {
        // 检查用户是否有权限访问此频道
        boolean hasAccess = channelMemberRepository.existsByChannelIdAndUserId(channelId, userId);
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }
        
        if (lastMessageId != null) {
            return messageRepository.findByChannelIdAndIdLessThanOrderByCreatedAtDesc(
                    channelId, lastMessageId, limit > 0 ? limit : 20)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } else {
            return getChannelMessages(channelId, userId, limit);
        }
    }
    
    public MessageDTO getMessageById(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        // 检查用户是否有权限访问此消息所在的频道
        boolean hasAccess = channelMemberRepository.existsByChannelIdAndUserId(
                message.getChannel().getId(), userId);
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }
        
        return convertToDTO(message);
    }
    
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        // 检查用户是否有权限删除此消息（发送者或频道管理员）
        if (!message.getSender().getId().equals(userId)) {
            // TODO: 添加频道管理员检查逻辑
            throw new RuntimeException("Only the message sender can delete this message");
        }
        
        message.setDeletedAt(LocalDateTime.now());
        messageRepository.save(message);
    }
    
    private MessageDTO convertToDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                null, // ChannelDTO will be set in controller if needed
                null, // UserDTO will be set in controller if needed
                message.getContent(),
                message.getMsgType(),
                null, // ReplyTo MessageDTO will be set in controller if needed
                message.getCreatedAt()
        );
    }
}