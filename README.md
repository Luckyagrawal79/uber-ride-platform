# Uber Ride Platform — Microservices Architecture

A production-grade, event-driven ride-hailing backend built with **Java 21**, **Spring Boot 3.3**, **Apache Kafka**, and polyglot persistence.


mvn clean install -DskipTests
docker-compose up -d --build


http://localhost:8081/swagger-ui.html   (auth-service)

http://localhost:8082/swagger-ui.html   (user-service)

http://localhost:8083/swagger-ui.html   (driver-service)

http://localhost:8084/swagger-ui.html   (ride-service)

http://localhost:8085/swagger-ui.html   (payment-service)

http://localhost:8086/swagger-ui.html   (notification-service)


## Project Structure


uber-microservices

├── common-dto          # Shared enums, DTOs, Kafka events

├── eureka-server        # Service discovery

├── api-gateway          # JWT validation + request routing

├── auth-service         # Registration, login, JWT tokens

├── user-service         # Passenger profiles, favorite routes

├── driver-service       # Driver management, location, matching

├── ride-service         # Ride lifecycle, pricing, reviews, reports

├── payment-service      # Payment processing (multi-strategy)

├── notification-service # Multi-channel notifications (MongoDB)

├── docker-compose.yml   # Infrastructure (Kafka, PG, Mongo, Redis)

└── init-databases.sql   # PostgreSQL database initialization


### 1. Build the Project

mvn clean install -DskipTests

Start Services (in order)

# Terminal 1: Service Discovery
cd eureka-server && mvn spring-boot:run

# Terminal 2: API Gateway
cd api-gateway && mvn spring-boot:run

# Terminal 3-8: Business Services (any order after Eureka is up)

cd auth-service && mvn spring-boot:run

cd user-service && mvn spring-boot:run

cd driver-service && mvn spring-boot:run

cd ride-service && mvn spring-boot:run

cd payment-service && mvn spring-boot:run

cd notification-service && mvn spring-boot:run


### Get Ride Estimate (WithOut logging)
"http://localhost:8080/api/rides/estimate?depLat=12.97&depLng=77.59&destLat=12.93&destLng=77.62&type=STANDARD"


## Default Admin Account
- Email: `admin@uber.com`
- Password: `admin123`

## Event Flow Example: Complete Ride

1. Passenger calls `POST /api/rides` → **Ride Service** creates ride (PENDING), publishes `ride-requested`
2. **Driver Service** consumes `ride-requested` → finds best driver (Strategy Pattern), publishes `driver-assigned`
3. **Ride Service** consumes `driver-assigned` → updates ride with driver info
4. **Notification Service** consumes `ride-status-changed` → sends "Driver assigned" notification
5. Driver calls `PUT /api/rides/{id}/start` → **Ride Service** changes state (State Pattern) to STARTED
6. Driver calls `PUT /api/rides/{id}/finish` → **Ride Service** changes to FINISHED, publishes `payment-requested`
7. **Payment Service** consumes `payment-requested` → processes via Strategy Pattern, publishes `payment-completed`
8. **Notification Service** consumes `payment-completed` → sends receipt via Factory Pattern
