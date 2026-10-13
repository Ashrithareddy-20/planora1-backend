package com.planora.dto;

import java.util.List;

public class ChatResponseDTO {
    private String reply;
    private String retrievedContext;
    private List<String> suggestions;
    private Boolean isAiGenerated;

    public ChatResponseDTO() {}

    public ChatResponseDTO(String reply, String retrievedContext, List<String> suggestions, Boolean isAiGenerated) {
        this.reply = reply;
        this.retrievedContext = retrievedContext;
        this.suggestions = suggestions;
        this.isAiGenerated = isAiGenerated;
    }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public String getRetrievedContext() { return retrievedContext; }
    public void setRetrievedContext(String retrievedContext) { this.retrievedContext = retrievedContext; }
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    public Boolean getIsAiGenerated() { return isAiGenerated; }
    public void setIsAiGenerated(Boolean aiGenerated) { isAiGenerated = aiGenerated; }
}
