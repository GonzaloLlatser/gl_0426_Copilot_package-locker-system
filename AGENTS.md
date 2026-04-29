# AGENTS - Working Agreement for AI Coding Agents

## Purpose
Define how AI coding agents must execute Spec-Driven Development for this repository.

## Mandatory Read Order Before Writing Code
1. SPEC.md
2. ARCHITECTURE.md
3. AGENTS.md
4. .github/copilot-instructions.md
5. .github/skills/assign-locker.md
6. tools/mock-locker-hardware-api.md

If a generated code proposal conflicts with these files, the agent must stop and align with the documentation.

## Agent Workflow Rules
1. Parse MVP scope from SPEC.md first.
2. Refuse to implement out-of-scope features unless explicitly requested in a future phase.
3. Use deterministic locker assignment skill for any assignment logic.
4. Keep implementation small and testable.
5. Generate tests for core use cases before or alongside code.
6. Keep frontend intentionally simple and demo-only.
7. Update docs when assumptions change.

## Coding Conventions
- Java 21 and Spring Boot 3 APIs only.
- Use clear package-by-feature or package-by-layer structure consistently.
- Use Bean Validation for request validation.
- Keep methods short and explicit.
- Prefer immutable DTOs where practical.
- Add OpenAPI annotations only where they improve API clarity.
- Use meaningful names for states and domain actions.

## Testing Conventions
- Unit tests for assignment logic and pickup PIN validation.
- Service-layer tests for use-case transitions.
- Lightweight integration tests for API happy paths.
- Deterministic tests: avoid randomness in locker selection.

## Definition of In-Scope Code for MVP
Allowed:
- Create delivery.
- Deterministic locker assignment.
- Confirm deposit.
- Pickup using PIN.

Not allowed in MVP implementation:
- Expiration scheduler.
- Decommissioning workflow.
- Lockout thresholds.
- Notifications/analytics.
- Advanced dashboard features.

## Agent Decision Checklist Before Any PR
- Is this feature in SPEC.md MVP scope?
- Does it follow ARCHITECTURE.md component boundaries?
- Does locker assignment use deterministic smallest-fit logic?
- Are acceptance criteria covered by tests?
- Are docs updated if behavior changed?
