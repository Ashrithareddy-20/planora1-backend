-- =============================================================================
-- Planora Initial Seed Data
-- BCrypt password for default users is: password123
-- Hash: $2a$10$/VvqNic9.xOUem1cAEP/3uYh/Q8x7Z6Pr3S5cm7ASofgq3SYU5dea
-- =============================================================================

-- Seed Users
INSERT INTO users (id, name, email, password, phone, role, created_at) VALUES
(1, 'System Administrator', 'admin@planora.com', '$2a$10$/VvqNic9.xOUem1cAEP/3uYh/Q8x7Z6Pr3S5cm7ASofgq3SYU5dea', '+91 98765 43210', 'ROLE_ADMIN', CURRENT_TIMESTAMP),
(2, 'Royal Palace Banquets', 'vendor@planora.com', '$2a$10$/VvqNic9.xOUem1cAEP/3uYh/Q8x7Z6Pr3S5cm7ASofgq3SYU5dea', '+91 91234 56789', 'ROLE_VENDOR', CURRENT_TIMESTAMP),
(3, 'Ashritha Reddy', 'user@planora.com', '$2a$10$/VvqNic9.xOUem1cAEP/3uYh/Q8x7Z6Pr3S5cm7ASofgq3SYU5dea', '+91 99887 76655', 'ROLE_USER', CURRENT_TIMESTAMP),
(4, 'Vikram Sharma', 'vikram@planora.com', '$2a$10$/VvqNic9.xOUem1cAEP/3uYh/Q8x7Z6Pr3S5cm7ASofgq3SYU5dea', '+91 98111 22334', 'ROLE_USER', CURRENT_TIMESTAMP),
(5, 'Artisan Caterers Co.', 'vendor2@planora.com', '$2a$10$/VvqNic9.xOUem1cAEP/3uYh/Q8x7Z6Pr3S5cm7ASofgq3SYU5dea', '+91 98450 33445', 'ROLE_VENDOR', CURRENT_TIMESTAMP);

-- Seed Vendors
INSERT INTO vendors (id, name, category, description, price_starting, max_guest_capacity, city, address, contact_phone, contact_email, rating, review_count, image_url, featured, owner_id, created_at) VALUES
(1, 'The Grand Palace & Royal Lawns', 'VENUE',
 'Spacious 5-acre banquet resort featuring manicured lawns, grand ballroom, and luxurious bridal suites.',
 250000.0, 1200, 'Bangalore', 'Palace Grounds, Bellary Road, Bangalore',
 '+91 80 2345 6789', 'events@grandpalace.com', 4.9, 42,
 'https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=1200&q=80', TRUE, 2, CURRENT_TIMESTAMP),

(2, 'Taj Lakefront Heritage Resort', 'VENUE',
 'Royal Rajasthani heritage palace surrounded by shimmering water and majestic courtyard pavilions.',
 450000.0, 800, 'Jaipur', 'Lake Pichola Outer Circle, Jaipur',
 '+91 141 2233 445', 'weddings@tajheritage.com', 5.0, 68,
 'https://images.unsplash.com/photo-1544077960-604201fe74bc?auto=format&fit=crop&w=1200&q=80', TRUE, 2, CURRENT_TIMESTAMP),

(3, 'Bayview Seaside Banquet & Terrace', 'VENUE',
 'Contemporary oceanfront banquet with an open sky terrace deck overlooking the Arabian Sea.',
 180000.0, 500, 'Mumbai', 'Juhu Beach Road, Mumbai',
 '+91 22 2654 9870', 'info@bayviewbanquet.com', 4.7, 31,
 'https://images.unsplash.com/photo-1511795409834-ef04bbd61622?auto=format&fit=crop&w=1200&q=80', TRUE, NULL, CURRENT_TIMESTAMP),

(4, 'Whispering Palms Luxury Villa', 'VENUE',
 'Private boutique lawn and heritage Portuguese villa ideal for intimate beach destination weddings and parties.',
 120000.0, 250, 'Goa', 'Candolim Beach Road, North Goa',
 '+91 832 2456 123', 'stay@whisperingpalms.com', 4.8, 25,
 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=80', FALSE, NULL, CURRENT_TIMESTAMP),

(5, 'Royal Feast Gourmet Caterers', 'CATERING',
 'Award-winning fine-dining catering offering authentic Awadhi, South Indian, continental live counters & artisan desserts.',
 120000.0, 1000, 'Bangalore', 'Indiranagar 100ft Road, Bangalore',
 '+91 98450 11223', 'orders@royalfeast.in', 4.9, 54,
 'https://images.unsplash.com/photo-1555244162-803834f70033?auto=format&fit=crop&w=1200&q=80', TRUE, 5, CURRENT_TIMESTAMP),

(6, 'Saffron & Spice Authentic Banquets', 'CATERING',
 'Specialists in royal North Indian, Mughlai, and Chaat counters with premium culinary presentation.',
 150000.0, 800, 'Mumbai', 'Bandra West, Mumbai',
 '+91 98200 44332', 'contact@saffronspice.com', 4.7, 39,
 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=1200&q=80', FALSE, NULL, CURRENT_TIMESTAMP),

(7, 'Delicacy Fusion Catering Co.', 'CATERING',
 'Customized menu design for corporate lunches, birthdays, and cocktail receptions with interactive live stations.',
 95000.0, 600, 'Delhi', 'Connaught Place, New Delhi',
 '+91 11 4152 7890', 'hello@delicacycatering.com', 4.6, 28,
 'https://images.unsplash.com/photo-1498837167922-ddd27525d352?auto=format&fit=crop&w=1200&q=80', FALSE, NULL, CURRENT_TIMESTAMP),

(8, 'Bloom & Blossom Floral Sculptures', 'DECORATION',
 'Exquisite exotic floral mandaps, fairy-light canopies, aesthetic photo backdrops and eco-friendly themes.',
 65000.0, NULL, 'Bangalore', 'Koramangala 4th Block, Bangalore',
 '+91 99001 23456', 'decor@bloombllsom.com', 4.8, 37,
 'https://images.unsplash.com/photo-1527529482837-4698179dc6ce?auto=format&fit=crop&w=1200&q=80', TRUE, NULL, CURRENT_TIMESTAMP),

(9, 'Ethereal Dream Weddings Decor', 'DECORATION',
 'Grand royal entry gates, thematic stage styling, crystal chandeliers and royal velvet drapes.',
 110000.0, NULL, 'Jaipur', 'MI Road, Jaipur',
 '+91 94140 88776', 'dream@etherealweddings.in', 4.9, 44,
 'https://images.unsplash.com/photo-1465495976277-4387d4b0b4c6?auto=format&fit=crop&w=1200&q=80', TRUE, NULL, CURRENT_TIMESTAMP),

(10, 'Candid Moments Photography & Cinema', 'PHOTOGRAPHY',
 'Cinematic wedding films, candid storytelling photography, 4K aerial drone coverage, and signature photo albums.',
 75000.0, NULL, 'Bangalore', 'HSR Layout Sector 2, Bangalore',
 '+91 98800 66778', 'shoot@candidmoments.in', 4.9, 62,
 'https://images.unsplash.com/photo-1537633552985-df8429e8048b?auto=format&fit=crop&w=1200&q=80', TRUE, NULL, CURRENT_TIMESTAMP),

(11, 'PixelArt Stories & Drone Shoots', 'PHOTOGRAPHY',
 'Award-winning team covering pre-weddings, corporate celebrations, birthday highlights with fast 48-hour preview delivery.',
 60000.0, NULL, 'Delhi', 'South Extension, New Delhi',
 '+91 98110 55443', 'team@pixelartstories.com', 4.8, 30,
 'https://images.unsplash.com/photo-1606800052052-a08af7148866?auto=format&fit=crop&w=1200&q=80', FALSE, NULL, CURRENT_TIMESTAMP),

(12, 'Glow & Grace Bridal Artistry', 'MAKEUP',
 'Celebrity bridal makeup artist, HD airbrush makeup, saree draping, and contemporary hair styling.',
 25000.0, NULL, 'Bangalore', 'Sadashivanagar, Bangalore',
 '+91 97400 33221', 'artistry@glowandgrace.com', 4.9, 58,
 'https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?auto=format&fit=crop&w=1200&q=80', TRUE, NULL, CURRENT_TIMESTAMP),

(13, 'Rhythm Beats Live Band & DJ Setup', 'ENTERTAINMENT',
 'Complete sound, club lighting, cold pyro effects, and versatile DJ playing Bollywood, EDM, and Retro classics.',
 45000.0, NULL, 'Bangalore', 'MG Road, Bangalore',
 '+91 98440 99887', 'booking@rhythmbeats.com', 4.8, 41,
 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=1200&q=80', FALSE, NULL, CURRENT_TIMESTAMP),

(14, 'Regal Vintage Cars & Luxury Convoy', 'TRANSPORTATION',
 'Chauffeured classic 1930s vintage cars for couple entries plus premium Mercedes and BMW fleets for VIP guests.',
 35000.0, NULL, 'Bangalore', 'Richmond Town, Bangalore',
 '+91 99010 44556', 'fleet@regalcars.in', 4.7, 19,
 'https://images.unsplash.com/photo-1511919884226-fd3cad34687c?auto=format&fit=crop&w=1200&q=80', FALSE, NULL, CURRENT_TIMESTAMP);

-- Seed Events
INSERT INTO events (id, title, event_type, description, event_date, location, guest_count, total_budget, status, user_id, created_at) VALUES
(1, 'Rohan & Ananya''s Grand Wedding & Reception', 'Wedding',
 'A grand 2-day wedding celebration with sangeet evening and traditional reception dinner.',
 '2026-12-15', 'Bangalore', 450, 1500000.0, 'UPCOMING', 3, CURRENT_TIMESTAMP),

(2, 'Priya''s 30th Milestone Birthday Gala', 'Birthday',
 'Rooftop cocktail party with live DJ, customized dessert buffet, and photo booth memories.',
 '2026-11-20', 'Bangalore', 80, 150000.0, 'UPCOMING', 3, CURRENT_TIMESTAMP),

(3, 'Pan-India Tech Founders Summit 2026', 'Corporate',
 'Annual gathering of 300 tech leaders, keynote presentations, networking dinners, and exhibition booths.',
 '2027-01-10', 'Bangalore', 300, 800000.0, 'UPCOMING', 4, CURRENT_TIMESTAMP);

-- Seed Budget Items
INSERT INTO budget_items (id, event_id, category, title, allocated_amount, actual_amount, notes, created_at) VALUES
(1, 1, 'VENUE', 'Venue Rental & Setup', 525000.0, 250000.0, 'Booked The Grand Palace & Royal Lawns', CURRENT_TIMESTAMP),
(2, 1, 'CATERING', 'Catering & Beverage Service', 375000.0, 120000.0, 'Booked Royal Feast Gourmet Caterers', CURRENT_TIMESTAMP),
(3, 1, 'DECORATION', 'Floral & Theme Decoration', 225000.0, 0.0, 'Shortlisted Bloom & Blossom', CURRENT_TIMESTAMP),
(4, 1, 'PHOTOGRAPHY', 'Photography & Cinematography', 150000.0, 75000.0, 'Candid Moments booked', CURRENT_TIMESTAMP),
(5, 1, 'ENTERTAINMENT', 'DJ, Sound & Lights', 120000.0, 45000.0, 'Rhythm Beats DJ setup', CURRENT_TIMESTAMP),
(6, 1, 'MAKEUP', 'Styling & Makeup', 60000.0, 25000.0, 'Glow & Grace Bridal Artistry', CURRENT_TIMESTAMP),
(7, 1, 'TRANSPORTATION', 'Guest Transportation / Logistics', 45000.0, 0.0, 'Vintage car and mini-bus shuttles', CURRENT_TIMESTAMP),

(8, 2, 'VENUE', 'Rooftop Venue Reservation', 50000.0, 45000.0, 'Advance paid for rooftop terrace', CURRENT_TIMESTAMP),
(9, 2, 'CATERING', 'Cocktail Menu & Custom Cake', 45000.0, 40000.0, 'Fusion tapas and 3-tier birthday cake', CURRENT_TIMESTAMP),
(10, 2, 'ENTERTAINMENT', 'DJ & Club Sound Setup', 30000.0, 25000.0, 'Sound check confirmed', CURRENT_TIMESTAMP),
(11, 2, 'DECORATION', 'Balloon Arch & Neon Signage', 25000.0, 20000.0, 'Custom neon hashtag sign ordered', CURRENT_TIMESTAMP);

-- Seed Checklist Tasks
INSERT INTO checklist_tasks (id, event_id, title, category, due_date, completed, priority, created_at) VALUES
(1, 1, 'Finalize and book wedding venue & lawns', 'Venue', '2026-10-25', TRUE, 'HIGH', CURRENT_TIMESTAMP),
(2, 1, 'Select catering menu and schedule food tasting', 'Catering', '2026-11-05', TRUE, 'HIGH', CURRENT_TIMESTAMP),
(3, 1, 'Confirm theme, stage decor, and floral arrangements', 'Decoration', '2026-11-15', FALSE, 'MEDIUM', CURRENT_TIMESTAMP),
(4, 1, 'Hire wedding photographer and videographer', 'Photography', '2026-11-10', TRUE, 'HIGH', CURRENT_TIMESTAMP),
(5, 1, 'Send digital & physical invitations to guests', 'Invitations', '2026-11-20', FALSE, 'MEDIUM', CURRENT_TIMESTAMP),
(6, 1, 'Finalize makeup artist and hair stylist appointments', 'Makeup', '2026-11-25', TRUE, 'MEDIUM', CURRENT_TIMESTAMP),
(7, 1, 'Book DJ, sound system, and sound check slot', 'Entertainment', '2026-11-28', FALSE, 'MEDIUM', CURRENT_TIMESTAMP),
(8, 1, 'Arrange logistics and guest transportation', 'Transport', '2026-12-05', FALSE, 'LOW', CURRENT_TIMESTAMP),
(9, 1, 'Conduct final rehearsal and review vendor timeline', 'Coordination', '2026-12-14', FALSE, 'HIGH', CURRENT_TIMESTAMP),

(10, 2, 'Book rooftop venue and pay deposit', 'Venue', '2026-10-28', TRUE, 'HIGH', CURRENT_TIMESTAMP),
(11, 2, 'Design & order customized 3-tier birthday cake', 'Catering', '2026-11-05', TRUE, 'HIGH', CURRENT_TIMESTAMP),
(12, 2, 'Finalize party playlist with DJ', 'Entertainment', '2026-11-15', FALSE, 'MEDIUM', CURRENT_TIMESTAMP),
(13, 2, 'Send RSVP reminders to attendees', 'Invitations', '2026-11-18', FALSE, 'HIGH', CURRENT_TIMESTAMP);

-- Seed Booking Requests
INSERT INTO booking_requests (id, user_id, vendor_id, event_id, requested_date, estimated_price, message, status, vendor_response, created_at) VALUES
(1, 3, 1, 1, '2026-12-15', 250000.0,
 'Requesting venue booking for wedding evening ceremony and reception banquet.',
 'ACCEPTED',
 'Congratulations! Your booking for The Grand Palace is confirmed for the requested date. Our banquet manager will reach out for the layout setup.',
 CURRENT_TIMESTAMP),

(2, 3, 5, 1, '2026-12-15', 120000.0,
 'Interested in 4-course banquet menu for 450 guests. Please confirm tasting schedule.',
 'ACCEPTED',
 'We would be honored to cater your special day. Food tasting has been scheduled for this coming weekend.',
 CURRENT_TIMESTAMP),

(3, 3, 10, 1, '2026-12-15', 75000.0,
 'Looking for candid photography + drone coverage for morning ceremony and evening reception.',
 'PENDING',
 NULL,
 CURRENT_TIMESTAMP),

(4, 3, 8, 1, '2026-12-15', 65000.0,
 'Need pastel floral theme mandap and fairy light entrance canopy.',
 'PENDING',
 NULL,
 CURRENT_TIMESTAMP);

-- Seed Reviews
INSERT INTO reviews (id, user_id, vendor_id, rating, comment, created_at) VALUES
(1, 3, 1, 5, 'The Grand Palace exceeded all our expectations! The lawn was breathtaking and the banquet manager ensured flawless guest coordination.', CURRENT_TIMESTAMP),
(2, 3, 5, 5, 'Unbelievable food quality! All our guests kept raving about the Biryani, live chaat counters, and pistachio kulfi. Worth every penny!', CURRENT_TIMESTAMP),
(3, 3, 10, 5, 'Photos came out like a cinematic movie! The team was punctual, creative, and captured the purest emotions without intrusive staging.', CURRENT_TIMESTAMP),
(4, 4, 2, 5, 'An absolute dream heritage property. If you want a royal fairy-tale wedding, Taj Lakefront Heritage is unmatched.', CURRENT_TIMESTAMP),
(5, 4, 12, 5, 'Ashritha looked stunning! The HD makeup lasted through 10 hours of heat and humidity with zero touch-ups needed.', CURRENT_TIMESTAMP),
(6, 4, 13, 4, 'High-energy DJ with great crowd reading skills! The dance floor was packed until 2 AM.', CURRENT_TIMESTAMP);

-- Seed Favorites
INSERT INTO favorites (id, user_id, vendor_id, created_at) VALUES
(1, 3, 1, CURRENT_TIMESTAMP),
(2, 3, 5, CURRENT_TIMESTAMP),
(3, 3, 10, CURRENT_TIMESTAMP),
(4, 3, 12, CURRENT_TIMESTAMP),
(5, 4, 2, CURRENT_TIMESTAMP),
(6, 4, 9, CURRENT_TIMESTAMP);
