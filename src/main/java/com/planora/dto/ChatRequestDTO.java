package com.planora.dto;

import java.util.List;

public class ChatRequestDTO {
    private String message;
    private Long eventId;
    private List<HistoryItem> history;

    public static class HistoryItem {
        private String role; // "user" or "assistant"
        private String content;

        public HistoryItem() {}
        public HistoryItem(String role, String content) {
            this.role = role;
            this.content = content;
        }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public ChatRequestDTO() {}

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public List<HistoryItem> getHistory() { return history; }
    public void setHistory(List<HistoryItem> history) { this.history = history; }
}
