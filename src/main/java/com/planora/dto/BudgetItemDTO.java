package com.planora.dto;

import com.planora.model.VendorCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class BudgetItemDTO {
    private Long id;
    private Long eventId;

    @NotNull(message = "Category is required")
    private VendorCategory category;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Allocated amount is required")
    private Double allocatedAmount;

    private Double actualAmount = 0.0;
    private String notes;
    private LocalDateTime createdAt;

    public BudgetItemDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public VendorCategory getCategory() { return category; }
    public void setCategory(VendorCategory category) { this.category = category; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Double getAllocatedAmount() { return allocatedAmount; }
    public void setAllocatedAmount(Double allocatedAmount) { this.allocatedAmount = allocatedAmount; }
    public Double getActualAmount() { return actualAmount; }
    public void setActualAmount(Double actualAmount) { this.actualAmount = actualAmount; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
