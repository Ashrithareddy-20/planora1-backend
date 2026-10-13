package com.planora.repository;

import com.planora.model.Vendor;
import com.planora.model.VendorCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    List<Vendor> findByCategory(VendorCategory category);
    List<Vendor> findByCityIgnoreCase(String city);
    List<Vendor> findByFeaturedTrue();

    @Query("SELECT v FROM Vendor v WHERE " +
           "(:category IS NULL OR v.category = :category) AND " +
           "(:city IS NULL OR LOWER(v.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:maxPrice IS NULL OR v.priceStarting <= :maxPrice) AND " +
           "(:minCapacity IS NULL OR v.maxGuestCapacity >= :minCapacity) AND " +
           "(:query IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(v.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Vendor> filterVendors(@Param("category") VendorCategory category,
                               @Param("city") String city,
                               @Param("maxPrice") Double maxPrice,
                               @Param("minCapacity") Integer minCapacity,
                               @Param("query") String query);

    List<Vendor> findByOwnerId(Long ownerId);
}
