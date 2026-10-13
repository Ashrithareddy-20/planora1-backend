package com.planora.repository;

import com.planora.model.BookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRequestRepository extends JpaRepository<BookingRequest, Long> {
    List<BookingRequest> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<BookingRequest> findByVendorIdOrderByCreatedAtDesc(Long vendorId);
    List<BookingRequest> findByEventIdOrderByCreatedAtDesc(Long eventId);
    Long countByUserId(Long userId);
}
