package com.planora.service;

import com.planora.dto.BookingDTO;
import com.planora.dto.DashboardAnalyticsDTO;
import com.planora.dto.EventDTO;
import com.planora.dto.VendorDTO;
import com.planora.exception.ResourceNotFoundException;
import com.planora.model.*;
import com.planora.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final EventRepository eventRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final ChecklistTaskRepository checklistTaskRepository;
    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final EventService eventService;
    private final BookingService bookingService;
    private final VendorService vendorService;

    public DashboardService(EventRepository eventRepository,
                            BookingRequestRepository bookingRequestRepository,
                            BudgetItemRepository budgetItemRepository,
                            ChecklistTaskRepository checklistTaskRepository,
                            VendorRepository vendorRepository,
                            UserRepository userRepository,
                            EventService eventService,
                            BookingService bookingService,
                            VendorService vendorService) {
        this.eventRepository = eventRepository;
        this.bookingRequestRepository = bookingRequestRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.checklistTaskRepository = checklistTaskRepository;
        this.vendorRepository = vendorRepository;
        this.userRepository = userRepository;
        this.eventService = eventService;
        this.bookingService = bookingService;
        this.vendorService = vendorService;
    }

    public DashboardAnalyticsDTO getUserDashboard(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        List<Event> userEvents = eventRepository.findByUserIdOrderByEventDateAsc(user.getId());
        List<BookingRequest> userBookings = bookingRequestRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        DashboardAnalyticsDTO dto = new DashboardAnalyticsDTO();
        dto.setTotalEvents((long) userEvents.size());
        dto.setUpcomingEvents(userEvents.stream().filter(e -> e.getStatus() == Event.EventStatus.UPCOMING).count());
        dto.setCompletedEvents(userEvents.stream().filter(e -> e.getStatus() == Event.EventStatus.COMPLETED).count());
        dto.setPendingBookings(userBookings.stream().filter(b -> b.getStatus() == BookingRequest.BookingStatus.PENDING).count());

        double totalBudget = userEvents.stream().mapToDouble(Event::getTotalBudget).sum();
        dto.setTotalBudget(totalBudget);

        double totalAllocated = 0.0;
        double totalSpent = 0.0;
        Map<String, Double> categoryBudgets = new HashMap<>();
        Map<String, Long> eventsByType = new HashMap<>();

        long totalTasks = 0;
        long completedTasks = 0;

        for (Event event : userEvents) {
            eventsByType.put(event.getEventType(), eventsByType.getOrDefault(event.getEventType(), 0L) + 1);

            List<BudgetItem> items = budgetItemRepository.findByEventId(event.getId());
            for (BudgetItem item : items) {
                double alloc = item.getAllocatedAmount() != null ? item.getAllocatedAmount() : 0.0;
                double spent = item.getActualAmount() != null ? item.getActualAmount() : 0.0;
                totalAllocated += alloc;
                totalSpent += spent;

                String cat = item.getCategory().name();
                categoryBudgets.put(cat, categoryBudgets.getOrDefault(cat, 0.0) + alloc);
            }

            totalTasks += checklistTaskRepository.countByEventId(event.getId());
            completedTasks += checklistTaskRepository.countByEventIdAndCompletedTrue(event.getId());
        }

        dto.setTotalAllocated(totalAllocated);
        dto.setTotalSpent(totalSpent);
        dto.setRemainingBudget(Math.max(0.0, totalBudget - totalAllocated));
        dto.setCategoryBudgets(categoryBudgets);
        dto.setEventsByType(eventsByType);

        if (totalTasks > 0) {
            dto.setOverallChecklistProgress((int) Math.round(((double) completedTasks / totalTasks) * 100));
        } else {
            dto.setOverallChecklistProgress(0);
        }

        // Recent events & bookings
        List<EventDTO> recentEvents = userEvents.stream()
                .limit(5)
                .map(eventService::toDTO)
                .collect(Collectors.toList());
        dto.setRecentEvents(recentEvents);

        List<BookingDTO> recentBookings = userBookings.stream()
                .limit(5)
                .map(bookingService::toDTO)
                .collect(Collectors.toList());
        dto.setRecentBookings(recentBookings);

        // Featured/recommended vendors
        List<VendorDTO> recommended = vendorRepository.findByFeaturedTrue().stream()
                .limit(4)
                .map(v -> vendorService.toDTO(v, user.getId()))
                .collect(Collectors.toList());
        dto.setRecommendedVendors(recommended);

        return dto;
    }
}
