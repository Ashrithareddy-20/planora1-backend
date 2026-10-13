package com.planora.dto;

import com.planora.model.Event.EventStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Event type is required")
    private String eventType;

    private String description;

    @NotNull(message = "Event date is required")
    private LocalDate eventDate;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Guest count is required")
    private Integer guestCount;

    @NotNull(message = "Total budget is required")
    private Double totalBudget;

    private EventStatus status = EventStatus.UPCOMING;
    private Long userId;
    private String userName;

    // Computed metrics
    private Double allocatedBudget = 0.0;
    private Double actualSpent = 0.0;
    private Double remainingBudget = 0.0;
    private Integer checklistProgress = 0;
    private Integer totalTasks = 0;
    private Integer completedTasks = 0;

    private LocalDateTime createdAt;

    public EventDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getGuestCount() { return guestCount; }
    public void setGuestCount(Integer guestCount) { this.guestCount = guestCount; }
    public Double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(Double totalBudget) { this.totalBudget = totalBudget; }
    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Double getAllocatedBudget() { return allocatedBudget; }
    public void setAllocatedBudget(Double allocatedBudget) { this.allocatedBudget = allocatedBudget; }
    public Double getActualSpent() { return actualSpent; }
    public void setActualSpent(Double actualSpent) { this.actualSpent = actualSpent; }
    public Double getRemainingBudget() { return remainingBudget; }
    public void setRemainingBudget(Double remainingBudget) { this.remainingBudget = remainingBudget; }
    public Integer getChecklistProgress() { return checklistProgress; }
    public void setChecklistProgress(Integer checklistProgress) { this.checklistProgress = checklistProgress; }
    public Integer getTotalTasks() { return totalTasks; }
    public void setTotalTasks(Integer totalTasks) { this.totalTasks = totalTasks; }
    public Integer getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(Integer completedTasks) { this.completedTasks = completedTasks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
