# ARCHITECTURE - Package Locker System

## Goal
Define a backend-first, MVP-sized architecture for smart package locker operations using Spec-Driven Development.

## Technology Stack
Backend:
- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- H2 Database
- Bean Validation
- Swagger/OpenAPI
- JUnit 5
- Mockito

Frontend:
- HTML5
- CSS3
- Vanilla JavaScript
- Fetch API

## Architectural Style
- Modular monolith (single Spring Boot application).
- Layered architecture:
  - API layer (controllers).
  - Application layer (use-case services).
  - Domain layer (entities, value objects, domain rules).
  - Infrastructure layer (repositories, persistence adapters, mock hardware adapter).

## Main Components
- Delivery API: receives create/deposit/pickup requests.
- Locker Assignment Service: deterministic locker selection algorithm.
- Pickup Validation Service: validates PIN and package state.
- Locker Repository: reads/writes locker occupancy and size metadata.
- Delivery Repository: tracks package lifecycle state.
- Mock Locker Hardware Client: simulates open/close/status operations.
- Demo Frontend (static): sends API calls and renders raw responses.

## Data Model (Conceptual)
- Locker
  - id
  - code
  - size (S, M, L)
  - status (AVAILABLE, OCCUPIED, OUT_OF_SERVICE)
- Package
  - id
  - deliveryId
  - size (S, M, L)
  - status (CREATED, ASSIGNED, DEPOSITED, PICKED_UP)
- Customer
  - id
  - name
- PickupCode
  - id
  - deliveryId
  - pin
  - active

## Deterministic Assignment Strategy
1. Validate package size value.
2. Query lockers where status is AVAILABLE.
3. Exclude lockers with active package occupancy.
4. Filter lockers where locker.size >= package.size.
5. Sort by locker size ascending, then locker ID ascending.
6. Select first locker.
7. If none found, return explicit no-locker-available result.

## API Draft (MVP)
- POST /api/deliveries
  - Purpose: create delivery and auto-assign locker.
- POST /api/deliveries/{deliveryId}/deposit
  - Purpose: confirm courier deposit.
- POST /api/deliveries/{deliveryId}/pickup
  - Purpose: pickup by PIN.
- GET /api/deliveries/{deliveryId}
  - Purpose: retrieve delivery state for demo/testing.

Note: Final request/response schemas will be generated from SPEC.md before implementation begins.

## Data Flow
1. Frontend submits create-delivery request.
2. Delivery API validates request.
3. Assignment service selects locker deterministically.
4. System creates delivery + pickup code transactionally.
5. Courier confirms deposit.
6. Customer submits PIN for pickup.
7. Pickup service validates PIN, updates delivery state, frees locker, optionally calls mock hardware API.

## Trade-Offs
- H2 chosen for speed and simplicity over production realism.
- Modular monolith chosen for course clarity over microservices.
- Deterministic assignment chosen over dynamic optimization for predictable tests.
- Simple frontend chosen for demoability, not UX depth.

## Basic Frontend Architecture and Purpose
- Single static page served separately or from static resources.
- Components:
  - Form: customer name + package size selector.
  - Action: submit button to trigger POST /api/deliveries.
  - Result panel: shows formatted JSON response or error.
- No framework, no routing, no state manager.

## Build and Test Commands (Planned)
- Build: ./mvnw clean verify
- Run tests: ./mvnw test
- Run app: ./mvnw spring-boot:run

(Equivalent Maven commands without wrapper may also be documented once project files are generated.)

## Implementation Boundaries for Current Phase
Current phase delivers only design artifacts and project structure.
No Java controllers, services, repositories, entities, or frontend logic should be implemented yet.
