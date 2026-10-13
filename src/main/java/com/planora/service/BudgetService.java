package com.planora.service;

import com.planora.dto.BudgetItemDTO;
import com.planora.exception.ResourceNotFoundException;
import com.planora.exception.UnauthorizedException;
import com.planora.model.BudgetItem;
import com.planora.model.Event;
import com.planora.model.Role;
import com.planora.model.User;
import com.planora.model.VendorCategory;
import com.planora.repository.BudgetItemRepository;
import com.planora.repository.EventRepository;
import com.planora.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    private final BudgetItemRepository budgetItemRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public BudgetService(BudgetItemRepository budgetItemRepository,
                         EventRepository eventRepository,
                         UserRepository userRepository) {
        this.budgetItemRepository = budgetItemRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<BudgetItemDTO> getBudgetItems(Long eventId) {
        return budgetItemRepository.findByEventId(eventId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BudgetItemDTO createBudgetItem(BudgetItemDTO dto, String userEmail) {
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + dto.getEventId()));
        verifyEventOwnership(event, userEmail);

        BudgetItem item = new BudgetItem(
                event,
                dto.getCategory(),
                dto.getTitle(),
                dto.getAllocatedAmount(),
                dto.getActualAmount() != null ? dto.getActualAmount() : 0.0,
                dto.getNotes()
        );

        return toDTO(budgetItemRepository.save(item));
    }

    @Transactional
    public BudgetItemDTO updateBudgetItem(Long id, BudgetItemDTO dto, String userEmail) {
        BudgetItem item = budgetItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget item not found with ID: " + id));
        verifyEventOwnership(item.getEvent(), userEmail);

        item.setCategory(dto.getCategory());
        item.setTitle(dto.getTitle());
        item.setAllocatedAmount(dto.getAllocatedAmount());
        if (dto.getActualAmount() != null) {
            item.setActualAmount(dto.getActualAmount());
        }
        item.setNotes(dto.getNotes());

        return toDTO(budgetItemRepository.save(item));
    }

    @Transactional
    public void deleteBudgetItem(Long id, String userEmail) {
        BudgetItem item = budgetItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget item not found with ID: " + id));
        verifyEventOwnership(item.getEvent(), userEmail);
        budgetItemRepository.delete(item);
    }

    public Map<String, Object> getBudgetSummary(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
        List<BudgetItem> items = budgetItemRepository.findByEventId(eventId);

        double totalBudget = event.getTotalBudget();
        double totalAllocated = items.stream().mapToDouble(b -> b.getAllocatedAmount() != null ? b.getAllocatedAmount() : 0.0).sum();
        double totalSpent = items.stream().mapToDouble(b -> b.getActualAmount() != null ? b.getActualAmount() : 0.0).sum();
        double remaining = totalBudget - totalAllocated;

        Map<VendorCategory, Double> categoryAllocations = new EnumMap<>(VendorCategory.class);
        Map<VendorCategory, Double> categoryActuals = new EnumMap<>(VendorCategory.class);

        for (BudgetItem b : items) {
            categoryAllocations.put(b.getCategory(),
                    categoryAllocations.getOrDefault(b.getCategory(), 0.0) + (b.getAllocatedAmount() != null ? b.getAllocatedAmount() : 0.0));
            categoryActuals.put(b.getCategory(),
                    categoryActuals.getOrDefault(b.getCategory(), 0.0) + (b.getActualAmount() != null ? b.getActualAmount() : 0.0));
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("eventId", eventId);
        summary.put("eventTitle", event.getTitle());
        summary.put("totalBudget", totalBudget);
        summary.put("totalAllocated", totalAllocated);
        summary.put("totalSpent", totalSpent);
        summary.put("remainingBudget", Math.max(0.0, remaining));
        summary.put("isOverBudget", totalAllocated > totalBudget);
        summary.put("categoryAllocations", categoryAllocations);
        summary.put("categoryActuals", categoryActuals);
        summary.put("items", items.stream().map(this::toDTO).collect(Collectors.toList()));

        return summary;
    }

    @Transactional
    public void seedDefaultBudget(Event event) {
        double budget = event.getTotalBudget();
        // Common event allocation distribution
        createTemplateItem(event, VendorCategory.VENUE, "Venue Rental & Setup", budget * 0.35);
        createTemplateItem(event, VendorCategory.CATERING, "Catering & Beverage Service", budget * 0.25);
        createTemplateItem(event, VendorCategory.DECORATION, "Floral & Theme Decoration", budget * 0.15);
        createTemplateItem(event, VendorCategory.PHOTOGRAPHY, "Photography & Cinematography", budget * 0.10);
        createTemplateItem(event, VendorCategory.ENTERTAINMENT, "DJ, Sound & Lights", budget * 0.08);
        createTemplateItem(event, VendorCategory.MAKEUP, "Styling & Makeup", budget * 0.04);
        createTemplateItem(event, VendorCategory.TRANSPORTATION, "Guest Transportation / Logistics", budget * 0.03);
    }

    private void createTemplateItem(Event event, VendorCategory category, String title, double allocated) {
        BudgetItem item = new BudgetItem(event, category, title, Math.round(allocated * 100.0) / 100.0, 0.0, "Standard initial budget allocation");
        budgetItemRepository.save(item);
    }

    private void verifyEventOwnership(Event event, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        if (!event.getUser().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("Unauthorized access to event budget");
        }
    }

    public BudgetItemDTO toDTO(BudgetItem b) {
        BudgetItemDTO dto = new BudgetItemDTO();
        dto.setId(b.getId());
        dto.setEventId(b.getEvent().getId());
        dto.setCategory(b.getCategory());
        dto.setTitle(b.getTitle());
        dto.setAllocatedAmount(b.getAllocatedAmount());
        dto.setActualAmount(b.getActualAmount());
        dto.setNotes(b.getNotes());
        dto.setCreatedAt(b.getCreatedAt());
        return dto;
    }
}
