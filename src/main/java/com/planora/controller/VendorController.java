package com.planora.controller;

import com.planora.dto.VendorDTO;
import com.planora.model.VendorCategory;
import com.planora.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@Tag(name = "Vendors", description = "Browse, search, filter and manage vendors and venues")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping
    @Operation(summary = "Search and filter vendors")
    public ResponseEntity<List<VendorDTO>> getVendors(
            @RequestParam(required = false) VendorCategory category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) String query,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(vendorService.getVendors(category, city, maxPrice, minCapacity, query, email));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vendor details by ID")
    public ResponseEntity<VendorDTO> getVendorById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(vendorService.getVendorById(id, email));
    }

    @PostMapping("/compare")
    @Operation(summary = "Compare multiple vendors side-by-side")
    public ResponseEntity<List<VendorDTO>> compareVendors(
            @RequestBody List<Long> ids,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(vendorService.compareVendors(ids, email));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    @Operation(summary = "Create new vendor profile (Vendor or Admin)")
    public ResponseEntity<VendorDTO> createVendor(
            @Valid @RequestBody VendorDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(vendorService.createVendor(dto, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    @Operation(summary = "Update vendor profile (Owner or Admin)")
    public ResponseEntity<VendorDTO> updateVendor(
            @PathVariable Long id,
            @Valid @RequestBody VendorDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(vendorService.updateVendor(id, dto, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    @Operation(summary = "Delete vendor profile (Owner or Admin)")
    public ResponseEntity<Void> deleteVendor(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        vendorService.deleteVendor(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-listings")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    @Operation(summary = "Get vendor profiles managed by logged-in user")
    public ResponseEntity<List<VendorDTO>> getMyListings(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(vendorService.getVendorsByOwner(userDetails.getUsername()));
    }
}
