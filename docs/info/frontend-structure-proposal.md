# Frontend Structure Proposal (Demo-Only)

## Purpose
Provide a minimal UI for demonstrating backend API behavior.

## Scope
- One page only.
- No framework.
- No routing.
- No complex client-side business logic.

## Proposed File Structure
frontend/
- index.html
- styles.css
- app.js

## Page Elements
- Customer name text input.
- Package size select (S, M, L).
- Submit button for create-delivery request.
- Response panel for JSON output and errors.

## Interaction Flow
1. User enters customer name.
2. User selects package size.
3. User clicks submit.
4. app.js sends POST request via fetch.
5. UI prints formatted response or error.

## Responsiveness
- Mobile-first layout with one-column form.
- Width-constrained container for desktop readability.
- Minimal spacing and typography rules.

## Non-Goals
- Authentication.
- Dashboard widgets.
- Client-side state store.
- Historical tables/charts.
