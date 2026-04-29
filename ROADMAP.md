# Implementation Roadmap

## Objective
Deliver a small, deterministic MVP for package lockers using Spec-Driven Development.

## Phase 0 - Documentation and Scaffolding (Current)
Status: Completed in this phase.

Deliverables:
- README.md
- SPEC.md
- ARCHITECTURE.md
- AGENTS.md
- .github/copilot-instructions.md
- .github/skills/assign-locker.md
- tools/mock-locker-hardware-api.md
- frontend structure proposal

## Phase 1 - Backend Skeleton
- Initialize Maven Spring Boot project (Java 21, Spring Boot 3).
- Configure dependencies: web, data-jpa, validation, h2, openapi, test.
- Create package structure based on ARCHITECTURE.md.
- Add baseline health endpoint and OpenAPI setup.

## Phase 2 - Domain and Persistence
- Model Locker, Package, Customer, PickupCode.
- Define enums for size and status.
- Create repositories and persistence mappings.
- Seed sample locker data for local testing.

## Phase 3 - Core Use Cases (MVP)
- UC1 Create package delivery.
- UC2 Deterministic locker assignment.
- UC3 Confirm package deposit.
- UC4 Pickup with PIN.

## Phase 4 - API Layer
- Add REST endpoints for create, deposit, pickup, and get delivery.
- Apply Bean Validation on request models.
- Add error handling for business errors (no locker available, invalid pin, invalid state).

## Phase 5 - Testing
- Unit tests for assignment and PIN validation.
- Service tests for state transitions.
- Integration tests for happy path and key failure cases.

## Phase 6 - Minimal Demo Frontend
- Add static HTML/CSS/JS page.
- Include customer name field, package size selector, submit button.
- Add fetch call to create delivery endpoint.
- Render JSON response/error in a simple panel.

## Future Phases (Not MVP)
- Scheduled package expiration.
- Locker decommissioning.
- Admin recovery workflows.
- Failed PIN lockout thresholds.
- Notifications and analytics.
- Advanced frontend dashboard.

## Exit Criteria for MVP
- All 4 MVP use cases pass acceptance criteria in SPEC.md.
- Deterministic assignment proven by tests.
- API documented and runnable locally.
- Frontend demo can create delivery and show response.
