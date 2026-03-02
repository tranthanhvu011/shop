package com.accountshop.service;

import com.accountshop.entity.Conversation;
import com.accountshop.entity.Message;
import com.accountshop.entity.User;
import com.accountshop.repository.ConversationRepository;
import com.accountshop.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ChatService — user ↔ admin real-time messaging.
 * Simplified from microservice: only USER↔ADMIN conversations (no buyer/seller distinction).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    // ===========================
    // Conversations
    // ===========================

    /**
     * Get or create conversation for a user with admin.
     */
    @Transactional
    public Conversation getOrCreateConversation(User user) {
        return conversationRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Conversation conv = Conversation.builder()
                            .user(user)
                            .userUnreadCount(0)
                            .adminUnreadCount(0)
                            .build();
                    return conversationRepository.save(conv);
                });
    }

    /**
     * Get all conversations (admin view).
     */
    public List<Conversation> getAllConversations() {
        return conversationRepository.findAllByOrderByLastMessageAtDesc();
    }

    /**
     * Get conversations for a user.
     */
    public List<Conversation> getUserConversations(Long userId) {
        return conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    // ===========================
    // Messages
    // ===========================

    /**
     * Send a text message.
     */
    @Transactional
    public Message sendMessage(Long conversationId, Long senderId, String senderName,
                                Message.SenderType senderType, String content) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Cuộc trò chuyện không tồn tại"));

        Message msg = Message.builder()
                .conversation(conv)
                .senderId(senderId)
                .senderName(senderName)
                .senderType(senderType)
                .content(content)
                .messageType(Message.MessageType.TEXT)
                .isRead(false)
                .build();

        msg = messageRepository.save(msg);

        // Update conversation metadata
        conv.setLastMessage(content.length() > 200 ? content.substring(0, 200) : content);
        conv.setLastMessageAt(msg.getCreatedAt());

        // Increment unread for recipient
        if (senderType == Message.SenderType.USER) {
            conv.setAdminUnreadCount(conv.getAdminUnreadCount() + 1);
        } else {
            conv.setUserUnreadCount(conv.getUserUnreadCount() + 1);
        }
        conversationRepository.save(conv);

        log.debug("Message sent: conv={}, sender={}, type={}", conversationId, senderId, senderType);
        return msg;
    }

    /**
     * Send a file message.
     */
    @Transactional
    public Message sendFileMessage(Long conversationId, Long senderId, String senderName,
                                    Message.SenderType senderType, String fileUrl, String fileName,
                                    Message.MessageType messageType) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Cuộc trò chuyện không tồn tại"));

        String displayContent = messageType == Message.MessageType.IMAGE ? "🖼️ Hình ảnh" : "📎 Tệp tin";

        Message msg = Message.builder()
                .conversation(conv)
                .senderId(senderId)
                .senderName(senderName)
                .senderType(senderType)
                .content(displayContent)
                .messageType(messageType)
                .fileUrl(fileUrl)
                .fileName(fileName)
                .isRead(false)
                .build();

        msg = messageRepository.save(msg);

        // Update conversation
        conv.setLastMessage(displayContent);
        conv.setLastMessageAt(msg.getCreatedAt());
        if (senderType == Message.SenderType.USER) {
            conv.setAdminUnreadCount(conv.getAdminUnreadCount() + 1);
        } else {
            conv.setUserUnreadCount(conv.getUserUnreadCount() + 1);
        }
        conversationRepository.save(conv);

        return msg;
    }

    /**
     * Get messages for a conversation (newest first for pagination).
     */
    public Page<Message> getMessages(Long conversationId, Pageable pageable) {
        return messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);
    }

    /**
     * Get all messages for a conversation (oldest first for display).
     */
    public List<Message> getAllMessages(Long conversationId) {
        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    /**
     * Mark messages as read.
     */
    @Transactional
    public void markAsRead(Long conversationId, Message.SenderType readerType) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Cuộc trò chuyện không tồn tại"));

        if (readerType == Message.SenderType.USER) {
            conv.setUserUnreadCount(0);
        } else {
            conv.setAdminUnreadCount(0);
        }
        conversationRepository.save(conv);
    }

    /**
     * Count unread for user.
     */
    public int getUserUnreadCount(Long userId) {
        return conversationRepository.findByUserId(userId)
                .map(Conversation::getUserUnreadCount)
                .orElse(0);
    }

    /**
     * Count total admin unread.
     */
    public int getAdminTotalUnread() {
        return conversationRepository.findAllByOrderByLastMessageAtDesc().stream()
                .mapToInt(Conversation::getAdminUnreadCount)
                .sum();
    }
}
