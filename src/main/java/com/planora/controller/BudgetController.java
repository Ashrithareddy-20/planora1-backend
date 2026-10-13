package com.planora.controller;

import com.planora.dto.BudgetItemDTO;
import com.planora.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budget")
@Tag(name = "Budget", description = "Event budget planning, estimation and category allocation")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get all budget items for an event")
    public ResponseEntity<List<BudgetItemDTO>> getBudgetItems(@PathVariable Long eventId) {
        return ResponseEntity.ok(budgetService.getBudgetItems(eventId));
    }

    @GetMapping("/event/{eventId}/summary")
    @Operation(summary = "Get detailed budget breakdown and summary for an event")
    public ResponseEntity<Map<String, Object>> getBudgetSummary(@PathVariable Long eventId) {
        return ResponseEntity.ok(budgetService.getBudgetSummary(eventId));
    }

    @PostMapping
    @Operation(summary = "Create a new budget item")
    public ResponseEntity<BudgetItemDTO> createBudgetItem(
            @Valid @RequestBody BudgetItemDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(budgetService.createBudgetItem(dto, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing budget item")
    public ResponseEntity<BudgetItemDTO> updateBudgetItem(
            @PathVariable Long id,
            @Valid @RequestBody BudgetItemDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(budgetService.updateBudgetItem(id, dto, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a budget item")
    public ResponseEntity<Void> deleteBudgetItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        budgetService.deleteBudgetItem(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
