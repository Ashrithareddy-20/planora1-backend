package com.planora.service;

import com.planora.dto.ReviewDTO;
import com.planora.exception.ResourceNotFoundException;
import com.planora.exception.UnauthorizedException;
import com.planora.model.Review;
import com.planora.model.Role;
import com.planora.model.User;
import com.planora.model.Vendor;
import com.planora.repository.ReviewRepository;
import com.planora.repository.UserRepository;
import com.planora.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         VendorRepository vendorRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.vendorRepository = vendorRepository;
        this.userRepository = userRepository;
    }

    public List<ReviewDTO> getVendorReviews(Long vendorId) {
        return reviewRepository.findByVendorIdOrderByCreatedAtDesc(vendorId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewDTO addReview(ReviewDTO dto, String userEmail) {
        User user = getUserByEmail(userEmail);
        Vendor vendor = vendorRepository.findById(dto.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + dto.getVendorId()));

        Review review = new Review(user, vendor, dto.getRating(), dto.getComment());
        Review saved = reviewRepository.save(review);

        // Recalculate average rating and review count
        Double avgRating = reviewRepository.calculateAverageRatingForVendor(vendor.getId());
        Integer count = reviewRepository.countReviewsForVendor(vendor.getId());

        vendor.setRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 5.0);
        vendor.setReviewCount(count != null ? count : 0);
        vendorRepository.save(vendor);

        return toDTO(saved);
    }

    @Transactional
    public void deleteReview(Long reviewId, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));
        User user = getUserByEmail(userEmail);

        if (!review.getUser().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("Not authorized to delete this review");
        }

        Vendor vendor = review.getVendor();
        reviewRepository.delete(review);

        Double avgRating = reviewRepository.calculateAverageRatingForVendor(vendor.getId());
        Integer count = reviewRepository.countReviewsForVendor(vendor.getId());

        vendor.setRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 5.0);
        vendor.setReviewCount(count != null ? count : 0);
        vendorRepository.save(vendor);
    }

    public ReviewDTO toDTO(Review r) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(r.getId());
        dto.setVendorId(r.getVendor().getId());
        dto.setVendorName(r.getVendor().getName());
        dto.setUserId(r.getUser().getId());
        dto.setUserName(r.getUser().getName());
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
