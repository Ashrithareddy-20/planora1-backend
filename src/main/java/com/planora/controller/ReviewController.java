package com.planora.controller;

import com.planora.dto.ReviewDTO;
import com.planora.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "Vendor reviews and ratings")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/vendor/{vendorId}")
    @Operation(summary = "Get all reviews for a vendor")
    public ResponseEntity<List<ReviewDTO>> getVendorReviews(@PathVariable Long vendorId) {
        return ResponseEntity.ok(reviewService.getVendorReviews(vendorId));
    }

    @PostMapping
    @Operation(summary = "Submit a review for a vendor")
    public ResponseEntity<ReviewDTO> addReview(
            @Valid @RequestBody ReviewDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reviewService.addReview(dto, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.deleteReview(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
