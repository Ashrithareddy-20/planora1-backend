package com.planora.dto;

import com.planora.model.BookingRequest.BookingStatus;
import com.planora.model.VendorCategory;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingDTO {
    private Long id;
    private Long eventId;
    private String eventTitle;

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;
    private String vendorName;
    private VendorCategory vendorCategory;
    private String vendorImage;
    private String vendorCity;

    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;

    @NotNull(message = "Requested date is required")
    private LocalDate requestedDate;

    private Double estimatedPrice;
    private String message;
    private BookingStatus status = BookingStatus.PENDING;
    private String vendorResponse;
    private LocalDateTime createdAt;

    public BookingDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public VendorCategory getVendorCategory() { return vendorCategory; }
    public void setVendorCategory(VendorCategory vendorCategory) { this.vendorCategory = vendorCategory; }
    public String getVendorImage() { return vendorImage; }
    public void setVendorImage(String vendorImage) { this.vendorImage = vendorImage; }
    public String getVendorCity() { return vendorCity; }
    public void setVendorCity(String vendorCity) { this.vendorCity = vendorCity; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }
    public LocalDate getRequestedDate() { return requestedDate; }
    public void setRequestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; }
    public Double getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(Double estimatedPrice) { this.estimatedPrice = estimatedPrice; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public String getVendorResponse() { return vendorResponse; }
    public void setVendorResponse(String vendorResponse) { this.vendorResponse = vendorResponse; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
