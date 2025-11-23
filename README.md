# Event Management Service

A comprehensive Spring Boot microservice for managing events, registrations, tickets, and attendees.

## Features

- User Management with Authentication & Authorization (JWT)
- Event Management (Create, Update, Publish, Cancel)
- Venue Management
- Ticket Management with Pricing
- Registration & Booking System
- Payment Processing
- QR Code Generation for Check-in
- Reviews & Ratings
- Category Management
- Notification System (RabbitMQ)
- Redis Caching
- RESTful API with OpenAPI/Swagger Documentation

## Tech Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17+
- **Database**: PostgreSQL 14+
- **Cache**: Redis 7+
- **Message Queue**: RabbitMQ 3+
- **Security**: Spring Security + JWT
- **API Docs**: SpringDoc OpenAPI (Swagger)
- **Validation**: Hibernate Validator
- **ORM**: Spring Data JPA
- **Migration**: Flyway
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Build**: Maven

## Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- Redis 7+
- RabbitMQ 3+

## Quick Start

1. **Start PostgreSQL, Redis, and RabbitMQ** (ensure they are running on your system)

2. **Configure Database**:
   - Update `application.yml` with your database credentials if needed

3. **Run the Application**:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the Application**:
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - API Docs: http://localhost:8080/api-docs

## API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - User login

### Events
- `GET /api/v1/events` - List all events
- `POST /api/v1/events` - Create new event
- `GET /api/v1/events/{id}` - Get event by ID
- `PUT /api/v1/events/{id}` - Update event
- `DELETE /api/v1/events/{id}` - Delete event
- `POST /api/v1/events/{id}/publish` - Publish event
- `POST /api/v1/events/{id}/cancel` - Cancel event
- `GET /api/v1/events/search?keyword=...` - Search events

### Tickets
- `GET /api/v1/events/{eventId}/tickets` - List tickets for event
- `POST /api/v1/events/{eventId}/tickets` - Create ticket type
- `GET /api/v1/tickets/{id}` - Get ticket by ID
- `PUT /api/v1/tickets/{id}` - Update ticket
- `DELETE /api/v1/tickets/{id}` - Delete ticket

### Registrations
- `GET /api/v1/registrations` - List user registrations
- `POST /api/v1/registrations` - Create registration
- `GET /api/v1/registrations/{id}` - Get registration by ID
- `DELETE /api/v1/registrations/{id}` - Cancel registration
- `GET /api/v1/registrations/{id}/qr` - Get QR code

### Payments
- `POST /api/v1/payments` - Process payment
- `GET /api/v1/payments/{id}` - Get payment details
- `POST /api/v1/payments/{id}/refund` - Process refund

### Venues
- `GET /api/v1/venues` - List all venues
- `POST /api/v1/venues` - Create new venue
- `GET /api/v1/venues/{id}` - Get venue by ID
- `PUT /api/v1/venues/{id}` - Update venue
- `DELETE /api/v1/venues/{id}` - Delete venue

### Categories
- `GET /api/v1/categories` - List all categories
- `POST /api/v1/categories` - Create category (Admin only)
- `PUT /api/v1/categories/{id}` - Update category (Admin only)
- `DELETE /api/v1/categories/{id}` - Delete category (Admin only)

## Configuration

### Environment Variables

- `DB_USERNAME` - Database username (default: postgres)
- `DB_PASSWORD` - Database password (default: password)
- `JWT_SECRET` - JWT secret key (change in production!)
- `REDIS_HOST` - Redis host (default: localhost)
- `REDIS_PORT` - Redis port (default: 6379)
- `RABBITMQ_HOST` - RabbitMQ host (default: localhost)
- `RABBITMQ_PORT` - RabbitMQ port (default: 5672)
- `RABBITMQ_USERNAME` - RabbitMQ username (default: guest)
- `RABBITMQ_PASSWORD` - RabbitMQ password (default: guest)

## Database Migrations

The application uses Flyway for database migrations. Migrations are located in `src/main/resources/db/migration/`.

## Security

- JWT-based authentication
- Role-based access control (ADMIN, ORGANIZER, ATTENDEE)
- Password encryption using BCrypt
- Secure endpoints with Spring Security

## Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

## Building

```bash
# Build JAR
./mvnw clean package
```

## License

MIT License


