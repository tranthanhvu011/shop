package com.accountshop.repository;

import com.accountshop.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByUserId(Long userId);
    List<Conversation> findAllByOrderByLastMessageAtDesc();
    List<Conversation> findByUserIdOrderByUpdatedAtDesc(Long userId);
}
