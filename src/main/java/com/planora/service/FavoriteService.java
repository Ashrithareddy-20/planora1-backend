package com.planora.service;

import com.planora.dto.VendorDTO;
import com.planora.exception.ResourceNotFoundException;
import com.planora.model.Favorite;
import com.planora.model.User;
import com.planora.model.Vendor;
import com.planora.repository.FavoriteRepository;
import com.planora.repository.UserRepository;
import com.planora.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final VendorService vendorService;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           UserRepository userRepository,
                           VendorRepository vendorRepository,
                           VendorService vendorService) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.vendorRepository = vendorRepository;
        this.vendorService = vendorService;
    }

    @Transactional
    public boolean toggleFavorite(Long vendorId, String userEmail) {
        User user = getUserByEmail(userEmail);
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + vendorId));

        Optional<Favorite> existing = favoriteRepository.findByUserIdAndVendorId(user.getId(), vendor.getId());
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return false; // Removed
        } else {
            Favorite favorite = new Favorite(user, vendor);
            favoriteRepository.save(favorite);
            return true; // Added
        }
    }

    public List<VendorDTO> getUserFavorites(String userEmail) {
        User user = getUserByEmail(userEmail);
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(f -> vendorService.toDTO(f.getVendor(), user.getId()))
                .collect(Collectors.toList());
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
