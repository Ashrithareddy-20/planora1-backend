package com.planora.dto;

import com.planora.model.VendorCategory;
import java.time.LocalDate;

public class RecommendationRequestDTO {
    private Long eventId;
    private String eventType;
    private String location;
    private LocalDate date;
    private Integer guestCount;
    private Double budget;
    private VendorCategory preferredCategory;
    private String preferences;

    public RecommendationRequestDTO() {}

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Integer getGuestCount() { return guestCount; }
    public void setGuestCount(Integer guestCount) { this.guestCount = guestCount; }
    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }
    public VendorCategory getPreferredCategory() { return preferredCategory; }
    public void setPreferredCategory(VendorCategory preferredCategory) { this.preferredCategory = preferredCategory; }
    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }
}
