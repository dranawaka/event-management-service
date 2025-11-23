# Event Management Service

A comprehensive Spring Boot microservice for managing events, registrations, tickets, and attendees.

---

## Table of Contents

1. [Overview](#overview)
2. [Use Cases](#use-cases)
3. [Architecture](#architecture)
4. [Data Models](#data-models)
5. [API Endpoints](#api-endpoints)
6. [Tech Stack](#tech-stack)
7. [Project Structure](#project-structure)
8. [Setup & Configuration](#setup--configuration)

---

## Overview

The Event Management Service provides a robust backend for creating, managing, and tracking events. It supports multiple user roles, ticketing, registrations, notifications, and analytics.

---

## Use Cases

### 1. User Management

| Use Case | Description | Actor |
|----------|-------------|-------|
| UC-1.1 | Register new user account | Guest |
| UC-1.2 | Login/Logout | User |
| UC-1.3 | Update profile information | User |
| UC-1.4 | Reset password | User |
| UC-1.5 | Manage user roles (Admin, Organizer, Attendee) | Admin |
| UC-1.6 | Deactivate/Reactivate user account | Admin |

### 2. Event Management

| Use Case | Description | Actor |
|----------|-------------|-------|
| UC-2.1 | Create new event | Organizer |
| UC-2.2 | Update event details | Organizer |
| UC-2.3 | Cancel/Delete event | Organizer |
| UC-2.4 | Publish/Unpublish event | Organizer |
| UC-2.5 | Clone existing event | Organizer |
| UC-2.6 | Set event capacity limits | Organizer |
| UC-2.7 | Add event categories/tags | Organizer |
| UC-2.8 | Upload event images/media | Organizer |
| UC-2.9 | Set event visibility (Public/Private) | Organizer |
| UC-2.10 | Create recurring events | Organizer |

### 3. Venue Management

| Use Case | Description | Actor |
|----------|-------------|-------|
| UC-3.1 | Add new venue | Organizer/Admin |
| UC-3.2 | Update venue details | Organizer/Admin |
| UC-3.3 | Delete venue | Admin |
| UC-3.4 | Check venue availability | Organizer |
| UC-3.5 | Set venue capacity | Organizer/Admin |

### 4. Ticket Management

| Use Case | Description | Actor |
|----------|-------------|-------|
| UC-4.1 | Create ticket types (VIP, Regular, Early Bird) | Organizer |
| UC-4.2 | Set ticket pricing | Organizer |
| UC-4.3 | Set ticket quantity limits | Organizer |
| UC-4.4 | Apply discount codes/coupons | Organizer |
| UC-4.5 | Enable/Disable ticket sales | Organizer |
| UC-4.6 | Set ticket sale date range | Organizer |
| UC-4.7 | Transfer ticket to another user | Attendee |
| UC-4.8 | Cancel/Refund ticket | Attendee/Organizer |

### 5. Registration & Booking

| Use Case | Description | Actor |
|----------|-------------|-------|
| UC-5.1 | Register for free event | Attendee |
| UC-5.2 | Purchase tickets | Attendee |
| UC-5.3 | View registration confirmation | Attendee |
| UC-5.4 | Cancel registration | Attendee |
| UC-5.5 | Join waitlist for sold-out events | Attendee |
| UC-5.6 | Apply promo codes at checkout | Attendee |
| UC-5.7 | Group registration | Attendee |

### 6. Check-in & Attendance

| Use Case | Description | Actor |
|----------|-------------|-------|
| UC-6.1 | Generate QR code for ticket | System |
| UC-6.2 | Scan QR code for check-in | Organizer/Staff |
| UC-6.3 | Manual check-in | Organizer/Staff |
| UC-6.4 | View real-time attendance | Organizer |
| UC-6.5 | Export attendee list | Organizer |
| UC-6.6 | Mark no-shows | Organizer |

### 7. Notifications

| Use Case | Description | Actor |
|----------|-------------|-------|
| UC-7.1 | Send registration confirmation email | System |
| UC-7.2 | Send event reminder notifications | System |
| UC-7.3 | Notify attendees of event updates | System |
| UC-7.4 | Send cancellation notifications | System |
| UC-7.5 | Waitlist promotion notification | System |
| UC-7.6 | Custom announcements to attendees | Organizer |

### 8. Search & Discovery

| Use Case | Description | Actor |
|----------|-------------|-------|
| UC-8.1 | Search events by keyword | User |
| UC-8.2 | Filter events by category | User |
| UC-8.3 | Filter events by date range | User |
| UC-8.4 | Filter events by location | User |
| UC-8.5 | View trending/featured events | User |
| UC-8.6 | Save/Bookmark events | User |
| UC-8.7 | View event recommendations | User |

### 9. Reviews & Feedback

| Use Case | Description | Actor |
|----------|-------------|-------|
| UC-9.1 | Submit event review/rating | Attendee |
| UC-9.2 | View event reviews | User |
| UC-9.3 | Respond to reviews | Organizer |
| UC-9.4 | Report inappropriate reviews | User |
| UC-9.5 | Moderate reviews | Admin |

### 10. Analytics & Reporting

| Use Case | Description | Actor |
|----------|-------------|-------|
| UC-10.1 | View event dashboard | Organizer |
| UC-10.2 | Track ticket sales | Organizer |
| UC-10.3 | View registration trends | Organizer |
| UC-10.4 | Export reports (CSV, PDF) | Organizer |
| UC-10.5 | View revenue analytics | Organizer/Admin |
| UC-10.6 | Platform-wide analytics | Admin |

### 11. Payment Processing

| Use Case | Description | Actor |
|----------|-------------|-------|
| UC-11.1 | Process credit card payments | System |
| UC-11.2 | Process refunds | Organizer/System |
| UC-11.3 | View payment history | Attendee/Organizer |
| UC-11.4 | Generate invoices | System |
| UC-11.5 | Payout to organizers | Admin/System |

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway                               │
└─────────────────────────────────────────────────────────────────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        ▼                       ▼                       ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│  Auth Service │    │ Event Service │    │ Payment Svc   │
└───────────────┘    └───────────────┘    └───────────────┘
        │                   │                       │
        └───────────────────┼───────────────────────┘
                            ▼
                   ┌───────────────┐
                   │   Database    │
                   │  (PostgreSQL) │
                   └───────────────┘
                            │
        ┌───────────────────┼───────────────────────┐
        ▼                   ▼                       ▼
┌───────────────┐   ┌───────────────┐    ┌───────────────┐
│     Redis     │   │ Elasticsearch │    │  RabbitMQ     │
│    (Cache)    │   │   (Search)    │    │   (Queue)     │
└───────────────┘   └───────────────┘    └───────────────┘
```

---

## Data Models

### Core Entities

```java
// User Entity
User {
    id: UUID
    email: String
    password: String (encrypted)
    firstName: String
    lastName: String
    phone: String
    role: Enum (ADMIN, ORGANIZER, ATTENDEE)
    status: Enum (ACTIVE, INACTIVE, SUSPENDED)
    createdAt: Timestamp
    updatedAt: Timestamp
}

// Event Entity
Event {
    id: UUID
    title: String
    description: Text
    organizerId: UUID (FK -> User)
    venueId: UUID (FK -> Venue)
    categoryId: UUID (FK -> Category)
    startDateTime: Timestamp
    endDateTime: Timestamp
    capacity: Integer
    status: Enum (DRAFT, PUBLISHED, CANCELLED, COMPLETED)
    visibility: Enum (PUBLIC, PRIVATE)
    imageUrl: String
    createdAt: Timestamp
    updatedAt: Timestamp
}

// Venue Entity
Venue {
    id: UUID
    name: String
    address: String
    city: String
    state: String
    country: String
    zipCode: String
    capacity: Integer
    latitude: Decimal
    longitude: Decimal
}

// Ticket Entity
Ticket {
    id: UUID
    eventId: UUID (FK -> Event)
    name: String
    description: String
    price: Decimal
    quantity: Integer
    sold: Integer
    saleStartDate: Timestamp
    saleEndDate: Timestamp
    status: Enum (AVAILABLE, SOLD_OUT, DISABLED)
}

// Registration Entity
Registration {
    id: UUID
    userId: UUID (FK -> User)
    eventId: UUID (FK -> Event)
    ticketId: UUID (FK -> Ticket)
    quantity: Integer
    totalAmount: Decimal
    status: Enum (CONFIRMED, CANCELLED, PENDING)
    qrCode: String
    registeredAt: Timestamp
}

// Payment Entity
Payment {
    id: UUID
    registrationId: UUID (FK -> Registration)
    amount: Decimal
    currency: String
    paymentMethod: String
    transactionId: String
    status: Enum (SUCCESS, FAILED, REFUNDED, PENDING)
    paidAt: Timestamp
}
```

---

## API Endpoints

### Authentication
```
POST   /api/v1/auth/register       - Register new user
POST   /api/v1/auth/login          - User login
POST   /api/v1/auth/logout         - User logout
POST   /api/v1/auth/refresh        - Refresh access token
POST   /api/v1/auth/forgot-password - Request password reset
POST   /api/v1/auth/reset-password  - Reset password
```

### Users
```
GET    /api/v1/users               - List all users (Admin)
GET    /api/v1/users/{id}          - Get user by ID
PUT    /api/v1/users/{id}          - Update user
DELETE /api/v1/users/{id}          - Delete user (Admin)
GET    /api/v1/users/me            - Get current user profile
PUT    /api/v1/users/me            - Update current user profile
```

### Events
```
GET    /api/v1/events              - List all events (with filters)
POST   /api/v1/events              - Create new event
GET    /api/v1/events/{id}         - Get event by ID
PUT    /api/v1/events/{id}         - Update event
DELETE /api/v1/events/{id}         - Delete event
POST   /api/v1/events/{id}/publish - Publish event
POST   /api/v1/events/{id}/cancel  - Cancel event
GET    /api/v1/events/{id}/attendees - Get event attendees
GET    /api/v1/events/organizer/{id} - Get events by organizer
GET    /api/v1/events/search       - Search events
```

### Venues
```
GET    /api/v1/venues              - List all venues
POST   /api/v1/venues              - Create new venue
GET    /api/v1/venues/{id}         - Get venue by ID
PUT    /api/v1/venues/{id}         - Update venue
DELETE /api/v1/venues/{id}         - Delete venue
GET    /api/v1/venues/{id}/availability - Check venue availability
```

### Tickets
```
GET    /api/v1/events/{eventId}/tickets     - List tickets for event
POST   /api/v1/events/{eventId}/tickets     - Create ticket type
GET    /api/v1/tickets/{id}                 - Get ticket by ID
PUT    /api/v1/tickets/{id}                 - Update ticket
DELETE /api/v1/tickets/{id}                 - Delete ticket
```

### Registrations
```
GET    /api/v1/registrations                - List user registrations
POST   /api/v1/registrations                - Create registration
GET    /api/v1/registrations/{id}           - Get registration by ID
DELETE /api/v1/registrations/{id}           - Cancel registration
GET    /api/v1/registrations/{id}/qr        - Get QR code
POST   /api/v1/registrations/{id}/transfer  - Transfer ticket
```

### Check-in
```
POST   /api/v1/checkin/scan        - Scan QR code for check-in
POST   /api/v1/checkin/manual      - Manual check-in
GET    /api/v1/events/{id}/checkins - Get check-in stats
```

### Payments
```
POST   /api/v1/payments            - Process payment
GET    /api/v1/payments/{id}       - Get payment details
POST   /api/v1/payments/{id}/refund - Process refund
GET    /api/v1/payments/history    - Get payment history
```

### Categories
```
GET    /api/v1/categories          - List all categories
POST   /api/v1/categories          - Create category (Admin)
PUT    /api/v1/categories/{id}     - Update category (Admin)
DELETE /api/v1/categories/{id}     - Delete category (Admin)
```

### Reviews
```
GET    /api/v1/events/{id}/reviews - Get event reviews
POST   /api/v1/events/{id}/reviews - Submit review
PUT    /api/v1/reviews/{id}        - Update review
DELETE /api/v1/reviews/{id}        - Delete review
```

### Analytics
```
GET    /api/v1/analytics/events/{id}        - Event analytics
GET    /api/v1/analytics/organizer/{id}     - Organizer dashboard
GET    /api/v1/analytics/platform           - Platform analytics (Admin)
GET    /api/v1/analytics/reports/export     - Export reports
```

---

## Tech Stack

| Component | Technology |
|-----------|------------|
| Framework | Spring Boot 3.x |
| Language | Java 17+ |
| Database | PostgreSQL |
| Cache | Redis |
| Search | Elasticsearch |
| Queue | RabbitMQ |
| Security | Spring Security + JWT |
| API Docs | SpringDoc OpenAPI (Swagger) |
| Validation | Hibernate Validator |
| ORM | Spring Data JPA |
| Migration | Flyway |
| Testing | JUnit 5, Mockito, Testcontainers |
| Build | Maven/Gradle |
| Containerization | Docker |

---

## Project Structure

```
event-management-service/
├── src/
│   ├── main/
│   │   ├── java/com/example/eventmanagement/
│   │   │   ├── EventManagementApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── RedisConfig.java
│   │   │   │   ├── SwaggerConfig.java
│   │   │   │   └── RabbitMQConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── EventController.java
│   │   │   │   ├── VenueController.java
│   │   │   │   ├── TicketController.java
│   │   │   │   ├── RegistrationController.java
│   │   │   │   ├── PaymentController.java
│   │   │   │   └── AnalyticsController.java
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── UserService.java
│   │   │   │   ├── EventService.java
│   │   │   │   ├── VenueService.java
│   │   │   │   ├── TicketService.java
│   │   │   │   ├── RegistrationService.java
│   │   │   │   ├── PaymentService.java
│   │   │   │   ├── NotificationService.java
│   │   │   │   ├── QRCodeService.java
│   │   │   │   └── AnalyticsService.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── EventRepository.java
│   │   │   │   ├── VenueRepository.java
│   │   │   │   ├── TicketRepository.java
│   │   │   │   ├── RegistrationRepository.java
│   │   │   │   └── PaymentRepository.java
│   │   │   ├── entity/
│   │   │   │   ├── User.java
│   │   │   │   ├── Event.java
│   │   │   │   ├── Venue.java
│   │   │   │   ├── Ticket.java
│   │   │   │   ├── Registration.java
│   │   │   │   ├── Payment.java
│   │   │   │   ├── Category.java
│   │   │   │   └── Review.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   └── response/
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── BusinessException.java
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   ├── mapper/
│   │   │   │   ├── UserMapper.java
│   │   │   │   └── EventMapper.java
│   │   │   └── util/
│   │   │       ├── Constants.java
│   │   │       └── DateUtils.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/
│   │           ├── V1__create_users_table.sql
│   │           ├── V2__create_events_table.sql
│   │           └── V3__create_tickets_table.sql
│   └── test/
│       └── java/com/example/eventmanagement/
│           ├── controller/
│           ├── service/
│           └── repository/
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

---

## Setup & Configuration

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- Redis 7+
- Docker (optional)

### application.yml

```yaml
server:
  port: 8080

spring:
  application:
    name: event-management-service
  
  datasource:
    url: jdbc:postgresql://localhost:5432/eventdb
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:password}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  
  redis:
    host: localhost
    port: 6379
  
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

jwt:
  secret: ${JWT_SECRET:your-secret-key}
  expiration: 86400000  # 24 hours

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

### Docker Compose

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - redis
      - rabbitmq
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - DB_USERNAME=postgres
      - DB_PASSWORD=password

  postgres:
    image: postgres:14
    environment:
      POSTGRES_DB: eventdb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"

volumes:
  postgres_data:
```

### Running the Application

```bash
# Clone repository
git clone https://github.com/your-repo/event-management-service.git
cd event-management-service

# Run with Docker
docker-compose up -d

# Or run locally
./mvnw spring-boot:run

# Run tests
./mvnw test

# Build JAR
./mvnw clean package
```

---

## License

MIT License
