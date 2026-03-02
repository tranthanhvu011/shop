package com.accountshop.controller.user;

import com.accountshop.entity.Conversation;
import com.accountshop.entity.Message;
import com.accountshop.entity.User;
import com.accountshop.repository.ConversationRepository;
import com.accountshop.repository.MessageRepository;
import com.accountshop.security.SecurityUtils;
import com.accountshop.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * UserMessageController — handles user-side chat/messaging.
 * Routes: /messages, /messages/{id}, /messages/{id}/send
 */
@Controller
@RequiredArgsConstructor
public class UserMessageController {

    private final SecurityUtils securityUtils;
    private final ChatService chatService;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    /**
     * Auto-open: always create/get conversation with admin and show it directly.
     */
    @GetMapping("/messages")
    public String messages(Model model) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        model.addAttribute("currentUser", user);

        Conversation conv = chatService.getOrCreateConversation(user);
        model.addAttribute("selectedConv", conv);
        model.addAttribute("messages", messageRepository.findByConversationIdOrderByCreatedAtAsc(conv.getId()));

        chatService.markAsRead(conv.getId(), Message.SenderType.USER);

        return "user/messages";
    }

    @GetMapping("/messages/{id}")
    public String messageDetail(@PathVariable Long id, Model model) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        model.addAttribute("currentUser", user);
        var conv = conversationRepository.findById(id).orElseThrow();
        model.addAttribute("selectedConv", conv);
        model.addAttribute("messages", messageRepository.findByConversationIdOrderByCreatedAtAsc(id));

        chatService.markAsRead(conv.getId(), Message.SenderType.USER);
        return "user/messages";
    }

    /**
     * Send message from user to admin.
     */
    @PostMapping("/messages/{id}/send")
    public String sendMessage(@PathVariable Long id, @RequestParam String content) {
        User user = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        chatService.sendMessage(id, user.getId(), user.getUsername(), Message.SenderType.USER, content);
        return "redirect:/messages/" + id;
    }
}
