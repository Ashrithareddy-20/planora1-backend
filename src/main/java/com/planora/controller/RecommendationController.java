package com.planora.controller;

import com.planora.dto.RecommendationRequestDTO;
import com.planora.dto.RecommendationResponseDTO;
import com.planora.model.VendorCategory;
import com.planora.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "AI Recommendations", description = "AI-powered vendor & venue recommendation engine")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    @Operation(summary = "Get AI recommendations based on custom event parameters")
    public ResponseEntity<List<RecommendationResponseDTO>> getRecommendations(
            @RequestBody RecommendationRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(recommendationService.recommendVendors(request, email));
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get AI vendor recommendations directly for a saved event")
    public ResponseEntity<List<RecommendationResponseDTO>> getRecommendationsForEvent(
            @PathVariable Long eventId,
            @RequestParam(required = false) VendorCategory category,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(recommendationService.recommendForEvent(eventId, category, userDetails.getUsername()));
    }
}
