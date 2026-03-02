package com.accountshop.controller;

import com.accountshop.entity.Conversation;
import com.accountshop.entity.Message;
import com.accountshop.entity.User;
import com.accountshop.repository.MessageRepository;
import com.accountshop.security.SecurityUtils;
import com.accountshop.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin chat controller — manages admin-side conversations and messages.
 */
@Controller
@RequestMapping("/admin/messages")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MessageRepository messageRepository;
    private final SecurityUtils securityUtils;

    /**
     * Show all conversations (admin view).
     */
    @GetMapping
    public String messages(Model model) {
        model.addAttribute("pageTitle", "Tin nhắn");
        model.addAttribute("conversations", chatService.getAllConversations());
        model.addAttribute("totalUnread", chatService.getAdminTotalUnread());
        return "admin/messages";
    }

    /**
     * View a specific conversation's messages.
     */
    @GetMapping("/{id}")
    public String messageDetail(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Tin nhắn");
        model.addAttribute("conversations", chatService.getAllConversations());
        model.addAttribute("totalUnread", chatService.getAdminTotalUnread());

        var conv = chatService.getAllConversations().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst().orElse(null);

        if (conv != null) {
            model.addAttribute("selectedConv", conv);
            model.addAttribute("messages",
                    messageRepository.findByConversationIdOrderByCreatedAtAsc(conv.getId()));

            // Mark as read for admin
            chatService.markAsRead(conv.getId(), Message.SenderType.ADMIN);
        }

        return "admin/messages";
    }

    /**
     * Send message from admin.
     */
    @PostMapping("/{id}/send")
    public String sendMessage(@PathVariable Long id, @RequestParam String content) {
        User admin = securityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));
        chatService.sendMessage(id, admin.getId(), admin.getUsername(), Message.SenderType.ADMIN, content);
        return "redirect:/admin/messages/" + id;
    }
}
