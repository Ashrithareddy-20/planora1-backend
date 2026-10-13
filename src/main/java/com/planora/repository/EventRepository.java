package com.planora.repository;

import com.planora.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUserIdOrderByEventDateAsc(Long userId);
    List<Event> findByUserIdAndStatusOrderByEventDateAsc(Long userId, Event.EventStatus status);
    Long countByUserId(Long userId);
}
