package com.planora.service;

import com.planora.dto.RecommendationRequestDTO;
import com.planora.dto.RecommendationResponseDTO;
import com.planora.dto.VendorDTO;
import com.planora.model.Event;
import com.planora.model.Vendor;
import com.planora.model.VendorCategory;
import com.planora.repository.EventRepository;
import com.planora.repository.VendorRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final VendorRepository vendorRepository;
    private final EventRepository eventRepository;
    private final VendorService vendorService;

    public RecommendationService(VendorRepository vendorRepository,
                                  EventRepository eventRepository,
                                  VendorService vendorService) {
        this.vendorRepository = vendorRepository;
        this.eventRepository = eventRepository;
        this.vendorService = vendorService;
    }

    public List<RecommendationResponseDTO> recommendForEvent(Long eventId, VendorCategory preferredCategory, String currentUserEmail) {
        Event event = eventRepository.findById(eventId).orElse(null);

        RecommendationRequestDTO request = new RecommendationRequestDTO();
        if (event != null) {
            request.setEventId(event.getId());
            request.setEventType(event.getEventType());
            request.setLocation(event.getLocation());
            request.setDate(event.getEventDate());
            request.setGuestCount(event.getGuestCount());
            request.setBudget(event.getTotalBudget());
        }
        request.setPreferredCategory(preferredCategory);

        return recommendVendors(request, currentUserEmail);
    }

    public List<RecommendationResponseDTO> recommendVendors(RecommendationRequestDTO req, String currentUserEmail) {
        List<Vendor> allVendors = vendorRepository.findAll();
        List<RecommendationResponseDTO> recommendations = new ArrayList<>();

        String targetCity = req.getLocation() != null ? req.getLocation().trim().toLowerCase() : "";
        Double totalBudget = req.getBudget() != null ? req.getBudget() : 100000.0;
        Integer guestCount = req.getGuestCount() != null ? req.getGuestCount() : 100;
        String eventType = req.getEventType() != null ? req.getEventType() : "Event";
        VendorCategory targetCategory = req.getPreferredCategory();

        for (Vendor vendor : allVendors) {
            // If category was specified, filter by it; otherwise consider all
            if (targetCategory != null && vendor.getCategory() != targetCategory) {
                continue;
            }

            double score = 50.0; // Base score
            List<String> highlights = new ArrayList<>();
            String matchReason;

            // 1. Location match
            boolean cityMatches = targetCity.isEmpty() || vendor.getCity().toLowerCase().contains(targetCity)
                    || targetCity.contains(vendor.getCity().toLowerCase());
            if (cityMatches) {
                score += 25.0;
                highlights.add("Located in " + vendor.getCity() + " (Exact City Match)");
            } else {
                score += 5.0;
                highlights.add("Located in " + vendor.getCity() + " (Out-of-city option)");
            }

            // 2. Budget match
            // Allocate estimated ceiling based on category weight
            double categoryBudgetWeight = getCategoryWeight(vendor.getCategory());
            double estimatedCategoryBudget = totalBudget * categoryBudgetWeight;
            String budgetFitStatus;

            if (vendor.getPriceStarting() <= estimatedCategoryBudget) {
                score += 20.0;
                budgetFitStatus = "Great Value (Within estimated " + vendor.getCategory() + " allocation)";
                highlights.add("Comfortably within your " + vendor.getCategory() + " budget allowance");
            } else if (vendor.getPriceStarting() <= estimatedCategoryBudget * 1.3) {
                score += 10.0;
                budgetFitStatus = "Slightly Above Average (Premium Quality)";
                highlights.add("Premium option, slightly above standard allocation");
            } else {
                score -= 10.0;
                budgetFitStatus = "High End / Luxury";
                highlights.add("Luxury vendor, may require reallocating other budget categories");
            }

            // 3. Guest capacity match (especially for Venues & Catering)
            boolean capacityMatch = true;
            if (vendor.getMaxGuestCapacity() != null && vendor.getMaxGuestCapacity() > 0) {
                if (vendor.getMaxGuestCapacity() >= guestCount) {
                    score += 15.0;
                    highlights.add("Capacity for " + vendor.getMaxGuestCapacity() + " guests (Covers your " + guestCount + " guests)");
                } else {
                    capacityMatch = false;
                    score -= 15.0;
                    highlights.add("Capacity limited to " + vendor.getMaxGuestCapacity() + " guests (Below your target)");
                }
            }

            // 4. Rating & Reviews
            if (vendor.getRating() != null) {
                score += (vendor.getRating() - 3.0) * 10.0; // 4.5 -> +15 pts
                if (vendor.getRating() >= 4.7) {
                    highlights.add("Top Rated: " + vendor.getRating() + "★ based on " + vendor.getReviewCount() + " reviews");
                }
            }

            if (Boolean.TRUE.equals(vendor.getFeatured())) {
                score += 5.0;
                highlights.add("Verified Planora Premier Partner");
            }

            // Reason construction
            matchReason = String.format("Recommended for your %s in %s. %s offers professional %s starting at ₹%,.0f with an outstanding rating of %.1f★.",
                    eventType, vendor.getCity(), vendor.getName(), vendor.getCategory().name().toLowerCase(),
                    vendor.getPriceStarting() != null ? vendor.getPriceStarting() : 0.0,
                    vendor.getRating() != null ? vendor.getRating() : 5.0);

            VendorDTO vendorDto = vendorService.toDTO(vendor, null);
            double finalScore = Math.min(100.0, Math.max(10.0, Math.round(score * 10.0) / 10.0));

            recommendations.add(new RecommendationResponseDTO(
                    vendorDto,
                    finalScore,
                    matchReason,
                    budgetFitStatus,
                    capacityMatch,
                    highlights
            ));
        }

        // Sort descending by match score
        recommendations.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));

        // If top recommendations list is small and target city was strict, include alternative top picks
        return recommendations.stream().limit(12).collect(Collectors.toList());
    }

    private double getCategoryWeight(VendorCategory category) {
        return switch (category) {
            case VENUE -> 0.40;
            case CATERING -> 0.30;
            case DECORATION -> 0.18;
            case PHOTOGRAPHY -> 0.12;
            case ENTERTAINMENT -> 0.10;
            case MAKEUP -> 0.06;
            case TRANSPORTATION -> 0.05;
        };
    }
}
