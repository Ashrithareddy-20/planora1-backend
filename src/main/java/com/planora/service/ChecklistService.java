package com.planora.service;

import com.planora.dto.ChecklistTaskDTO;
import com.planora.exception.ResourceNotFoundException;
import com.planora.exception.UnauthorizedException;
import com.planora.model.ChecklistTask;
import com.planora.model.Event;
import com.planora.model.Role;
import com.planora.model.User;
import com.planora.repository.ChecklistTaskRepository;
import com.planora.repository.EventRepository;
import com.planora.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChecklistService {

    private final ChecklistTaskRepository checklistTaskRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public ChecklistService(ChecklistTaskRepository checklistTaskRepository,
                            EventRepository eventRepository,
                            UserRepository userRepository) {
        this.checklistTaskRepository = checklistTaskRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<ChecklistTaskDTO> getEventTasks(Long eventId) {
        return checklistTaskRepository.findByEventIdOrderByDueDateAsc(eventId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChecklistTaskDTO createTask(ChecklistTaskDTO dto, String userEmail) {
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + dto.getEventId()));
        verifyEventOwnership(event, userEmail);

        ChecklistTask task = new ChecklistTask(
                event,
                dto.getTitle(),
                dto.getCategory() != null ? dto.getCategory() : "General",
                dto.getDueDate() != null ? dto.getDueDate() : event.getEventDate().minusDays(7),
                dto.getCompleted() != null ? dto.getCompleted() : false,
                dto.getPriority() != null ? dto.getPriority() : ChecklistTask.Priority.MEDIUM
        );

        return toDTO(checklistTaskRepository.save(task));
    }

    @Transactional
    public ChecklistTaskDTO updateTask(Long id, ChecklistTaskDTO dto, String userEmail) {
        ChecklistTask task = checklistTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist task not found with ID: " + id));
        verifyEventOwnership(task.getEvent(), userEmail);

        task.setTitle(dto.getTitle());
        if (dto.getCategory() != null) task.setCategory(dto.getCategory());
        if (dto.getDueDate() != null) task.setDueDate(dto.getDueDate());
        if (dto.getCompleted() != null) task.setCompleted(dto.getCompleted());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());

        return toDTO(checklistTaskRepository.save(task));
    }

    @Transactional
    public ChecklistTaskDTO toggleTask(Long id, String userEmail) {
        ChecklistTask task = checklistTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist task not found with ID: " + id));
        verifyEventOwnership(task.getEvent(), userEmail);

        task.setCompleted(!Boolean.TRUE.equals(task.getCompleted()));
        return toDTO(checklistTaskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id, String userEmail) {
        ChecklistTask task = checklistTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist task not found with ID: " + id));
        verifyEventOwnership(task.getEvent(), userEmail);
        checklistTaskRepository.delete(task);
    }

    @Transactional
    public void seedDefaultChecklist(Event event) {
        LocalDate eventDate = event.getEventDate();
        String type = event.getEventType().toUpperCase();

        if (type.contains("WEDDING") || type.contains("RECEPTION") || type.contains("ENGAGEMENT")) {
            createTaskEntity(event, "Finalize guest list and count", "Planning", eventDate.minusDays(45), ChecklistTask.Priority.HIGH);
            createTaskEntity(event, "Book venue and deposit advance", "Venue", eventDate.minusDays(40), ChecklistTask.Priority.HIGH);
            createTaskEntity(event, "Select catering menu and schedule food tasting", "Catering", eventDate.minusDays(30), ChecklistTask.Priority.HIGH);
            createTaskEntity(event, "Confirm theme, stage decor, and floral arrangements", "Decoration", eventDate.minusDays(25), ChecklistTask.Priority.MEDIUM);
            createTaskEntity(event, "Hire wedding photographer and videographer", "Photography", eventDate.minusDays(20), ChecklistTask.Priority.HIGH);
            createTaskEntity(event, "Send digital/physical invitations to guests", "Invitations", eventDate.minusDays(15), ChecklistTask.Priority.MEDIUM);
            createTaskEntity(event, "Finalize makeup artist and hair stylist appointments", "Makeup", eventDate.minusDays(12), ChecklistTask.Priority.MEDIUM);
            createTaskEntity(event, "Book DJ, sound system, and sound check slot", "Entertainment", eventDate.minusDays(10), ChecklistTask.Priority.MEDIUM);
            createTaskEntity(event, "Arrange logistics and guest transportation", "Transport", eventDate.minusDays(5), ChecklistTask.Priority.LOW);
            createTaskEntity(event, "Conduct final rehearsal and review vendor timeline", "Coordination", eventDate.minusDays(1), ChecklistTask.Priority.HIGH);
        } else if (type.contains("BIRTHDAY") || type.contains("PARTY")) {
            createTaskEntity(event, "Pick party theme and color palette", "Planning", eventDate.minusDays(20), ChecklistTask.Priority.MEDIUM);
            createTaskEntity(event, "Book party venue or party hall", "Venue", eventDate.minusDays(18), ChecklistTask.Priority.HIGH);
            createTaskEntity(event, "Order custom birthday cake and desserts", "Catering", eventDate.minusDays(10), ChecklistTask.Priority.HIGH);
            createTaskEntity(event, "Arrange balloon decorators and photo booth backdrop", "Decoration", eventDate.minusDays(7), ChecklistTask.Priority.MEDIUM);
            createTaskEntity(event, "Send out invitations via WhatsApp / Email", "Invitations", eventDate.minusDays(6), ChecklistTask.Priority.HIGH);
            createTaskEntity(event, "Book DJ / party host / game coordinator", "Entertainment", eventDate.minusDays(5), ChecklistTask.Priority.MEDIUM);
            createTaskEntity(event, "Purchase party favors and return gifts", "Gifts", eventDate.minusDays(3), ChecklistTask.Priority.LOW);
            createTaskEntity(event, "Confirm delivery time with cake and food vendors", "Coordination", eventDate.minusDays(1), ChecklistTask.Priority.HIGH);
        } else {
            // Corporate or generic event
            createTaskEntity(event, "Define agenda and speaker schedules", "Planning", eventDate.minusDays(30), ChecklistTask.Priority.HIGH);
            createTaskEntity(event, "Reserve conference hall and AV equipment", "Venue", eventDate.minusDays(25), ChecklistTask.Priority.HIGH);
            createTaskEntity(event, "Arrange lunch and coffee break catering", "Catering", eventDate.minusDays(18), ChecklistTask.Priority.HIGH);
            createTaskEntity(event, "Send registration links and attendee passes", "Invitations", eventDate.minusDays(14), ChecklistTask.Priority.MEDIUM);
            createTaskEntity(event, "Arrange badges, delegate kits, and signage", "Logistics", eventDate.minusDays(7), ChecklistTask.Priority.MEDIUM);
            createTaskEntity(event, "Test audio-visual presentation setup", "Technical", eventDate.minusDays(1), ChecklistTask.Priority.HIGH);
        }
    }

    private void createTaskEntity(Event event, String title, String category, LocalDate dueDate, ChecklistTask.Priority priority) {
        ChecklistTask task = new ChecklistTask(event, title, category, dueDate, false, priority);
        checklistTaskRepository.save(task);
    }

    private void verifyEventOwnership(Event event, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));
        if (!event.getUser().getId().equals(user.getId()) && user.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("Unauthorized access to event checklist");
        }
    }

    public ChecklistTaskDTO toDTO(ChecklistTask task) {
        ChecklistTaskDTO dto = new ChecklistTaskDTO();
        dto.setId(task.getId());
        dto.setEventId(task.getEvent().getId());
        dto.setTitle(task.getTitle());
        dto.setCategory(task.getCategory());
        dto.setDueDate(task.getDueDate());
        dto.setCompleted(task.getCompleted());
        dto.setPriority(task.getPriority());
        dto.setCreatedAt(task.getCreatedAt());
        return dto;
    }
}
