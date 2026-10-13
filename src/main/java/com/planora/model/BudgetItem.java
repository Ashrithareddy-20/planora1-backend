package com.planora.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "budget_items")
public class BudgetItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VendorCategory category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Double allocatedAmount = 0.0;

    private Double actualAmount = 0.0;

    private String notes;

    private LocalDateTime createdAt = LocalDateTime.now();

    public BudgetItem() {}

    public BudgetItem(Event event, VendorCategory category, String title, Double allocatedAmount, Double actualAmount, String notes) {
        this.event = event;
        this.category = category;
        this.title = title;
        this.allocatedAmount = allocatedAmount;
        this.actualAmount = actualAmount != null ? actualAmount : 0.0;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public VendorCategory getCategory() {
        return category;
    }

    public void setCategory(VendorCategory category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(Double allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public Double getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(Double actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
