package com.planora.controller;

import com.planora.dto.ChatRequestDTO;
import com.planora.dto.ChatResponseDTO;
import com.planora.service.AiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Chatbot", description = "Planora AI conversational event assistant and RAG engine")
public class ChatController {

    private final AiChatService aiChatService;

    public ChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat")
    @Operation(summary = "Send message to Planora AI event assistant")
    public ResponseEntity<ChatResponseDTO> chat(
            @RequestBody ChatRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(aiChatService.processChat(request, email));
    }
}
