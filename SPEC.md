# SPEC - Package Locker System

## Problem Statement
Parcel delivery operations in smart locker networks need a small, deterministic backend service to track deliveries, assign lockers, and validate secure pickup. Many pilot implementations fail because scope is too broad early on. This project defines a tight MVP using Spec-Driven Development so implementation remains predictable and testable.

## Target Users
- Courier Operator: creates package deliveries and confirms package deposit.
- Customer: picks up package using a PIN.
- Operations/Admin (MVP observer only): reviews state through API/Swagger during pilot.

## MVP Scope
MVP includes only these core use cases:
1. Create package delivery.
2. Automatically assign the best available locker.
3. Confirm package deposit.
4. Pick up package using PIN.

Out of scope for MVP:
- Scheduled package expiration.
- Locker decommissioning.
- Admin recovery flows.
- Failed PIN lockout thresholds.
- Advanced locker management.
- Advanced frontend dashboard.
- Notifications.
- Analytics.

## Main Domain Entities
- Locker
- Package
- Customer
- PickupCode

## Use Cases

### UC1 - Create Package Delivery
Trigger: Courier requests a new delivery for a customer and package size.

Expected result:
- Delivery is created in "CREATED" state.
- Locker assignment is attempted deterministically.
- If assignment succeeds, locker and pickup PIN are associated with delivery.

### UC2 - Auto-Assign Locker
Trigger: Assignment logic is called during delivery creation.

Expected result:
- Only available and unoccupied lockers are considered.
- Package size must fit locker size.
- The smallest locker that fits is selected.
- Tie-breaker is deterministic (for example, ascending locker ID).

### UC3 - Confirm Package Deposit
Trigger: Courier confirms package has been placed into assigned locker.

Expected result:
- Delivery transitions from "CREATED" (or "ASSIGNED") to "DEPOSITED".
- Locker becomes occupied by that active package.

### UC4 - Pickup with PIN
Trigger: Customer submits delivery reference and PIN.

Expected result:
- PIN is validated.
- If valid and package not already picked up, delivery becomes "PICKED_UP".
- Locker is released (available/unoccupied).

## Business Rules
1. A package can only be assigned to one locker.
2. A locker can contain only one active package.
3. Package size must fit locker size.
4. Pickup PIN must be validated before pickup.
5. Picked-up packages cannot be picked up again.
6. Locker assignment must be deterministic and choose the smallest available fitting locker.

## Acceptance Criteria
- Given valid customer and package size, when delivery is created, then a deterministic locker assignment result is returned.
- Given no suitable available locker, when delivery is created, then response indicates assignment failure with clear reason.
- Given assigned delivery, when deposit is confirmed, then state becomes "DEPOSITED".
- Given deposited delivery and correct PIN, when pickup is requested, then state becomes "PICKED_UP" and locker is freed.
- Given wrong PIN, when pickup is requested, then pickup is denied and state is unchanged.
- Given already picked-up delivery, when pickup is requested again, then request is rejected.

## Edge Cases
- No lockers registered.
- Lockers exist but all are occupied.
- Lockers available but all too small.
- Duplicate delivery confirmation request.
- Pickup attempt before deposit confirmation.
- Empty or invalid customer name.
- Invalid package size value.

## API Scope (Conceptual)
- POST delivery creation endpoint.
- POST deposit confirmation endpoint.
- POST pickup endpoint with PIN.
- Optional GET endpoint for delivery/locker lookup for demo visibility.

Concrete endpoint paths and payload schemas are defined in ARCHITECTURE.md and refined before coding.

## Non-Functional Constraints
- Keep implementation intentionally small and easy to reason about.
- Prefer clear deterministic logic over optimization.
- Ensure API is testable with JUnit 5 + Mockito.
- Keep persistence simple with H2 for local development.

## Frontend Purpose (MVP Demo)
A very simple frontend will be used only to:
- Enter customer name.
- Select package size.
- Submit create-delivery request via fetch.
- Visualize API response.

No frontend business logic beyond calling backend and rendering response.

## Definition of Done for Planning Phase
- Documentation files complete.
- Repository structure scaffolded.
- Skill and mock tool contracts documented.
- Roadmap prepared.
- No production Java controllers/services/entities implemented yet.
