package com.planora.service;

import com.planora.dto.BookingDTO;
import com.planora.exception.BadRequestException;
import com.planora.exception.ResourceNotFoundException;
import com.planora.exception.UnauthorizedException;
import com.planora.model.*;
import com.planora.repository.BookingRequestRepository;
import com.planora.repository.EventRepository;
import com.planora.repository.UserRepository;
import com.planora.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRequestRepository bookingRequestRepository;
    private final VendorRepository vendorRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRequestRepository bookingRequestRepository,
                          VendorRepository vendorRepository,
                          EventRepository eventRepository,
                          UserRepository userRepository) {
        this.bookingRequestRepository = bookingRequestRepository;
        this.vendorRepository = vendorRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingDTO createBooking(BookingDTO dto, String userEmail) {
        User user = getUserByEmail(userEmail);
        Vendor vendor = vendorRepository.findById(dto.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + dto.getVendorId()));

        Event event = null;
        if (dto.getEventId() != null) {
            event = eventRepository.findById(dto.getEventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + dto.getEventId()));
            if (!event.getUser().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
                throw new UnauthorizedException("Event does not belong to you");
            }
        }

        BookingRequest booking = new BookingRequest(
                user,
                vendor,
                event,
                dto.getRequestedDate(),
                dto.getEstimatedPrice() != null ? dto.getEstimatedPrice() : vendor.getPriceStarting(),
                dto.getMessage(),
                BookingRequest.BookingStatus.PENDING
        );

        BookingRequest saved = bookingRequestRepository.save(booking);
        return toDTO(saved);
    }

    public List<BookingDTO> getUserBookings(String userEmail) {
        User user = getUserByEmail(userEmail);
        return bookingRequestRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getVendorBookings(Long vendorId, String userEmail) {
        User user = getUserByEmail(userEmail);
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));

        if (user.getRole() != Role.ROLE_ADMIN && (vendor.getOwner() == null || !vendor.getOwner().getId().equals(user.getId()))) {
            throw new UnauthorizedException("You are not the owner of this vendor profile");
        }

        return bookingRequestRepository.findByVendorIdOrderByCreatedAtDesc(vendorId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDTO updateBookingStatus(Long bookingId, BookingRequest.BookingStatus status, String responseNote, String userEmail) {
        BookingRequest booking = bookingRequestRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking request not found with ID: " + bookingId));
        User user = getUserByEmail(userEmail);

        if (status == BookingRequest.BookingStatus.CANCELLED) {
            // User can cancel their own booking
            if (!booking.getUser().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
                throw new UnauthorizedException("You cannot cancel this booking");
            }
        } else if (status == BookingRequest.BookingStatus.ACCEPTED || status == BookingRequest.BookingStatus.REJECTED) {
            // Vendor owner or Admin can accept/reject
            Vendor vendor = booking.getVendor();
            if (user.getRole() != Role.ROLE_ADMIN && (vendor.getOwner() == null || !vendor.getOwner().getId().equals(user.getId()))) {
                throw new UnauthorizedException("Only the vendor owner or admin can update this booking status");
            }
        }

        booking.setStatus(status);
        if (responseNote != null) {
            booking.setVendorResponse(responseNote);
        }

        return toDTO(bookingRequestRepository.save(booking));
    }

    public BookingDTO toDTO(BookingRequest b) {
        BookingDTO dto = new BookingDTO();
        dto.setId(b.getId());
        if (b.getEvent() != null) {
            dto.setEventId(b.getEvent().getId());
            dto.setEventTitle(b.getEvent().getTitle());
        }
        dto.setVendorId(b.getVendor().getId());
        dto.setVendorName(b.getVendor().getName());
        dto.setVendorCategory(b.getVendor().getCategory());
        dto.setVendorImage(b.getVendor().getImageUrl());
        dto.setVendorCity(b.getVendor().getCity());

        dto.setUserId(b.getUser().getId());
        dto.setUserName(b.getUser().getName());
        dto.setUserEmail(b.getUser().getEmail());
        dto.setUserPhone(b.getUser().getPhone());

        dto.setRequestedDate(b.getRequestedDate());
        dto.setEstimatedPrice(b.getEstimatedPrice());
        dto.setMessage(b.getMessage());
        dto.setStatus(b.getStatus());
        dto.setVendorResponse(b.getVendorResponse());
        dto.setCreatedAt(b.getCreatedAt());
        return dto;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
