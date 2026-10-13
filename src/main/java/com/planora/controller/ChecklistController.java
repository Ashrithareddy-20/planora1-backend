package com.planora.controller;

import com.planora.dto.ChecklistTaskDTO;
import com.planora.service.ChecklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklists")
@Tag(name = "Checklists", description = "Event checklist generation, milestones and task tracking")
public class ChecklistController {

    private final ChecklistService checklistService;

    public ChecklistController(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get all checklist tasks for an event")
    public ResponseEntity<List<ChecklistTaskDTO>> getEventTasks(@PathVariable Long eventId) {
        return ResponseEntity.ok(checklistService.getEventTasks(eventId));
    }

    @PostMapping
    @Operation(summary = "Add a task to event checklist")
    public ResponseEntity<ChecklistTaskDTO> createTask(
            @Valid @RequestBody ChecklistTaskDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(checklistService.createTask(dto, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task details")
    public ResponseEntity<ChecklistTaskDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody ChecklistTaskDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(checklistService.updateTask(id, dto, userDetails.getUsername()));
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Toggle task completed status")
    public ResponseEntity<ChecklistTaskDTO> toggleTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(checklistService.toggleTask(id, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task from checklist")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        checklistService.deleteTask(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
