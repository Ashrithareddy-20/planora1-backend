package com.planora.dto;

import com.planora.model.VendorCategory;
import java.time.LocalDateTime;

public class VendorDTO {
    private Long id;
    private String name;
    private VendorCategory category;
    private String description;
    private Double priceStarting;
    private Integer maxGuestCapacity;
    private String city;
    private String address;
    private String contactPhone;
    private String contactEmail;
    private Double rating;
    private Integer reviewCount;
    private String imageUrl;
    private Boolean featured;
    private Boolean isFavorite;
    private Long ownerId;
    private LocalDateTime createdAt;

    public VendorDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public VendorCategory getCategory() { return category; }
    public void setCategory(VendorCategory category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPriceStarting() { return priceStarting; }
    public void setPriceStarting(Double priceStarting) { this.priceStarting = priceStarting; }
    public Integer getMaxGuestCapacity() { return maxGuestCapacity; }
    public void setMaxGuestCapacity(Integer maxGuestCapacity) { this.maxGuestCapacity = maxGuestCapacity; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Boolean getFeatured() { return featured; }
    public void setFeatured(Boolean featured) { this.featured = featured; }
    public Boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(Boolean favorite) { isFavorite = favorite; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
