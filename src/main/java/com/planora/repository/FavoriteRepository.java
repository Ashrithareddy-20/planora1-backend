package com.planora.repository;

import com.planora.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Favorite> findByUserIdAndVendorId(Long userId, Long vendorId);
    Boolean existsByUserIdAndVendorId(Long userId, Long vendorId);
    void deleteByUserIdAndVendorId(Long userId, Long vendorId);
}
