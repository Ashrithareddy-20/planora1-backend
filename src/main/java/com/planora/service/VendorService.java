package com.planora.service;

import com.planora.dto.VendorDTO;
import com.planora.exception.ResourceNotFoundException;
import com.planora.exception.UnauthorizedException;
import com.planora.model.Role;
import com.planora.model.User;
import com.planora.model.Vendor;
import com.planora.model.VendorCategory;
import com.planora.repository.FavoriteRepository;
import com.planora.repository.UserRepository;
import com.planora.repository.VendorRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VendorService {

    private final VendorRepository vendorRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    public VendorService(VendorRepository vendorRepository,
                         FavoriteRepository favoriteRepository,
                         UserRepository userRepository) {
        this.vendorRepository = vendorRepository;
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
    }

    public List<VendorDTO> getVendors(VendorCategory category, String city, Double maxPrice,
                                      Integer minCapacity, String query, String currentUserEmail) {
        List<Vendor> vendors = vendorRepository.filterVendors(category, city, maxPrice, minCapacity, query);
        Long currentUserId = getCurrentUserIdOrNull(currentUserEmail);

        return vendors.stream()
                .map(v -> toDTO(v, currentUserId))
                .collect(Collectors.toList());
    }

    public VendorDTO getVendorById(Long id, String currentUserEmail) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + id));
        Long currentUserId = getCurrentUserIdOrNull(currentUserEmail);
        return toDTO(vendor, currentUserId);
    }

    public List<VendorDTO> compareVendors(List<Long> ids, String currentUserEmail) {
        Long currentUserId = getCurrentUserIdOrNull(currentUserEmail);
        return vendorRepository.findAllById(ids).stream()
                .map(v -> toDTO(v, currentUserId))
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "vendors", allEntries = true)
    public VendorDTO createVendor(VendorDTO dto, String currentUserEmail) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserEmail));

        Vendor vendor = new Vendor();
        populateVendorFromDTO(vendor, dto);
        vendor.setOwner(user);
        vendor.setRating(5.0);
        vendor.setReviewCount(0);

        Vendor saved = vendorRepository.save(vendor);
        return toDTO(saved, user.getId());
    }

    @Transactional
    @CacheEvict(value = "vendors", allEntries = true)
    public VendorDTO updateVendor(Long id, VendorDTO dto, String currentUserEmail) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + id));
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserEmail));

        if (user.getRole() != Role.ROLE_ADMIN && (vendor.getOwner() == null || !vendor.getOwner().getId().equals(user.getId()))) {
            throw new UnauthorizedException("You are not authorized to update this vendor profile");
        }

        populateVendorFromDTO(vendor, dto);
        Vendor updated = vendorRepository.save(vendor);
        return toDTO(updated, user.getId());
    }

    @Transactional
    @CacheEvict(value = "vendors", allEntries = true)
    public void deleteVendor(Long id, String currentUserEmail) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + id));
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + currentUserEmail));

        if (user.getRole() != Role.ROLE_ADMIN && (vendor.getOwner() == null || !vendor.getOwner().getId().equals(user.getId()))) {
            throw new UnauthorizedException("You are not authorized to delete this vendor");
        }

        vendorRepository.delete(vendor);
    }

    public List<VendorDTO> getVendorsByOwner(String ownerEmail) {
        User user = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + ownerEmail));
        return vendorRepository.findByOwnerId(user.getId()).stream()
                .map(v -> toDTO(v, user.getId()))
                .collect(Collectors.toList());
    }

    private void populateVendorFromDTO(Vendor vendor, VendorDTO dto) {
        vendor.setName(dto.getName());
        vendor.setCategory(dto.getCategory());
        vendor.setDescription(dto.getDescription());
        vendor.setPriceStarting(dto.getPriceStarting());
        vendor.setMaxGuestCapacity(dto.getMaxGuestCapacity());
        vendor.setCity(dto.getCity());
        vendor.setAddress(dto.getAddress());
        vendor.setContactPhone(dto.getContactPhone());
        vendor.setContactEmail(dto.getContactEmail());
        if (dto.getImageUrl() != null) vendor.setImageUrl(dto.getImageUrl());
        if (dto.getFeatured() != null) vendor.setFeatured(dto.getFeatured());
    }

    public VendorDTO toDTO(Vendor v, Long currentUserId) {
        VendorDTO dto = new VendorDTO();
        dto.setId(v.getId());
        dto.setName(v.getName());
        dto.setCategory(v.getCategory());
        dto.setDescription(v.getDescription());
        dto.setPriceStarting(v.getPriceStarting());
        dto.setMaxGuestCapacity(v.getMaxGuestCapacity());
        dto.setCity(v.getCity());
        dto.setAddress(v.getAddress());
        dto.setContactPhone(v.getContactPhone());
        dto.setContactEmail(v.getContactEmail());
        dto.setRating(v.getRating());
        dto.setReviewCount(v.getReviewCount());
        dto.setImageUrl(v.getImageUrl());
        dto.setFeatured(v.getFeatured());
        dto.setOwnerId(v.getOwner() != null ? v.getOwner().getId() : null);
        dto.setCreatedAt(v.getCreatedAt());

        if (currentUserId != null) {
            dto.setIsFavorite(favoriteRepository.existsByUserIdAndVendorId(currentUserId, v.getId()));
        } else {
            dto.setIsFavorite(false);
        }
        return dto;
    }

    private Long getCurrentUserIdOrNull(String email) {
        if (email == null || email.isBlank()) return null;
        return userRepository.findByEmail(email).map(User::getId).orElse(null);
    }
}
