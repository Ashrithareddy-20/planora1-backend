package com.planora;

import com.planora.dto.AuthRequest;
import com.planora.dto.AuthResponse;
import com.planora.dto.EventDTO;
import com.planora.model.Event;
import com.planora.model.Role;
import com.planora.model.User;
import com.planora.repository.EventRepository;
import com.planora.repository.UserRepository;
import com.planora.service.AuthService;
import com.planora.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional
class PlanoraApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private EventService eventService;

    @Test
    void contextLoads() {
        assertNotNull(userRepository);
        assertNotNull(eventRepository);
    }

    @Test
    void testUserAuthentication() {
        AuthRequest request = new AuthRequest("user@planora.com", "password123");
        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("user@planora.com", response.getEmail());
        assertEquals(Role.ROLE_USER, response.getRole());
    }

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Test
    void testBCryptPassword() {
        String hash = passwordEncoder.encode("password123");
        System.out.println("GENERATED_BCRYPT_HASH=" + hash);
        assertTrue(passwordEncoder.matches("password123", hash));
    }

    @Test
    void testCreateAndRetrieveEvent() {
        EventDTO dto = new EventDTO();
        dto.setTitle("Annual Tech Summit 2026");
        dto.setEventType("Corporate");
        dto.setDescription("Corporate technical conference");
        dto.setEventDate(LocalDate.now().plusMonths(2));
        dto.setLocation("Bangalore");
        dto.setGuestCount(200);
        dto.setTotalBudget(500000.0);
        dto.setStatus(Event.EventStatus.UPCOMING);

        EventDTO created = eventService.createEvent(dto, "user@planora.com");
        assertNotNull(created.getId());
        assertEquals("Annual Tech Summit 2026", created.getTitle());

        List<EventDTO> userEvents = eventService.getUserEvents("user@planora.com", null);
        assertFalse(userEvents.isEmpty());
        assertTrue(userEvents.stream().anyMatch(e -> e.getTitle().equals("Annual Tech Summit 2026")));
    }
}
