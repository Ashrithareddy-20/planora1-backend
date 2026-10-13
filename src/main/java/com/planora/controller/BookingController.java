package com.planora.controller;

import com.planora.dto.BookingDTO;
import com.planora.model.BookingRequest;
import com.planora.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Bookings", description = "Vendor booking requests and status management")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Operation(summary = "Submit a booking request to a vendor")
    public ResponseEntity<BookingDTO> createBooking(
            @Valid @RequestBody BookingDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookingService.createBooking(dto, userDetails.getUsername()));
    }

    @GetMapping("/my-requests")
    @Operation(summary = "Get bookings submitted by current user")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookingService.getUserBookings(userDetails.getUsername()));
    }

    @GetMapping("/vendor/{vendorId}")
    @Operation(summary = "Get bookings received by vendor (Vendor owner or Admin)")
    public ResponseEntity<List<BookingDTO>> getVendorBookings(
            @PathVariable Long vendorId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookingService.getVendorBookings(vendorId, userDetails.getUsername()));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update booking status (Accept/Reject by vendor, Cancel by user)")
    public ResponseEntity<BookingDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetails userDetails) {
        String statusStr = payload.get("status");
        String responseNote = payload.get("responseNote");
        BookingRequest.BookingStatus status = BookingRequest.BookingStatus.valueOf(statusStr.toUpperCase());
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status, responseNote, userDetails.getUsername()));
    }
}
