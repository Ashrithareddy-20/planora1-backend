package com.planora.controller;

import com.planora.dto.BookingDTO;
import com.planora.dto.ReviewDTO;
import com.planora.dto.UserProfileDTO;
import com.planora.model.Role;
import com.planora.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administration", description = "Admin dashboard, role moderation, and platform management")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get platform-wide statistics")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(adminService.getPlatformStats());
    }

    @GetMapping("/users")
    @Operation(summary = "Get list of all users")
    public ResponseEntity<List<UserProfileDTO>> getUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/users/{userId}/role")
    @Operation(summary = "Update user role")
    public ResponseEntity<UserProfileDTO> updateUserRole(
            @PathVariable Long userId,
            @RequestBody Map<String, String> payload) {
        Role role = Role.valueOf(payload.get("role"));
        return ResponseEntity.ok(adminService.updateUserRole(userId, role));
    }

    @GetMapping("/bookings")
    @Operation(summary = "Get all platform bookings")
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        return ResponseEntity.ok(adminService.getAllBookings());
    }

    @GetMapping("/reviews")
    @Operation(summary = "Get all platform reviews for moderation")
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        return ResponseEntity.ok(adminService.getAllReviews());
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "Delete an inappropriate review")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        adminService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
