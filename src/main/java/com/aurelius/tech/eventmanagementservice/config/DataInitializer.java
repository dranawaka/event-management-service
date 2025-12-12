package com.aurelius.tech.eventmanagementservice.config;

import com.aurelius.tech.eventmanagementservice.entity.Category;
import com.aurelius.tech.eventmanagementservice.entity.Event;
import com.aurelius.tech.eventmanagementservice.entity.User;
import com.aurelius.tech.eventmanagementservice.entity.Venue;
import com.aurelius.tech.eventmanagementservice.entity.enums.EventStatus;
import com.aurelius.tech.eventmanagementservice.entity.enums.EventVisibility;
import com.aurelius.tech.eventmanagementservice.entity.enums.UserRole;
import com.aurelius.tech.eventmanagementservice.entity.enums.UserStatus;
import com.aurelius.tech.eventmanagementservice.repository.CategoryRepository;
import com.aurelius.tech.eventmanagementservice.repository.EventRepository;
import com.aurelius.tech.eventmanagementservice.repository.UserRepository;
import com.aurelius.tech.eventmanagementservice.repository.VenueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DataInitializer(UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          VenueRepository venueRepository,
                          EventRepository eventRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.venueRepository = venueRepository;
        this.eventRepository = eventRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) {
        logger.info("Starting data initialization...");
        
        // Initialize sample data only if database is empty
        if (eventRepository.count() == 0) {
            initializeSampleData();
            logger.info("Sample data initialized successfully");
        } else {
            logger.info("Database already contains data. Skipping initialization.");
        }
    }
    
    private void initializeSampleData() {
        // Create sample organizers
        List<User> organizers = createSampleOrganizers();
        
        // Create sample categories
        List<Category> categories = createSampleCategories();
        
        // Create sample venues
        List<Venue> venues = createSampleVenues();
        
        // Create sample public events
        createSamplePublicEvents(organizers, categories, venues);
    }
    
    private List<User> createSampleOrganizers() {
        List<User> organizers = new ArrayList<>();
        
        String[][] organizerData = {
            {"john.doe@events.com", "John", "Doe", "+1-555-0101"},
            {"jane.smith@events.com", "Jane", "Smith", "+1-555-0102"},
            {"mike.johnson@events.com", "Mike", "Johnson", "+1-555-0103"},
            {"sarah.williams@events.com", "Sarah", "Williams", "+1-555-0104"}
        };
        
        for (String[] data : organizerData) {
            if (!userRepository.existsByEmail(data[0])) {
                User organizer = new User();
                organizer.setEmail(data[0]);
                organizer.setPassword(passwordEncoder.encode("password123"));
                organizer.setFirstName(data[1]);
                organizer.setLastName(data[2]);
                organizer.setPhone(data[3]);
                organizer.setRole(UserRole.ORGANIZER);
                organizer.setStatus(UserStatus.ACTIVE);
                organizers.add(userRepository.save(organizer));
                logger.info("Created organizer: {} {}", data[1], data[2]);
            } else {
                organizers.add(userRepository.findByEmail(data[0]).orElse(null));
            }
        }
        
        return organizers;
    }
    
    private List<Category> createSampleCategories() {
        List<Category> categories = new ArrayList<>();
        
        String[][] categoryData = {
            {"Music", "Music concerts, festivals, and performances"},
            {"Technology", "Tech conferences, workshops, and meetups"},
            {"Sports", "Sports events, games, and competitions"},
            {"Business", "Business conferences, seminars, and networking events"},
            {"Arts & Culture", "Art exhibitions, cultural events, and theater"},
            {"Food & Drink", "Food festivals, wine tastings, and culinary events"},
            {"Education", "Educational workshops, seminars, and training sessions"}
        };
        
        for (String[] data : categoryData) {
            if (!categoryRepository.existsByName(data[0])) {
                Category category = new Category();
                category.setName(data[0]);
                category.setDescription(data[1]);
                categories.add(categoryRepository.save(category));
                logger.info("Created category: {}", data[0]);
            } else {
                categoryRepository.findByName(data[0]).ifPresent(categories::add);
            }
        }
        
        return categories;
    }
    
    private List<Venue> createSampleVenues() {
        List<Venue> venues = new ArrayList<>();
        
        Object[][] venueData = {
            {"Grand Convention Center", "123 Main Street", "New York", "NY", "USA", "10001", 5000, 40.7128, -74.0060},
            {"Tech Hub Auditorium", "456 Innovation Drive", "San Francisco", "CA", "USA", "94102", 2000, 37.7749, -122.4194},
            {"Riverside Park", "789 Park Avenue", "Chicago", "IL", "USA", "60601", 10000, 41.8781, -87.6298},
            {"Downtown Theater", "321 Arts Boulevard", "Los Angeles", "CA", "USA", "90001", 1500, 34.0522, -118.2437},
            {"Sports Arena", "654 Stadium Road", "Boston", "MA", "USA", "02101", 8000, 42.3601, -71.0589},
            {"Community Hall", "987 Community Street", "Seattle", "WA", "USA", "98101", 500, 47.6062, -122.3321}
        };
        
        for (Object[] data : venueData) {
            Venue venue = new Venue();
            venue.setName((String) data[0]);
            venue.setAddress((String) data[1]);
            venue.setCity((String) data[2]);
            venue.setState((String) data[3]);
            venue.setCountry((String) data[4]);
            venue.setZipCode((String) data[5]);
            venue.setCapacity((Integer) data[6]);
            venue.setLatitude(BigDecimal.valueOf((Double) data[7]));
            venue.setLongitude(BigDecimal.valueOf((Double) data[8]));
            venues.add(venueRepository.save(venue));
            logger.info("Created venue: {}", data[0]);
        }
        
        return venues;
    }
    
    private void createSamplePublicEvents(List<User> organizers, List<Category> categories, List<Venue> venues) {
        LocalDateTime now = LocalDateTime.now();
        
        // Sample events data
        Object[][] eventsData = {
            // Title, Description, Organizer Index, Category Index, Venue Index, Start DateTime (days from now), Duration (hours), Capacity
            {"Summer Music Festival 2024", 
             "Join us for an amazing summer music festival featuring top artists from around the world. Three days of non-stop music, food, and fun!", 
             0, 0, 2, 30, 72, 10000},
            
            {"Tech Innovation Summit", 
             "A premier technology conference bringing together industry leaders, innovators, and entrepreneurs. Explore the latest trends in AI, cloud computing, and digital transformation.", 
             1, 1, 1, 15, 8, 2000},
            
            {"Marathon Championship 2024", 
             "Annual marathon championship featuring elite runners from across the country. Multiple race categories including 5K, 10K, half-marathon, and full marathon.", 
             2, 2, 4, 45, 6, 5000},
            
            {"Business Networking Conference", 
             "Connect with industry professionals, attend insightful workshops, and discover new business opportunities. Perfect for entrepreneurs and business leaders.", 
             0, 3, 0, 20, 6, 3000},
            
            {"Art Gallery Opening: Modern Masters", 
             "Exclusive opening of our new exhibition featuring works from renowned contemporary artists. Wine and cheese reception included.", 
             3, 4, 3, 10, 4, 200},
            
            {"Food & Wine Festival", 
             "Indulge in culinary delights from top chefs and sample wines from local vineyards. Live cooking demonstrations and tastings throughout the day.", 
             1, 5, 2, 25, 8, 5000},
            
            {"Web Development Bootcamp", 
             "Intensive 2-day bootcamp covering modern web development technologies including React, Node.js, and cloud deployment. Suitable for all skill levels.", 
             2, 6, 1, 12, 16, 100},
            
            {"Jazz Night Under the Stars", 
             "An intimate evening of live jazz music in a beautiful outdoor setting. Featuring local and international jazz artists.", 
             3, 0, 2, 18, 4, 800},
            
            {"Startup Pitch Competition", 
             "Watch innovative startups pitch their ideas to a panel of investors. Network with entrepreneurs and investors. Prizes for top pitches!", 
             0, 3, 0, 22, 5, 500},
            
            {"Yoga & Wellness Retreat", 
             "A day of relaxation and rejuvenation with yoga sessions, meditation workshops, and wellness talks. All levels welcome.", 
             1, 6, 5, 14, 6, 150}
        };
        
        for (Object[] data : eventsData) {
            Event event = new Event();
            event.setTitle((String) data[0]);
            event.setDescription((String) data[1]);
            event.setOrganizerId(organizers.get((Integer) data[2]).getId());
            event.setCategoryId(categories.get((Integer) data[3]).getId());
            event.setVenueId(venues.get((Integer) data[4]).getId());
            
            int daysFromNow = (Integer) data[5];
            int durationHours = (Integer) data[6];
            LocalDateTime startDateTime = now.plusDays(daysFromNow).withHour(10).withMinute(0).withSecond(0);
            LocalDateTime endDateTime = startDateTime.plusHours(durationHours);
            
            event.setStartDateTime(startDateTime);
            event.setEndDateTime(endDateTime);
            event.setCapacity((Integer) data[7]);
            event.setStatus(EventStatus.PUBLISHED);
            event.setVisibility(EventVisibility.PUBLIC);
            event.setImageUrl("https://via.placeholder.com/800x400?text=" + ((String) data[0]).replace(" ", "+"));
            
            eventRepository.save(event);
            logger.info("Created public event: {}", data[0]);
        }
        
        logger.info("Created {} sample public events", eventsData.length);
    }
}

