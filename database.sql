-- =============================================================================
-- Planora – AI-Powered Event Planning & Vendor Recommendation System
-- Complete MySQL Database Schema and Seed Data Initialization Script
-- Database: planora_db
-- =============================================================================

CREATE DATABASE IF NOT EXISTS planora_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE planora_db;

-- Disable foreign key checks for clean recreation
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS booking_requests;
DROP TABLE IF EXISTS checklist_tasks;
DROP TABLE IF EXISTS budget_items;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS vendors;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- -----------------------------------------------------------------------------
-- 1. USERS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 2. VENDORS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE vendors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    price_starting DOUBLE,
    max_guest_capacity INT,
    city VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    contact_phone VARCHAR(50),
    contact_email VARCHAR(100),
    rating DOUBLE DEFAULT 4.5,
    review_count INT DEFAULT 0,
    image_url VARCHAR(1000),
    featured BOOLEAN DEFAULT FALSE,
    owner_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_vendor_category (category),
    INDEX idx_vendor_city (city),
    INDEX idx_vendor_rating (rating),
    INDEX idx_vendor_featured (featured),
    CONSTRAINT fk_vendor_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 3. EVENTS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    description TEXT,
    event_date DATE NOT NULL,
    location VARCHAR(255) NOT NULL,
    guest_count INT NOT NULL,
    total_budget DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'UPCOMING',
    user_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_event_user (user_id),
    INDEX idx_event_date (event_date),
    INDEX idx_event_status (status),
    CONSTRAINT fk_event_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 4. BUDGET ITEMS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE budget_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    allocated_amount DOUBLE NOT NULL DEFAULT 0.0,
    actual_amount DOUBLE DEFAULT 0.0,
    notes VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_budget_event (event_id),
    INDEX idx_budget_category (category),
    CONSTRAINT fk_budget_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 5. CHECKLIST TASKS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE checklist_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    due_date DATE,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_checklist_event (event_id),
    INDEX idx_checklist_completed (completed),
    INDEX idx_checklist_due_date (due_date),
    CONSTRAINT fk_checklist_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 6. BOOKING REQUESTS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE booking_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vendor_id BIGINT NOT NULL,
    event_id BIGINT,
    requested_date DATE NOT NULL,
    estimated_price DOUBLE,
    message TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    vendor_response TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_booking_user (user_id),
    INDEX idx_booking_vendor (vendor_id),
    INDEX idx_booking_event (event_id),
    INDEX idx_booking_status (status),
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_vendor FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE,
    CONSTRAINT fk_booking_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 7. REVIEWS TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vendor_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_review_vendor (vendor_id),
    INDEX idx_review_user (user_id),
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_vendor FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 8. FAVORITES TABLE
-- -----------------------------------------------------------------------------
CREATE TABLE favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vendor_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_vendor (user_id, vendor_id),
    INDEX idx_favorite_user (user_id),
    INDEX idx_favorite_vendor (vendor_id),
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_favorite_vendor FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- SEED DATA INSERTION
-- Password for all pre-seeded accounts is 'password123'
-- BCrypt Hash: $2a$10$/VvqNic9.xOUem1cAEP/3uYh/Q8x7Z6Pr3S5cm7ASofgq3SYU5dea
-- =============================================================================

-- 1. Insert Users
INSERT INTO users (id, name, email, password, phone, role, created_at) VALUES
(1, 'System Administrator', 'admin@planora.com', '$2a$10$/VvqNic9.xOUem1cAEP/3uYh/Q8x7Z6Pr3S5cm7ASofgq3SYU5dea', '+91 98765 43210', 'ROLE_ADMIN', NOW()),
(2, 'Royal Palace Banquets', 'vendor@planora.com', '$2a$10$/VvqNic9.xOUem1cAEP/3uYh/Q8x7Z6Pr3S5cm7ASofgq3SYU5dea', '+91 91234 56789', 'ROLE_VENDOR', NOW()),
(3, 'Ashritha Reddy', 'user@planora.com', '$2a$10$/VvqNic9.xOUem1cAEP/3uYh/Q8x7Z6Pr3S5cm7ASofgq3SYU5dea', '+91 99887 76655', 'ROLE_USER', NOW()),
(4, 'Vikram Sharma', 'vikram@planora.com', '$2a$10$/VvqNic9.xOUem1cAEP/3uYh/Q8x7Z6Pr3S5cm7ASofgq3SYU5dea', '+91 98111 22334', 'ROLE_USER', NOW()),
(5, 'Artisan Caterers Co.', 'vendor2@planora.com', '$2a$10$/VvqNic9.xOUem1cAEP/3uYh/Q8x7Z6Pr3S5cm7ASofgq3SYU5dea', '+91 98450 33445', 'ROLE_VENDOR', NOW());

-- 2. Insert Vendors
INSERT INTO vendors (id, name, category, description, price_starting, max_guest_capacity, city, address, contact_phone, contact_email, rating, review_count, image_url, featured, owner_id, created_at) VALUES
(1, 'The Grand Palace & Royal Lawns', 'VENUE',
 'Spacious 5-acre banquet resort featuring manicured lawns, grand ballroom, and luxurious bridal suites.',
 250000.0, 1200, 'Bangalore', 'Palace Grounds, Bellary Road, Bangalore',
 '+91 80 2345 6789', 'events@grandpalace.com', 4.9, 42,
 'https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=1200&q=80', TRUE, 2, NOW()),

(2, 'Taj Lakefront Heritage Resort', 'VENUE',
 'Royal Rajasthani heritage palace surrounded by shimmering water and majestic courtyard pavilions.',
 450000.0, 800, 'Jaipur', 'Lake Pichola Outer Circle, Jaipur',
 '+91 141 2233 445', 'weddings@tajheritage.com', 5.0, 68,
 'https://images.unsplash.com/photo-1544077960-604201fe74bc?auto=format&fit=crop&w=1200&q=80', TRUE, 2, NOW()),

(3, 'Bayview Seaside Banquet & Terrace', 'VENUE',
 'Contemporary oceanfront banquet with an open sky terrace deck overlooking the Arabian Sea.',
 180000.0, 500, 'Mumbai', 'Juhu Beach Road, Mumbai',
 '+91 22 2654 9870', 'info@bayviewbanquet.com', 4.7, 31,
 'https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=1200&q=80', TRUE, NULL, NOW()),

(4, 'Whispering Palms Luxury Villa', 'VENUE',
 'Private boutique lawn and heritage Portuguese villa ideal for intimate beach destination weddings and parties.',
 120000.0, 250, 'Goa', 'Candolim Beach Road, North Goa',
 '+91 832 2456 123', 'stay@whisperingpalms.com', 4.8, 25,
 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=80', FALSE, NULL, NOW()),

(5, 'Royal Feast Gourmet Caterers', 'CATERING',
 'Award-winning fine-dining catering offering authentic Awadhi, South Indian, continental live counters & artisan desserts.',
 120000.0, 1000, 'Bangalore', 'Indiranagar 100ft Road, Bangalore',
 '+91 98450 11223', 'orders@royalfeast.in', 4.9, 54,
 'https://images.unsplash.com/photo-1555244162-803834f70033?auto=format&fit=crop&w=1200&q=80', TRUE, 5, NOW()),

(6, 'Saffron & Spice Authentic Banquets', 'CATERING',
 'Specialists in royal North Indian, Mughlai, and Chaat counters with premium culinary presentation.',
 150000.0, 800, 'Mumbai', 'Bandra West, Mumbai',
 '+91 98200 44332', 'contact@saffronspice.com', 4.7, 39,
 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=1200&q=80', FALSE, NULL, NOW()),

(7, 'Delicacy Fusion Catering Co.', 'CATERING',
 'Customized menu design for corporate lunches, birthdays, and cocktail receptions with interactive live stations.',
 95000.0, 600, 'Delhi', 'Connaught Place, New Delhi',
 '+91 11 4152 7890', 'hello@delicacycatering.com', 4.6, 28,
 'https://images.unsplash.com/photo-1498837167922-ddd27525d352?auto=format&fit=crop&w=1200&q=80', FALSE, NULL, NOW()),

(8, 'Bloom & Blossom Floral Sculptures', 'DECORATION',
 'Exquisite exotic floral mandaps, fairy-light canopies, aesthetic photo backdrops and eco-friendly themes.',
 65000.0, NULL, 'Bangalore', 'Koramangala 4th Block, Bangalore',
 '+91 99001 23456', 'decor@bloombllsom.com', 4.8, 37,
 'https://images.unsplash.com/photo-1527529482837-4698179dc6ce?auto=format&fit=crop&w=1200&q=80', TRUE, NULL, NOW()),

(9, 'Ethereal Dream Weddings Decor', 'DECORATION',
 'Grand royal entry gates, thematic stage styling, crystal chandeliers and royal velvet drapes.',
 110000.0, NULL, 'Jaipur', 'MI Road, Jaipur',
 '+91 94140 88776', 'dream@etherealweddings.in', 4.9, 44,
 'https://images.unsplash.com/photo-1465495976277-4387d4b0b4c6?auto=format&fit=crop&w=1200&q=80', TRUE, NULL, NOW()),

(10, 'Candid Moments Photography & Cinema', 'PHOTOGRAPHY',
 'Cinematic wedding films, candid storytelling photography, 4K aerial drone coverage, and signature photo albums.',
 75000.0, NULL, 'Bangalore', 'HSR Layout Sector 2, Bangalore',
 '+91 98800 66778', 'shoot@candidmoments.in', 4.9, 62,
 'https://images.unsplash.com/photo-1537633552985-df8429e8048b?auto=format&fit=crop&w=1200&q=80', TRUE, NULL, NOW()),

(11, 'PixelArt Stories & Drone Shoots', 'PHOTOGRAPHY',
 'Award-winning team covering pre-weddings, corporate celebrations, birthday highlights with fast 48-hour preview delivery.',
 60000.0, NULL, 'Delhi', 'South Extension, New Delhi',
 '+91 98110 55443', 'team@pixelartstories.com', 4.8, 30,
 'https://images.unsplash.com/photo-1606800052052-a08af7148866?auto=format&fit=crop&w=1200&q=80', FALSE, NULL, NOW()),

(12, 'Glow & Grace Bridal Artistry', 'MAKEUP',
 'Celebrity bridal makeup artist, HD airbrush makeup, saree draping, and contemporary hair styling.',
 25000.0, NULL, 'Bangalore', 'Sadashivanagar, Bangalore',
 '+91 97400 33221', 'artistry@glowandgrace.com', 4.9, 58,
 'https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?auto=format&fit=crop&w=1200&q=80', TRUE, NULL, NOW()),

(13, 'Rhythm Beats Live Band & DJ Setup', 'ENTERTAINMENT',
 'Complete sound, club lighting, cold pyro effects, and versatile DJ playing Bollywood, EDM, and Retro classics.',
 45000.0, NULL, 'Bangalore', 'MG Road, Bangalore',
 '+91 98440 99887', 'booking@rhythmbeats.com', 4.8, 41,
 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=1200&q=80', FALSE, NULL, NOW()),

(14, 'Regal Vintage Cars & Luxury Convoy', 'TRANSPORTATION',
 'Chauffeured classic 1930s vintage cars for couple entries plus premium Mercedes and BMW fleets for VIP guests.',
 35000.0, NULL, 'Bangalore', 'Richmond Town, Bangalore',
 '+91 99010 44556', 'fleet@regalcars.in', 4.7, 19,
 'https://images.unsplash.com/photo-1511919884226-fd3cad34687c?auto=format&fit=crop&w=1200&q=80', FALSE, NULL, NOW());

-- 3. Insert Events
INSERT INTO events (id, title, event_type, description, event_date, location, guest_count, total_budget, status, user_id, created_at) VALUES
(1, 'Rohan & Ananya\'s Grand Wedding & Reception', 'Wedding',
 'A grand 2-day wedding celebration with sangeet evening and traditional reception dinner.',
 DATE_ADD(CURRENT_DATE, INTERVAL 90 DAY), 'Bangalore', 450, 1500000.0, 'UPCOMING', 3, NOW()),

(2, 'Priya\'s 30th Milestone Birthday Gala', 'Birthday',
 'Rooftop cocktail party with live DJ, customized dessert buffet, and photo booth memories.',
 DATE_ADD(CURRENT_DATE, INTERVAL 45 DAY), 'Bangalore', 80, 150000.0, 'UPCOMING', 3, NOW()),

(3, 'Pan-India Tech Founders Summit 2026', 'Corporate',
 'Annual gathering of 300 tech leaders, keynote presentations, networking dinners, and exhibition booths.',
 DATE_ADD(CURRENT_DATE, INTERVAL 120 DAY), 'Bangalore', 300, 800000.0, 'UPCOMING', 4, NOW());

-- 4. Insert Budget Items
INSERT INTO budget_items (id, event_id, category, title, allocated_amount, actual_amount, notes, created_at) VALUES
-- Event 1 (Wedding)
(1, 1, 'VENUE', 'Venue Rental & Setup', 525000.0, 250000.0, 'Booked The Grand Palace & Royal Lawns', NOW()),
(2, 1, 'CATERING', 'Catering & Beverage Service', 375000.0, 120000.0, 'Booked Royal Feast Gourmet Caterers', NOW()),
(3, 1, 'DECORATION', 'Floral & Theme Decoration', 225000.0, 0.0, 'Shortlisted Bloom & Blossom', NOW()),
(4, 1, 'PHOTOGRAPHY', 'Photography & Cinematography', 150000.0, 75000.0, 'Candid Moments booked', NOW()),
(5, 1, 'ENTERTAINMENT', 'DJ, Sound & Lights', 120000.0, 45000.0, 'Rhythm Beats DJ setup', NOW()),
(6, 1, 'MAKEUP', 'Styling & Makeup', 60000.0, 25000.0, 'Glow & Grace Bridal Artistry', NOW()),
(7, 1, 'TRANSPORTATION', 'Guest Transportation / Logistics', 45000.0, 0.0, 'Vintage car and mini-bus shuttles', NOW()),

-- Event 2 (Birthday)
(8, 2, 'VENUE', 'Rooftop Venue Reservation', 50000.0, 45000.0, 'Advance paid for rooftop terrace', NOW()),
(9, 2, 'CATERING', 'Cocktail Menu & Custom Cake', 45000.0, 40000.0, 'Fusion tapas and 3-tier birthday cake', NOW()),
(10, 2, 'ENTERTAINMENT', 'DJ & Club Sound Setup', 30000.0, 25000.0, 'Sound check confirmed', NOW()),
(11, 2, 'DECORATION', 'Balloon Arch & Neon Signage', 25000.0, 20000.0, 'Custom neon hashtag sign ordered', NOW());

-- 5. Insert Checklist Tasks
INSERT INTO checklist_tasks (id, event_id, title, category, due_date, completed, priority, created_at) VALUES
-- Event 1 Tasks
(1, 1, 'Finalize and book wedding venue & lawns', 'Venue', DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY), TRUE, 'HIGH', NOW()),
(2, 1, 'Select catering menu and schedule food tasting', 'Catering', DATE_ADD(CURRENT_DATE, INTERVAL 25 DAY), TRUE, 'HIGH', NOW()),
(3, 1, 'Confirm theme, stage decor, and floral arrangements', 'Decoration', DATE_ADD(CURRENT_DATE, INTERVAL 40 DAY), FALSE, 'MEDIUM', NOW()),
(4, 1, 'Hire wedding photographer and videographer', 'Photography', DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY), TRUE, 'HIGH', NOW()),
(5, 1, 'Send digital & physical invitations to guests', 'Invitations', DATE_ADD(CURRENT_DATE, INTERVAL 60 DAY), FALSE, 'MEDIUM', NOW()),
(6, 1, 'Finalize makeup artist and hair stylist appointments', 'Makeup', DATE_ADD(CURRENT_DATE, INTERVAL 65 DAY), TRUE, 'MEDIUM', NOW()),
(7, 1, 'Book DJ, sound system, and sound check slot', 'Entertainment', DATE_ADD(CURRENT_DATE, INTERVAL 70 DAY), FALSE, 'MEDIUM', NOW()),
(8, 1, 'Arrange logistics and guest transportation', 'Transport', DATE_ADD(CURRENT_DATE, INTERVAL 80 DAY), FALSE, 'LOW', NOW()),
(9, 1, 'Conduct final rehearsal and review vendor timeline', 'Coordination', DATE_ADD(CURRENT_DATE, INTERVAL 88 DAY), FALSE, 'HIGH', NOW()),

-- Event 2 Tasks
(10, 2, 'Book rooftop venue and pay deposit', 'Venue', DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY), TRUE, 'HIGH', NOW()),
(11, 2, 'Design & order customized 3-tier birthday cake', 'Catering', DATE_ADD(CURRENT_DATE, INTERVAL 20 DAY), TRUE, 'HIGH', NOW()),
(12, 2, 'Finalize party playlist with DJ', 'Entertainment', DATE_ADD(CURRENT_DATE, INTERVAL 35 DAY), FALSE, 'MEDIUM', NOW()),
(13, 2, 'Send RSVP reminders to attendees', 'Invitations', DATE_ADD(CURRENT_DATE, INTERVAL 38 DAY), FALSE, 'HIGH', NOW());

-- 6. Insert Booking Requests
INSERT INTO booking_requests (id, user_id, vendor_id, event_id, requested_date, estimated_price, message, status, vendor_response, created_at) VALUES
(1, 3, 1, 1, DATE_ADD(CURRENT_DATE, INTERVAL 90 DAY), 250000.0,
 'Requesting venue booking for wedding evening ceremony and reception banquet.',
 'ACCEPTED',
 'Congratulations! Your booking for The Grand Palace is confirmed for the requested date. Our banquet manager will reach out for the layout setup.',
 NOW()),

(2, 3, 5, 1, DATE_ADD(CURRENT_DATE, INTERVAL 90 DAY), 120000.0,
 'Interested in 4-course banquet menu for 450 guests. Please confirm tasting schedule.',
 'ACCEPTED',
 'We would be honored to cater your special day. Food tasting has been scheduled for this coming weekend.',
 NOW()),

(3, 3, 10, 1, DATE_ADD(CURRENT_DATE, INTERVAL 90 DAY), 75000.0,
 'Looking for candid photography + drone coverage for morning ceremony and evening reception.',
 'PENDING',
 NULL,
 NOW()),

(4, 3, 8, 1, DATE_ADD(CURRENT_DATE, INTERVAL 90 DAY), 65000.0,
 'Need pastel floral theme mandap and fairy light entrance canopy.',
 'PENDING',
 NULL,
 NOW());

-- 7. Insert Reviews
INSERT INTO reviews (id, user_id, vendor_id, rating, comment, created_at) VALUES
(1, 3, 1, 5, 'The Grand Palace exceeded all our expectations! The lawn was breathtaking and the banquet manager ensured flawless guest coordination.', NOW()),
(2, 3, 5, 5, 'Unbelievable food quality! All our guests kept raving about the Biryani, live chaat counters, and pistachio kulfi. Worth every penny!', NOW()),
(3, 3, 10, 5, 'Photos came out like a cinematic movie! The team was punctual, creative, and captured the purest emotions without intrusive staging.', NOW()),
(4, 4, 2, 5, 'An absolute dream heritage property. If you want a royal fairy-tale wedding, Taj Lakefront Heritage is unmatched.', NOW()),
(5, 4, 12, 5, 'Ashritha looked stunning! The HD makeup lasted through 10 hours of heat and humidity with zero touch-ups needed.', NOW()),
(6, 4, 13, 4, 'High-energy DJ with great crowd reading skills! The dance floor was packed until 2 AM.', NOW());

-- 8. Insert Favorites
INSERT INTO favorites (id, user_id, vendor_id, created_at) VALUES
(1, 3, 1, NOW()),
(2, 3, 5, NOW()),
(3, 3, 10, NOW()),
(4, 3, 12, NOW()),
(5, 4, 2, NOW()),
(6, 4, 9, NOW());

-- =============================================================================
-- Reset AUTO_INCREMENT counters to follow seeded records safely
-- =============================================================================
ALTER TABLE users AUTO_INCREMENT = 10;
ALTER TABLE vendors AUTO_INCREMENT = 20;
ALTER TABLE events AUTO_INCREMENT = 10;
ALTER TABLE budget_items AUTO_INCREMENT = 20;
ALTER TABLE checklist_tasks AUTO_INCREMENT = 20;
ALTER TABLE booking_requests AUTO_INCREMENT = 10;
ALTER TABLE reviews AUTO_INCREMENT = 10;
ALTER TABLE favorites AUTO_INCREMENT = 10;

-- Verification query
SELECT 'Database schema & sample data successfully created!' AS status;
