package com.planora.repository;

import com.planora.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByVendorIdOrderByCreatedAtDesc(Long vendorId);
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.vendor.id = :vendorId")
    Double calculateAverageRatingForVendor(@Param("vendorId") Long vendorId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.vendor.id = :vendorId")
    Integer countReviewsForVendor(@Param("vendorId") Long vendorId);
}
