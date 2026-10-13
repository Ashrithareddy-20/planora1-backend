package com.planora.controller;

import com.planora.dto.VendorDTO;
import com.planora.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "User favorite vendors collection")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    @Operation(summary = "Get user's favorite vendors")
    public ResponseEntity<List<VendorDTO>> getFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(favoriteService.getUserFavorites(userDetails.getUsername()));
    }

    @PostMapping("/{vendorId}/toggle")
    @Operation(summary = "Toggle favorite status for vendor")
    public ResponseEntity<Map<String, Object>> toggleFavorite(
            @PathVariable Long vendorId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean isFavorite = favoriteService.toggleFavorite(vendorId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("vendorId", vendorId, "isFavorite", isFavorite));
    }
}
