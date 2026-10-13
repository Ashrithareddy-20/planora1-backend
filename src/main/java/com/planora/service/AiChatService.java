package com.planora.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planora.dto.ChatRequestDTO;
import com.planora.dto.ChatResponseDTO;
import com.planora.model.BudgetItem;
import com.planora.model.ChecklistTask;
import com.planora.model.Event;
import com.planora.model.Vendor;
import com.planora.repository.BudgetItemRepository;
import com.planora.repository.ChecklistTaskRepository;
import com.planora.repository.EventRepository;
import com.planora.repository.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiChatService {

    private static final Logger logger = LoggerFactory.getLogger(AiChatService.class);

    private final EventRepository eventRepository;
    private final VendorRepository vendorRepository;
    private final BudgetItemRepository budgetItemRepository;
    private final ChecklistTaskRepository checklistTaskRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${planora.ai.api-key:}")
    private String apiKey;

    @Value("${planora.ai.api-url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${planora.ai.model:gpt-3.5-turbo}")
    private String model;

    public AiChatService(EventRepository eventRepository,
                         VendorRepository vendorRepository,
                         BudgetItemRepository budgetItemRepository,
                         ChecklistTaskRepository checklistTaskRepository,
                         WebClient.Builder webClientBuilder) {
        this.eventRepository = eventRepository;
        this.vendorRepository = vendorRepository;
        this.budgetItemRepository = budgetItemRepository;
        this.checklistTaskRepository = checklistTaskRepository;
        this.webClient = webClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    public ChatResponseDTO processChat(ChatRequestDTO request, String currentUserEmail) {
        String userQuery = request.getMessage() != null ? request.getMessage().trim() : "";
        Long eventId = request.getEventId();

        // 1. RAG Retrieval Step: Fetch relevant event, budget, tasks, and vendors
        Event currentEvent = null;
        List<BudgetItem> budgetItems = Collections.emptyList();
        List<ChecklistTask> tasks = Collections.emptyList();

        if (eventId != null) {
            currentEvent = eventRepository.findById(eventId).orElse(null);
            if (currentEvent != null) {
                budgetItems = budgetItemRepository.findByEventId(eventId);
                tasks = checklistTaskRepository.findByEventIdOrderByDueDateAsc(eventId);
            }
        }

        List<Vendor> matchedVendors = retrieveRelevantVendors(userQuery, currentEvent);

        String contextSummary = buildRetrievedContext(currentEvent, budgetItems, tasks, matchedVendors);

        // 2. If API Key is provided, attempt external LLM call; otherwise fallback to RAG generation engine
        if (apiKey != null && !apiKey.isBlank() && !apiKey.equalsIgnoreCase("YOUR_API_KEY_HERE")) {
            try {
                String aiReply = callExternalLlm(userQuery, contextSummary, request.getHistory());
                if (aiReply != null && !aiReply.isBlank()) {
                    return new ChatResponseDTO(
                            aiReply,
                            "Retrieved live event & database vendor records",
                            generateSuggestions(userQuery, currentEvent),
                            true
                    );
                }
            } catch (Exception ex) {
                logger.warn("External LLM call failed, switching to local RAG generator: {}", ex.getMessage());
            }
        }

        // 3. Built-in Local RAG Response Generator
        return generateRAGResponse(userQuery, currentEvent, budgetItems, tasks, matchedVendors, contextSummary);
    }

    private List<Vendor> retrieveRelevantVendors(String query, Event event) {
        String q = query.toLowerCase();
        List<Vendor> all = vendorRepository.findAll();

        return all.stream()
                .filter(v -> {
                    boolean textMatch = q.contains(v.getName().toLowerCase())
                            || q.contains(v.getCategory().name().toLowerCase())
                            || q.contains(v.getCity().toLowerCase())
                            || v.getDescription().toLowerCase().contains(q);
                    boolean eventMatch = (event != null && event.getLocation() != null &&
                            (v.getCity().equalsIgnoreCase(event.getLocation()) || event.getLocation().toLowerCase().contains(v.getCity().toLowerCase())));
                    return textMatch || eventMatch;
                })
                .limit(6)
                .collect(Collectors.toList());
    }

    private String buildRetrievedContext(Event event, List<BudgetItem> budgetItems, List<ChecklistTask> tasks, List<Vendor> vendors) {
        StringBuilder sb = new StringBuilder();
        if (event != null) {
            sb.append(String.format("EVENT CONTEXT: '%s' (%s) on %s in %s for %d guests with total budget ₹%,.0f.\n",
                    event.getTitle(), event.getEventType(), event.getEventDate(), event.getLocation(), event.getGuestCount(), event.getTotalBudget()));
            if (!budgetItems.isEmpty()) {
                double allocated = budgetItems.stream().mapToDouble(b -> b.getAllocatedAmount() != null ? b.getAllocatedAmount() : 0.0).sum();
                sb.append(String.format("BUDGET: Total Allocated: ₹%,.0f, Remaining: ₹%,.0f across %d categories.\n",
                        allocated, Math.max(0, event.getTotalBudget() - allocated), budgetItems.size()));
            }
            if (!tasks.isEmpty()) {
                long completed = tasks.stream().filter(t -> Boolean.TRUE.equals(t.getCompleted())).count();
                sb.append(String.format("CHECKLIST: %d of %d tasks completed.\n", completed, tasks.size()));
            }
        }
        if (!vendors.isEmpty()) {
            sb.append("MATCHED DATABASE VENDORS:\n");
            for (Vendor v : vendors) {
                sb.append(String.format("- %s (%s, %s): Starting ₹%,.0f, Rating: %.1f★ (Capacity: %s guests)\n",
                        v.getName(), v.getCategory(), v.getCity(), v.getPriceStarting(), v.getRating(),
                        v.getMaxGuestCapacity() != null ? v.getMaxGuestCapacity() : "N/A"));
            }
        }
        return sb.toString();
    }

    private String callExternalLlm(String query, String context, List<ChatRequestDTO.HistoryItem> history) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> sysMessage = new HashMap<>();
        sysMessage.put("role", "system");
        sysMessage.put("content", "You are Planora AI, an intelligent event planning assistant. " +
                "Assist the user with budgeting, vendor comparison, checklists, and event coordination based on the following real data:\n" + context);
        messages.add(sysMessage);

        if (history != null) {
            for (ChatRequestDTO.HistoryItem h : history) {
                Map<String, String> m = new HashMap<>();
                m.put("role", h.getRole());
                m.put("content", h.getContent());
                messages.add(m);
            }
        }

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", query);
        messages.add(userMsg);

        body.put("messages", messages);
        body.put("temperature", 0.7);

        String jsonResp = webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(12))
                .block();

        try {
            JsonNode root = objectMapper.readTree(jsonResp);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            logger.error("Failed to parse external LLM response: {}", e.getMessage());
            return null;
        }
    }

    private ChatResponseDTO generateRAGResponse(String query, Event event, List<BudgetItem> budgetItems,
                                                List<ChecklistTask> tasks, List<Vendor> vendors, String contextSummary) {
        String q = query.toLowerCase();
        StringBuilder reply = new StringBuilder();

        if (q.contains("budget") || q.contains("cost") || q.contains("price") || q.contains("estimate")) {
            reply.append("### 💰 Budget & Cost Analysis\n\n");
            if (event != null) {
                double total = event.getTotalBudget();
                double allocated = budgetItems.stream().mapToDouble(b -> b.getAllocatedAmount() != null ? b.getAllocatedAmount() : 0.0).sum();
                double remaining = Math.max(0.0, total - allocated);

                reply.append(String.format("For your event **%s**, your total budget is **₹%,.0f**.\n", event.getTitle(), total));
                reply.append(String.format("- **Allocated:** ₹%,.0f (%.1f%%)\n", allocated, (allocated / total) * 100));
                reply.append(String.format("- **Available Remaining:** ₹%,.0f\n\n", remaining));

                reply.append("#### Recommended Allocation Breakdown:\n");
                reply.append(String.format("- **Venue & Logistics (35-40%%):** ~₹%,.0f\n", total * 0.38));
                reply.append(String.format("- **Catering & Beverages (25-30%%):** ~₹%,.0f\n", total * 0.28));
                reply.append(String.format("- **Decor & Floral (15%%):** ~₹%,.0f\n", total * 0.15));
                reply.append(String.format("- **Photography & Video (10%%):** ~₹%,.0f\n", total * 0.10));
                reply.append(String.format("- **Entertainment & Music (7%%):** ~₹%,.0f\n\n", total * 0.07));

                if (allocated > total) {
                    reply.append("> ⚠️ **Warning:** Your current allocated expenses exceed your total budget! Consider reducing decoration or negotiating catering rates.\n\n");
                }
            } else {
                reply.append("As a general rule of thumb for successful event budgeting:\n\n");
                reply.append("1. **Venue & Food:** Reserve 55-65% of your total pool for venue and catering.\n");
                reply.append("2. **Aesthetics & Memory:** 25% for theme decor, stage lighting, and professional photography.\n");
                reply.append("3. **Contingency Fund:** Always keep 5-10% unallocated for last-minute guest additions.\n\n");
            }
        } else if (q.contains("vendor") || q.contains("recommend") || q.contains("venue") || q.contains("catering") || q.contains("photographer") || q.contains("decor")) {
            reply.append("### 🌟 Curated Vendor Recommendations\n\n");
            if (!vendors.isEmpty()) {
                reply.append("Here are top-rated providers retrieved directly from Planora's database that match your criteria:\n\n");
                for (Vendor v : vendors) {
                    reply.append(String.format("#### 📍 **%s** (%s)\n", v.getName(), v.getCategory()));
                    reply.append(String.format("- **Location:** %s | **Rating:** %.1f★ (%d reviews)\n", v.getCity(), v.getRating(), v.getReviewCount()));
                    reply.append(String.format("- **Starting Price:** ₹%,.0f\n", v.getPriceStarting()));
                    if (v.getMaxGuestCapacity() != null && v.getMaxGuestCapacity() > 0) {
                        reply.append(String.format("- **Guest Capacity:** Up to %d guests\n", v.getMaxGuestCapacity()));
                    }
                    reply.append(String.format("- *\"%s\"*\n\n", v.getDescription()));
                }
                reply.append("👉 You can compare these vendors side-by-side or send a booking inquiry from the **Vendors** section!\n\n");
            } else {
                reply.append("I looked up our database for suitable matches. You can browse all service categories including **Venues, Catering, Photography, and Decor** in the Vendors explorer tab or filter by your city.\n\n");
            }
        } else if (q.contains("checklist") || q.contains("todo") || q.contains("task") || q.contains("timeline") || q.contains("schedule")) {
            reply.append("### 📋 Event Planning Checklist & Timeline\n\n");
            if (event != null && !tasks.isEmpty()) {
                long pending = tasks.stream().filter(t -> !Boolean.TRUE.equals(t.getCompleted())).count();
                reply.append(String.format("You have **%d pending tasks** for **%s**:\n\n", pending, event.getTitle()));
                tasks.stream().filter(t -> !Boolean.TRUE.equals(t.getCompleted())).limit(6).forEach(t -> {
                    reply.append(String.format("- [ ] **%s** *(Due: %s)* - Priority: %s\n", t.getTitle(), t.getDueDate(), t.getPriority()));
                });
                reply.append("\n💡 *Pro-tip: Focus on high-priority items first (Venue & Invitations).* \n\n");
            } else {
                reply.append("Here is the golden milestone roadmap:\n\n");
                reply.append("1. **4-6 Weeks Out:** Lock in Venue, Catering, and theme decoration.\n");
                reply.append("2. **2-3 Weeks Out:** Dispatch RSVP invitations and confirm guest headcounts.\n");
                reply.append("3. **1 Week Out:** Reconfirm schedule with photographer, DJ, and makeup artists.\n");
                reply.append("4. **Day Before:** Double check emergency contacts and equipment setups.\n\n");
            }
        } else {
            reply.append("### 👋 Hello! I'm Planora AI, your personal event concierge.\n\n");
            if (event != null) {
                reply.append(String.format("I'm currently assisting you with **%s** on **%s** in **%s** for **%d guests**.\n\n",
                        event.getTitle(), event.getEventDate(), event.getLocation(), event.getGuestCount()));
            }
            reply.append("I can help you with:\n");
            reply.append("- 🎯 **Finding & Comparing Vendors:** Match venues, caterers, and decorators within budget.\n");
            reply.append("- 📊 **Budget Estimation:** Optimize cost breakdowns and avoid overspending.\n");
            reply.append("- 📝 **Checklist Management:** Step-by-step milestones to stay on schedule.\n");
            reply.append("- 💡 **Theme & Menu Ideas:** Personalized suggestions for weddings, birthdays, corporate meets, and parties.\n\n");
            reply.append("What would you like to explore next?");
        }

        return new ChatResponseDTO(
                reply.toString(),
                contextSummary,
                generateSuggestions(query, event),
                false
        );
    }

    private List<String> generateSuggestions(String query, Event event) {
        String q = query.toLowerCase();
        if (q.contains("budget")) {
            return List.of("How can I save on catering costs?", "Recommend venues within my budget", "Show checklist for this event");
        } else if (q.contains("vendor") || q.contains("venue")) {
            return List.of("Compare top catering services", "How to allocate budget for photography?", "Show my event tasks");
        } else {
            return List.of("Give me a budget breakdown", "Recommend vendors in my city", "What tasks should I do next?");
        }
    }
}
