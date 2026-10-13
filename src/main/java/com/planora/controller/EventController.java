package com.planora.controller;

import com.planora.dto.EventDTO;
import com.planora.model.Event;
import com.planora.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Create, view, update and delete events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @Operation(summary = "Get all events for current user")
    public ResponseEntity<List<EventDTO>> getEvents(
            @RequestParam(required = false) Event.EventStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(eventService.getUserEvents(userDetails.getUsername(), status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single event by ID")
    public ResponseEntity<EventDTO> getEventById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(eventService.getEventById(id, userDetails.getUsername()));
    }

    @PostMapping
    @Operation(summary = "Create a new event")
    public ResponseEntity<EventDTO> createEvent(
            @Valid @RequestBody EventDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(eventService.createEvent(dto, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing event")
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(eventService.updateEvent(id, dto, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an event")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        eventService.deleteEvent(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
