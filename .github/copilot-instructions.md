# Copilot Instructions for package-locker-system

## Project Intent
Backend-first learning project for a smart package locker API built with Spec-Driven Development.

## Hard Constraints
- Do not implement beyond MVP scope defined in SPEC.md.
- Prefer documentation-first and tests-first behavior.
- Keep domain logic deterministic and reproducible.
- Keep frontend minimal (HTML/CSS/vanilla JS only).

## Required Context Loading Before Code Generation
Before generating code, always read:
1. SPEC.md
2. ARCHITECTURE.md
3. AGENTS.md
4. .github/skills/assign-locker.md
5. tools/mock-locker-hardware-api.md

If any file is missing or contradictory, ask for clarification before generating significant code.

## Implementation Priorities
1. Domain correctness over framework complexity.
2. Deterministic locker assignment over smart heuristics.
3. Testability over premature optimization.
4. Clear API contracts over implicit behavior.

## Backend Guardrails
- Java 21 + Spring Boot 3 stack only.
- Validate input with Bean Validation.
- Use explicit package and delivery states.
- Keep transitions strict (cannot pick up twice, cannot deposit invalid assignment).
- Document API via Swagger/OpenAPI.

## Frontend Guardrails
- No frameworks.
- Keep one simple page with form, submit button, and response panel.
- Use fetch API only.
- No advanced state management or routing.

## Done Criteria for Agent-Generated Work
- Matches acceptance criteria from SPEC.md.
- Respects architecture boundaries from ARCHITECTURE.md.
- Includes or updates relevant tests.
- Leaves out future-improvement features.
