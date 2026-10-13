package com.planora.dto;

import java.util.List;

public class RecommendationResponseDTO {
    private VendorDTO vendor;
    private Double matchScore; // 0 to 100
    private String matchReason;
    private String budgetFitStatus; // "Within Budget", "Slightly Over Budget", "Great Value"
    private Boolean capacityMatch;
    private List<String> highlights;

    public RecommendationResponseDTO() {}

    public RecommendationResponseDTO(VendorDTO vendor, Double matchScore, String matchReason,
                                     String budgetFitStatus, Boolean capacityMatch, List<String> highlights) {
        this.vendor = vendor;
        this.matchScore = matchScore;
        this.matchReason = matchReason;
        this.budgetFitStatus = budgetFitStatus;
        this.capacityMatch = capacityMatch;
        this.highlights = highlights;
    }

    public VendorDTO getVendor() { return vendor; }
    public void setVendor(VendorDTO vendor) { this.vendor = vendor; }
    public Double getMatchScore() { return matchScore; }
    public void setMatchScore(Double matchScore) { this.matchScore = matchScore; }
    public String getMatchReason() { return matchReason; }
    public void setMatchReason(String matchReason) { this.matchReason = matchReason; }
    public String getBudgetFitStatus() { return budgetFitStatus; }
    public void setBudgetFitStatus(String budgetFitStatus) { this.budgetFitStatus = budgetFitStatus; }
    public Boolean getCapacityMatch() { return capacityMatch; }
    public void setCapacityMatch(Boolean capacityMatch) { this.capacityMatch = capacityMatch; }
    public List<String> getHighlights() { return highlights; }
    public void setHighlights(List<String> highlights) { this.highlights = highlights; }
}
