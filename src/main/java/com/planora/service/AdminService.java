package com.planora.service;

import com.planora.dto.BookingDTO;
import com.planora.dto.ReviewDTO;
import com.planora.dto.UserProfileDTO;
import com.planora.dto.VendorDTO;
import com.planora.exception.ResourceNotFoundException;
import com.planora.model.Role;
import com.planora.model.User;
import com.planora.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final EventRepository eventRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final ReviewRepository reviewRepository;
    private final VendorService vendorService;
    private final BookingService bookingService;
    private final ReviewService reviewService;

    public AdminService(UserRepository userRepository,
                        VendorRepository vendorRepository,
                        EventRepository eventRepository,
                        BookingRequestRepository bookingRequestRepository,
                        ReviewRepository reviewRepository,
                        VendorService vendorService,
                        BookingService bookingService,
                        ReviewService reviewService) {
        this.userRepository = userRepository;
        this.vendorRepository = vendorRepository;
        this.eventRepository = eventRepository;
        this.bookingRequestRepository = bookingRequestRepository;
        this.reviewRepository = reviewRepository;
        this.vendorService = vendorService;
        this.bookingService = bookingService;
        this.reviewService = reviewService;
    }

    public Map<String, Object> getPlatformStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalVendors", vendorRepository.count());
        stats.put("totalEvents", eventRepository.count());
        stats.put("totalBookings", bookingRequestRepository.count());
        stats.put("totalReviews", reviewRepository.count());
        return stats;
    }

    public List<UserProfileDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserProfileDTO(u.getId(), u.getName(), u.getEmail(), u.getPhone(), u.getRole(), u.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserProfileDTO updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setRole(role);
        User updated = userRepository.save(user);
        return new UserProfileDTO(updated.getId(), updated.getName(), updated.getEmail(), updated.getPhone(), updated.getRole(), updated.getCreatedAt());
    }

    public List<BookingDTO> getAllBookings() {
        return bookingRequestRepository.findAll().stream()
                .map(bookingService::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(reviewService::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
