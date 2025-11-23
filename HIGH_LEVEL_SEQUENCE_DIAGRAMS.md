# Event Management Service - High-Level Sequence Diagrams

This document provides high-level sequence diagrams showing the overall system architecture and major business flows at a macro level.

---

## 1. System Architecture Overview

```mermaid
sequenceDiagram
    participant User
    participant API as REST API<br/>(Controllers)
    participant Service as Business Logic<br/>(Services)
    participant Data as Data Layer<br/>(Repositories)
    participant DB as PostgreSQL<br/>Database
    participant Cache as Redis<br/>Cache
    participant MQ as RabbitMQ<br/>Message Queue
    participant External as External Services<br/>(Email/SMS)

    Note over User,External: Event Management Service - High Level Architecture

    User->>API: HTTP Request
    API->>Service: Business Operation
    
    Service->>Cache: Check Cache
    Cache-->>Service: Cache Hit/Miss
    
    alt Cache Miss
        Service->>Data: Query Data
        Data->>DB: SQL Query
        DB-->>Data: Result Set
        Data-->>Service: Entity/Data
        Service->>Cache: Store in Cache
    else Cache Hit
        Cache-->>Service: Cached Data
    end
    
    Service->>Service: Business Logic Processing
    
    alt Requires Persistence
        Service->>Data: Save/Update
        Data->>DB: INSERT/UPDATE
        DB-->>Data: Confirmation
        Data-->>Service: Saved Entity
    end
    
    alt Requires Async Notification
        Service->>MQ: Publish Message
        MQ-->>Service: Acknowledged
        MQ->>External: Deliver Message
        External-->>MQ: Processed
    end
    
    Service-->>API: Response Data
    API-->>User: HTTP Response
```

---

## 2. Complete Event Lifecycle - High Level

```mermaid
sequenceDiagram
    participant Attendee
    participant Organizer
    participant API as Event Management<br/>Service API
    participant Core as Core Services<br/>(Event, Registration, Payment)
    participant Storage as Data Storage<br/>(PostgreSQL + Redis)
    participant Queue as Message Queue<br/>(RabbitMQ)
    participant Notify as Notification<br/>Services

    Note over Attendee,Notify: Complete Event Lifecycle - High Level View

    rect rgb(200, 220, 255)
        Note over Organizer,Storage: Phase 1: Event Setup
        Organizer->>API: Create Event
        API->>Core: EventService.createEvent()
        Core->>Storage: Save Event (DRAFT)
        Storage-->>Core: Event Created
        Core-->>API: Event Response
        API-->>Organizer: Event Created
        
        Organizer->>API: Create Tickets
        API->>Core: TicketService.createTicket()
        Core->>Storage: Save Ticket Types
        Storage-->>Core: Tickets Created
        Core-->>API: Tickets Response
        API-->>Organizer: Tickets Created
        
        Organizer->>API: Publish Event
        API->>Core: EventService.publishEvent()
        Core->>Storage: Update Status (PUBLISHED)
        Storage-->>Core: Event Published
        Core-->>API: Event Published
        API-->>Organizer: Event Live
    end

    rect rgb(255, 240, 200)
        Note over Attendee,Notify: Phase 2: Registration & Payment
        Attendee->>API: Register for Event
        API->>Core: RegistrationService.createRegistration()
        Core->>Storage: Validate & Create Registration
        Storage-->>Core: Registration (PENDING)
        Core->>Core: Generate QR Code
        Core->>Storage: Update with QR Code
        Core->>Queue: Publish Registration Event
        Queue-->>Core: Acknowledged
        Core-->>API: Registration Created
        API-->>Attendee: Registration Confirmed
        
        Attendee->>API: Process Payment
        API->>Core: PaymentService.processPayment()
        Core->>Storage: Create Payment Record
        Storage-->>Core: Payment (SUCCESS)
        Core->>Storage: Update Registration (CONFIRMED)
        Storage-->>Core: Updated
        Core->>Queue: Publish Payment Event
        Queue-->>Core: Acknowledged
        Core-->>API: Payment Processed
        API-->>Attendee: Payment Confirmed
        
        Queue->>Notify: Registration Confirmation
        Notify->>Attendee: Email/SMS Notification
    end

    rect rgb(200, 255, 200)
        Note over Attendee,Storage: Phase 3: Event Day Operations
        Attendee->>API: Get QR Code
        API->>Core: RegistrationService.getRegistration()
        Core->>Storage: Fetch Registration
        Storage-->>Core: Registration with QR
        Core-->>API: QR Code
        API-->>Attendee: QR Code Image
        
        Note over Attendee,Storage: Check-in at Event Venue
        Organizer->>API: Verify QR Code
        API->>Core: RegistrationService.getByQrCode()
        Core->>Storage: Validate QR Code
        Storage-->>Core: Registration Details
        Core-->>API: Registration Status
        API-->>Organizer: Check-in Status
    end
```

---

## 3. System Layers & Data Flow

```mermaid
sequenceDiagram
    participant Client
    participant API as API Layer<br/>(REST Controllers)
    participant BL as Business Logic<br/>(Service Layer)
    participant DA as Data Access<br/>(Repository Layer)
    participant DB as Database<br/>(PostgreSQL)
    participant Cache as Cache<br/>(Redis)
    participant MQ as Message Queue<br/>(RabbitMQ)

    Note over Client,MQ: System Layers & Data Flow

    Client->>API: HTTP Request<br/>(JSON)
    
    API->>API: Validate Request<br/>(@Valid)
    API->>BL: Invoke Service Method
    
    BL->>BL: Business Rules<br/>& Validation
    
    alt Read Operation
        BL->>Cache: Check Cache Key
        Cache-->>BL: Cache Result
        
        alt Cache Hit
            BL-->>API: Cached Data
        else Cache Miss
            BL->>DA: Repository Query
            DA->>DB: SQL Execution
            DB-->>DA: Result Set
            DA-->>BL: Entity Objects
            BL->>Cache: Store in Cache
            BL-->>API: Data
        end
    else Write Operation
        BL->>DA: Repository Save
        DA->>DB: INSERT/UPDATE
        DB-->>DA: Confirmation
        DA-->>BL: Saved Entity
        BL->>Cache: Invalidate Cache
        BL-->>API: Result
    end
    
    alt Async Operation Required
        BL->>MQ: Publish Message
        MQ-->>BL: Acknowledged
    end
    
    API->>API: Build Response<br/>(DTO)
    API-->>Client: HTTP Response<br/>(JSON)
```

---

## 4. User Journey - High Level

```mermaid
sequenceDiagram
    participant User
    participant System as Event Management<br/>System
    participant Auth as Authentication<br/>Service
    participant Event as Event<br/>Service
    participant Reg as Registration<br/>Service
    participant Pay as Payment<br/>Service
    participant Notify as Notification<br/>System

    Note over User,Notify: User Journey - From Registration to Event Attendance

    User->>System: Register Account
    System->>Auth: Create User
    Auth-->>System: User Created
    System-->>User: Account Created
    
    User->>System: Login
    System->>Auth: Authenticate
    Auth-->>System: Authentication Token
    System-->>User: Login Success
    
    User->>System: Browse Events
    System->>Event: Get Published Events
    Event-->>System: Event List
    System-->>User: Events Displayed
    
    User->>System: Select Event
    System->>Event: Get Event Details
    Event-->>System: Event Information
    System-->>User: Event Details
    
    User->>System: Register for Event
    System->>Reg: Create Registration
    Reg->>Reg: Generate QR Code
    Reg-->>System: Registration Created
    System-->>User: Registration Confirmed
    
    User->>System: Make Payment
    System->>Pay: Process Payment
    Pay->>Reg: Update Registration Status
    Pay-->>System: Payment Processed
    System-->>User: Payment Confirmed
    
    System->>Notify: Send Confirmation
    Notify->>User: Email/SMS Notification
    
    Note over User,Notify: Event Day
    User->>System: Retrieve QR Code
    System->>Reg: Get Registration QR
    Reg-->>System: QR Code
    System-->>User: QR Code Image
    
    Note over User,Notify: At Venue
    System->>Reg: Verify QR Code
    Reg-->>System: Registration Valid
    System-->>User: Check-in Complete
```

---

## 5. Organizer Journey - High Level

```mermaid
sequenceDiagram
    participant Organizer
    participant System as Event Management<br/>System
    participant Event as Event<br/>Service
    participant Ticket as Ticket<br/>Service
    participant Venue as Venue<br/>Service
    participant Reg as Registration<br/>Service
    participant Analytics as Analytics<br/>& Reports

    Note over Organizer,Analytics: Organizer Journey - Event Management

    Organizer->>System: Login
    System-->>Organizer: Authenticated
    
    Organizer->>System: Create Venue
    System->>Venue: Save Venue
    Venue-->>System: Venue Created
    System-->>Organizer: Venue Saved
    
    Organizer->>System: Create Event
    System->>Event: Save Event (DRAFT)
    Event-->>System: Event Created
    System-->>Organizer: Event Draft Created
    
    Organizer->>System: Create Ticket Types
    System->>Ticket: Save Tickets
    Ticket-->>System: Tickets Created
    System-->>Organizer: Tickets Configured
    
    Organizer->>System: Publish Event
    System->>Event: Update Status (PUBLISHED)
    Event-->>System: Event Published
    System-->>Organizer: Event Live
    
    Note over Organizer,Analytics: Event Active Period
    
    System->>Reg: Process Registrations
    Reg-->>System: Registration Updates
    
    Organizer->>System: View Registrations
    System->>Reg: Get Event Registrations
    Reg-->>System: Registration List
    System-->>Organizer: Registration Dashboard
    
    Organizer->>System: View Analytics
    System->>Analytics: Generate Reports
    Analytics-->>System: Analytics Data
    System-->>Organizer: Event Analytics
    
    Note over Organizer,Analytics: Event Day
    Organizer->>System: Check-in Attendees
    System->>Reg: Verify QR Codes
    Reg-->>System: Check-in Status
    System-->>Organizer: Check-in Results
```

---

## 6. System Integration Overview

```mermaid
sequenceDiagram
    participant Client as Client<br/>Applications
    participant API as REST API<br/>Gateway
    participant Service as Event Management<br/>Service
    participant DB as PostgreSQL<br/>Database
    participant Cache as Redis<br/>Cache
    participant MQ as RabbitMQ<br/>Message Queue
    participant Email as Email<br/>Service
    participant SMS as SMS<br/>Service
    participant Payment as Payment<br/>Gateway

    Note over Client,Payment: System Integration Architecture

    Client->>API: REST API Calls
    API->>Service: Route Requests
    
    Service->>Cache: Read/Write Cache
    Cache-->>Service: Cache Operations
    
    Service->>DB: Data Persistence
    DB-->>Service: Data Operations
    
    Service->>MQ: Publish Events
    MQ->>Email: Notification Events
    MQ->>SMS: Notification Events
    Email-->>MQ: Processed
    SMS-->>MQ: Processed
    
    Service->>Payment: Payment Processing
    Payment-->>Service: Payment Status
    
    Service-->>API: Response Data
    API-->>Client: HTTP Responses
```

---

## Key Components

### API Layer (Controllers)
- **AuthController**: User authentication and registration
- **EventController**: Event CRUD operations
- **TicketController**: Ticket management
- **RegistrationController**: Event registration
- **PaymentController**: Payment processing
- **VenueController**: Venue management
- **CategoryController**: Category management
- **UserController**: User management

### Business Logic Layer (Services)
- **AuthService**: Authentication and authorization
- **EventService**: Event business logic
- **TicketService**: Ticket management logic
- **RegistrationService**: Registration processing
- **PaymentService**: Payment processing
- **QRCodeService**: QR code generation
- **NotificationService**: Async notifications
- **VenueService**: Venue management
- **CategoryService**: Category management
- **UserService**: User management

### Data Layer
- **Repositories**: JPA repositories for data access
- **Entities**: Domain model entities
- **PostgreSQL**: Primary relational database
- **Redis**: Caching layer for performance

### External Integrations
- **RabbitMQ**: Message queue for async processing
- **Email Service**: Email notifications (via MQ)
- **SMS Service**: SMS notifications (via MQ)
- **Payment Gateway**: Payment processing (future integration)

---

## Notes

- **High-level diagrams** show major system interactions and flows
- **Detailed diagrams** are available in `SEQUENCE_DIAGRAMS.md`
- All diagrams use **Mermaid** syntax
- Diagrams can be rendered in GitHub, GitLab, VS Code, and documentation tools



