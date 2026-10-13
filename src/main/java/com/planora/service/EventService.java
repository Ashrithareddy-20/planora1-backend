package com.planora.service;

import com.planora.dto.EventDTO;
import com.planora.exception.ResourceNotFoundException;
import com.planora.exception.UnauthorizedException;
import com.planora.model.BudgetItem;
import com.planora.model.Event;
import com.planora.model.Role;
import com.planora.model.User;
import com.planora.repository.BudgetItemRepository;
import com.planora.repository.ChecklistTaskRepository;
import com.planora.repository.EventRepository;
import com.planora.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final ChecklistTaskRepository checklistTaskRepository;
    private final ChecklistService checklistService;
    private final BudgetService budgetService;

    public EventService(EventRepository eventRepository,
                        UserRepository userRepository,
                        BudgetItemRepository budgetItemRepository,
                        ChecklistTaskRepository checklistTaskRepository,
                        ChecklistService checklistService,
                        BudgetService budgetService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.checklistTaskRepository = checklistTaskRepository;
        this.checklistService = checklistService;
        this.budgetService = budgetService;
    }

    public List<EventDTO> getUserEvents(String userEmail, Event.EventStatus status) {
        User user = getUserByEmail(userEmail);
        List<Event> events;
        if (status != null) {
            events = eventRepository.findByUserIdAndStatusOrderByEventDateAsc(user.getId(), status);
        } else {
            events = eventRepository.findByUserIdOrderByEventDateAsc(user.getId());
        }
        return events.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public EventDTO getEventById(Long eventId, String userEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
        User user = getUserByEmail(userEmail);

        if (!event.getUser().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("You do not have permission to view this event");
        }

        return toDTO(event);
    }

    @Transactional
    public EventDTO createEvent(EventDTO dto, String userEmail) {
        User user = getUserByEmail(userEmail);

        Event event = new Event(
                dto.getTitle(),
                dto.getEventType(),
                dto.getDescription(),
                dto.getEventDate(),
                dto.getLocation(),
                dto.getGuestCount(),
                dto.getTotalBudget(),
                dto.getStatus() != null ? dto.getStatus() : Event.EventStatus.UPCOMING,
                user
        );

        Event saved = eventRepository.save(event);

        // Auto-seed starter checklist tasks and budget template for the event type
        checklistService.seedDefaultChecklist(saved);
        budgetService.seedDefaultBudget(saved);

        return toDTO(saved);
    }

    @Transactional
    public EventDTO updateEvent(Long eventId, EventDTO dto, String userEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
        User user = getUserByEmail(userEmail);

        if (!event.getUser().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("You do not have permission to edit this event");
        }

        event.setTitle(dto.getTitle());
        event.setEventType(dto.getEventType());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLocation(dto.getLocation());
        event.setGuestCount(dto.getGuestCount());
        event.setTotalBudget(dto.getTotalBudget());
        if (dto.getStatus() != null) {
            event.setStatus(dto.getStatus());
        }

        Event updated = eventRepository.save(event);
        return toDTO(updated);
    }

    @Transactional
    public void deleteEvent(Long eventId, String userEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
        User user = getUserByEmail(userEmail);

        if (!event.getUser().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("You do not have permission to delete this event");
        }

        // Clean up associated budget items and checklist tasks
        budgetItemRepository.deleteByEventId(eventId);
        checklistTaskRepository.deleteByEventId(eventId);
        eventRepository.delete(event);
    }

    public EventDTO toDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setEventType(event.getEventType());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setGuestCount(event.getGuestCount());
        dto.setTotalBudget(event.getTotalBudget());
        dto.setStatus(event.getStatus());
        dto.setUserId(event.getUser().getId());
        dto.setUserName(event.getUser().getName());
        dto.setCreatedAt(event.getCreatedAt());

        // Calculate budget aggregates
        List<BudgetItem> items = budgetItemRepository.findByEventId(event.getId());
        double allocated = items.stream().mapToDouble(b -> b.getAllocatedAmount() != null ? b.getAllocatedAmount() : 0.0).sum();
        double spent = items.stream().mapToDouble(b -> b.getActualAmount() != null ? b.getActualAmount() : 0.0).sum();
        dto.setAllocatedBudget(allocated);
        dto.setActualSpent(spent);
        dto.setRemainingBudget(Math.max(0.0, event.getTotalBudget() - allocated));

        // Calculate checklist progress
        long totalTasks = checklistTaskRepository.countByEventId(event.getId());
        long completedTasks = checklistTaskRepository.countByEventIdAndCompletedTrue(event.getId());
        dto.setTotalTasks((int) totalTasks);
        dto.setCompletedTasks((int) completedTasks);
        if (totalTasks > 0) {
            dto.setChecklistProgress((int) Math.round(((double) completedTasks / totalTasks) * 100));
        } else {
            dto.setChecklistProgress(0);
        }

        return dto;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
