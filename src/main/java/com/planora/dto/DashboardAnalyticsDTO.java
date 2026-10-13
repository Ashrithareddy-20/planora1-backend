package com.planora.dto;

import java.util.List;
import java.util.Map;

public class DashboardAnalyticsDTO {
    private Long totalEvents;
    private Long upcomingEvents;
    private Long completedEvents;
    private Long pendingBookings;
    private Double totalBudget;
    private Double totalAllocated;
    private Double totalSpent;
    private Double remainingBudget;
    private Integer overallChecklistProgress;

    private Map<String, Double> categoryBudgets;
    private Map<String, Long> eventsByType;
    private List<EventDTO> recentEvents;
    private List<BookingDTO> recentBookings;
    private List<VendorDTO> recommendedVendors;

    public DashboardAnalyticsDTO() {}

    public Long getTotalEvents() { return totalEvents; }
    public void setTotalEvents(Long totalEvents) { this.totalEvents = totalEvents; }
    public Long getUpcomingEvents() { return upcomingEvents; }
    public void setUpcomingEvents(Long upcomingEvents) { this.upcomingEvents = upcomingEvents; }
    public Long getCompletedEvents() { return completedEvents; }
    public void setCompletedEvents(Long completedEvents) { this.completedEvents = completedEvents; }
    public Long getPendingBookings() { return pendingBookings; }
    public void setPendingBookings(Long pendingBookings) { this.pendingBookings = pendingBookings; }
    public Double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(Double totalBudget) { this.totalBudget = totalBudget; }
    public Double getTotalAllocated() { return totalAllocated; }
    public void setTotalAllocated(Double totalAllocated) { this.totalAllocated = totalAllocated; }
    public Double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Double totalSpent) { this.totalSpent = totalSpent; }
    public Double getRemainingBudget() { return remainingBudget; }
    public void setRemainingBudget(Double remainingBudget) { this.remainingBudget = remainingBudget; }
    public Integer getOverallChecklistProgress() { return overallChecklistProgress; }
    public void setOverallChecklistProgress(Integer overallChecklistProgress) { this.overallChecklistProgress = overallChecklistProgress; }
    public Map<String, Double> getCategoryBudgets() { return categoryBudgets; }
    public void setCategoryBudgets(Map<String, Double> categoryBudgets) { this.categoryBudgets = categoryBudgets; }
    public Map<String, Long> getEventsByType() { return eventsByType; }
    public void setEventsByType(Map<String, Long> eventsByType) { this.eventsByType = eventsByType; }
    public List<EventDTO> getRecentEvents() { return recentEvents; }
    public void setRecentEvents(List<EventDTO> recentEvents) { this.recentEvents = recentEvents; }
    public List<BookingDTO> getRecentBookings() { return recentBookings; }
    public void setRecentBookings(List<BookingDTO> recentBookings) { this.recentBookings = recentBookings; }
    public List<VendorDTO> getRecommendedVendors() { return recommendedVendors; }
    public void setRecommendedVendors(List<VendorDTO> recommendedVendors) { this.recommendedVendors = recommendedVendors; }
}
