package com.planora.repository;

import com.planora.model.ChecklistTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChecklistTaskRepository extends JpaRepository<ChecklistTask, Long> {
    List<ChecklistTask> findByEventIdOrderByDueDateAsc(Long eventId);
    void deleteByEventId(Long eventId);
    Long countByEventIdAndCompletedTrue(Long eventId);
    Long countByEventId(Long eventId);
}
