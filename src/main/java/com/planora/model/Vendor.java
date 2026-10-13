package com.planora.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendors")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VendorCategory category;

    @Column(length = 2000)
    private String description;

    private Double priceStarting;

    private Integer maxGuestCapacity;

    @Column(nullable = false)
    private String city;

    private String address;

    private String contactPhone;

    private String contactEmail;

    private Double rating = 4.5;

    private Integer reviewCount = 0;

    @Column(length = 1000)
    private String imageUrl;

    private Boolean featured = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Vendor() {}

    public Vendor(String name, VendorCategory category, String description, Double priceStarting,
                  Integer maxGuestCapacity, String city, String address, String contactPhone,
                  String contactEmail, Double rating, Integer reviewCount, String imageUrl, Boolean featured) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.priceStarting = priceStarting;
        this.maxGuestCapacity = maxGuestCapacity;
        this.city = city;
        this.address = address;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.imageUrl = imageUrl;
        this.featured = featured;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VendorCategory getCategory() {
        return category;
    }

    public void setCategory(VendorCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPriceStarting() {
        return priceStarting;
    }

    public void setPriceStarting(Double priceStarting) {
        this.priceStarting = priceStarting;
    }

    public Integer getMaxGuestCapacity() {
        return maxGuestCapacity;
    }

    public void setMaxGuestCapacity(Integer maxGuestCapacity) {
        this.maxGuestCapacity = maxGuestCapacity;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
