package com.planora.loader;

import com.planora.model.*;
import com.planora.repository.*;
import com.planora.service.BudgetService;
import com.planora.service.ChecklistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final EventRepository eventRepository;
    private final BookingRequestRepository bookingRequestRepository;
    private final ReviewRepository reviewRepository;
    private final FavoriteRepository favoriteRepository;
    private final PasswordEncoder passwordEncoder;
    private final ChecklistService checklistService;
    private final BudgetService budgetService;

    public DataInitializer(UserRepository userRepository,
                           VendorRepository vendorRepository,
                           EventRepository eventRepository,
                           BookingRequestRepository bookingRequestRepository,
                           ReviewRepository reviewRepository,
                           FavoriteRepository favoriteRepository,
                           PasswordEncoder passwordEncoder,
                           ChecklistService checklistService,
                           BudgetService budgetService) {
        this.userRepository = userRepository;
        this.vendorRepository = vendorRepository;
        this.eventRepository = eventRepository;
        this.bookingRequestRepository = bookingRequestRepository;
        this.reviewRepository = reviewRepository;
        this.favoriteRepository = favoriteRepository;
        this.passwordEncoder = passwordEncoder;
        this.checklistService = checklistService;
        this.budgetService = budgetService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            logger.info("Database already seeded. Skipping initial data load.");
            return;
        }

        logger.info("Seeding initial Planora database with default users, rich vendors, and sample event...");

        // 1. Seed Users
        User admin = userRepository.save(new User(
                "System Admin",
                "admin@planora.com",
                passwordEncoder.encode("password123"),
                "+91 98765 43210",
                Role.ROLE_ADMIN
        ));

        User vendorUser = userRepository.save(new User(
                "Royal Palace Banquets",
                "vendor@planora.com",
                passwordEncoder.encode("password123"),
                "+91 91234 56789",
                Role.ROLE_VENDOR
        ));

        User clientUser = userRepository.save(new User(
                "Ashritha Reddy",
                "user@planora.com",
                passwordEncoder.encode("password123"),
                "+91 99887 76655",
                Role.ROLE_USER
        ));

        // 2. Seed Vendors
        Vendor v1 = createVendor("The Grand Palace & Royal Lawns", VendorCategory.VENUE,
                "Spacious 5-acre banquet resort featuring manicured lawns, grand ballroom, and luxurious bridal suites.",
                250000.0, 1200, "Bangalore", "Palace Grounds, Bellary Road, Bangalore",
                "+91 80 2345 6789", "events@grandpalace.com", 4.9, 42,
                "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=1200&q=80", true, vendorUser);

        Vendor v2 = createVendor("Taj Lakefront Heritage Resort", VendorCategory.VENUE,
                "Royal Rajasthani heritage palace surrounded by shimmering water and majestic courtyard pavilions.",
                450000.0, 800, "Jaipur", "Lake Pichola Outer Circle, Jaipur",
                "+91 141 2233 445", "weddings@tajheritage.com", 5.0, 68,
                "https://images.unsplash.com/photo-1544077960-604201fe74bc?auto=format&fit=crop&w=1200&q=80", true, vendorUser);

        Vendor v3 = createVendor("Bayview Seaside Banquet & Terrace", VendorCategory.VENUE,
                "Contemporary oceanfront banquet with an open sky terrace deck overlooking the Arabian Sea.",
                180000.0, 500, "Mumbai", "Juhu Beach Road, Mumbai",
                "+91 22 2654 9870", "info@bayviewbanquet.com", 4.7, 31,
                "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=1200&q=80", true, null);

        Vendor v4 = createVendor("Whispering Palms Luxury Villa", VendorCategory.VENUE,
                "Private boutique lawn and heritage Portuguese villa ideal for intimate beach destination weddings and parties.",
                120000.0, 250, "Goa", "Candolim Beach Road, North Goa",
                "+91 832 2456 123", "stay@whisperingpalms.com", 4.8, 25,
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=80", false, null);

        Vendor v5 = createVendor("Royal Feast Gourmet Caterers", VendorCategory.CATERING,
                "Award-winning fine-dining catering offering authentic Awadhi, South Indian, continental live counters & artisan desserts.",
                120000.0, 1000, "Bangalore", "Indiranagar 100ft Road, Bangalore",
                "+91 98450 11223", "orders@royalfeast.in", 4.9, 54,
                "https://images.unsplash.com/photo-1555244162-803834f70033?auto=format&fit=crop&w=1200&q=80", true, null);

        Vendor v6 = createVendor("Saffron & Spice Authentic Banquets", VendorCategory.CATERING,
                "Specialists in royal North Indian, Mughlai, and Chaat counters with premium culinary presentation.",
                150000.0, 800, "Mumbai", "Bandra West, Mumbai",
                "+91 98200 44332", "contact@saffronspice.com", 4.7, 39,
                "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=1200&q=80", false, null);

        Vendor v7 = createVendor("Delicacy Fusion Catering Co.", VendorCategory.CATERING,
                "Customized menu design for corporate lunches, birthdays, and cocktail receptions with interactive live stations.",
                95000.0, 600, "Delhi", "Connaught Place, New Delhi",
                "+91 11 4152 7890", "hello@delicacycatering.com", 4.6, 28,
                "https://images.unsplash.com/photo-1498837167922-ddd27525d352?auto=format&fit=crop&w=1200&q=80", false, null);

        Vendor v8 = createVendor("Bloom & Blossom Floral Sculptures", VendorCategory.DECORATION,
                "Exquisite exotic floral mandaps, fairy-light canopies, aesthetic photo backdrops and eco-friendly themes.",
                65000.0, null, "Bangalore", "Koramangala 4th Block, Bangalore",
                "+91 99001 23456", "decor@bloombllsom.com", 4.8, 37,
                "https://images.unsplash.com/photo-1527529482837-4698179dc6ce?auto=format&fit=crop&w=1200&q=80", true, null);

        Vendor v9 = createVendor("Ethereal Dream Weddings Decor", VendorCategory.DECORATION,
                "Grand royal entry gates, thematic stage styling, crystal chandeliers and royal velvet drapes.",
                110000.0, null, "Jaipur", "MI Road, Jaipur",
                "+91 94140 88776", "dream@etherealweddings.in", 4.9, 44,
                "https://images.unsplash.com/photo-1465495976277-4387d4b0b4c6?auto=format&fit=crop&w=1200&q=80", true, null);

        Vendor v10 = createVendor("Candid Moments Photography & Cinema", VendorCategory.PHOTOGRAPHY,
                "Cinematic wedding films, candid storytelling photography, 4K aerial drone coverage, and signature photo albums.",
                75000.0, null, "Bangalore", "HSR Layout Sector 2, Bangalore",
                "+91 98800 66778", "shoot@candidmoments.in", 4.9, 62,
                "https://images.unsplash.com/photo-1537633552985-df8429e8048b?auto=format&fit=crop&w=1200&q=80", true, null);

        Vendor v11 = createVendor("PixelArt Stories & Drone Shoots", VendorCategory.PHOTOGRAPHY,
                "Award-winning team covering pre-weddings, corporate celebrations, birthday highlights with fast 48-hour preview delivery.",
                60000.0, null, "Delhi", "South Extension, New Delhi",
                "+91 98110 55443", "team@pixelartstories.com", 4.8, 30,
                "https://images.unsplash.com/photo-1606800052052-a08af7148866?auto=format&fit=crop&w=1200&q=80", false, null);

        Vendor v12 = createVendor("Glow & Grace Bridal Artistry", VendorCategory.MAKEUP,
                "Celebrity bridal makeup artist, HD airbrush makeup, saree draping, and contemporary hair styling.",
                25000.0, null, "Bangalore", "Sadashivanagar, Bangalore",
                "+91 97400 33221", "artistry@glowandgrace.com", 4.9, 58,
                "https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?auto=format&fit=crop&w=1200&q=80", true, null);

        Vendor v13 = createVendor("Rhythm Beats Live Band & DJ Setup", VendorCategory.ENTERTAINMENT,
                "Complete sound, club lighting, cold pyro effects, and versatile DJ playing Bollywood, EDM, and Retro classics.",
                45000.0, null, "Bangalore", "MG Road, Bangalore",
                "+91 98440 99887", "booking@rhythmbeats.com", 4.8, 41,
                "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=1200&q=80", false, null);

        Vendor v14 = createVendor("Regal Vintage Cars & Luxury Convoy", VendorCategory.TRANSPORTATION,
                "Chauffeured classic 1930s vintage cars for couple entries plus premium Mercedes and BMW fleets for VIP guests.",
                35000.0, null, "Bangalore", "Richmond Town, Bangalore",
                "+91 99010 44556", "fleet@regalcars.in", 4.7, 19,
                "https://images.unsplash.com/photo-1511919884226-fd3cad34687c?auto=format&fit=crop&w=1200&q=80", false, null);

        // 3. Seed Sample Event for Client
        Event weddingEvent = eventRepository.save(new Event(
                "Rohan & Ananya's Grand Wedding & Reception",
                "Wedding",
                "A grand 2-day wedding celebration with sangeet evening and traditional reception dinner.",
                LocalDate.now().plusMonths(3),
                "Bangalore",
                450,
                1500000.0,
                Event.EventStatus.UPCOMING,
                clientUser
        ));

        // Seed checklist & budget for this event
        checklistService.seedDefaultChecklist(weddingEvent);
        budgetService.seedDefaultBudget(weddingEvent);

        // 4. Seed Reviews for Vendors
        reviewRepository.save(new Review(clientUser, v1, 5, "The Grand Palace exceeded all our expectations! The lawn was breathtaking and staff was extremely courteous."));
        reviewRepository.save(new Review(clientUser, v5, 5, "Unbelievable food quality! All our guests kept raving about the Biryani and live chaat counters."));
        reviewRepository.save(new Review(clientUser, v10, 5, "Photos came out like a Bollywood film! Highly professional photographers who made everyone feel relaxed."));

        // 5. Seed Favorites
        favoriteRepository.save(new Favorite(clientUser, v1));
        favoriteRepository.save(new Favorite(clientUser, v5));
        favoriteRepository.save(new Favorite(clientUser, v10));

        // 6. Seed Sample Booking Request
        bookingRequestRepository.save(new BookingRequest(
                clientUser,
                v1,
                weddingEvent,
                weddingEvent.getEventDate(),
                250000.0,
                "Requesting venue booking for wedding evening ceremony and reception banquet.",
                BookingRequest.BookingStatus.ACCEPTED
        ));

        bookingRequestRepository.save(new BookingRequest(
                clientUser,
                v5,
                weddingEvent,
                weddingEvent.getEventDate(),
                120000.0,
                "Interested in 4-course banquet menu for 450 guests. Please confirm tasting schedule.",
                BookingRequest.BookingStatus.PENDING
        ));

        logger.info("Successfully initialized Planora test data!");
    }

    private Vendor createVendor(String name, VendorCategory category, String description,
                                Double price, Integer capacity, String city, String address,
                                String phone, String email, Double rating, Integer reviews,
                                String image, Boolean featured, User owner) {
        Vendor v = new Vendor(name, category, description, price, capacity, city, address, phone, email, rating, reviews, image, featured);
        v.setOwner(owner);
        return vendorRepository.save(v);
    }
}
