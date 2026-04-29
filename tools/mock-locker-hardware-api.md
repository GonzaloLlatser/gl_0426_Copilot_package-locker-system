# Mock Tool - Locker Hardware API

## Purpose
Define a mock external API that simulates smart locker hardware interactions for development and testing.

This tool is a contract-only artifact in the current phase.
No real hardware integration is implemented yet.

## Base Path (Proposed)
/mock/lockers

## Endpoints

### POST /mock/lockers/{lockerId}/open
Purpose: simulate opening locker door.

Response example:
{
  "lockerId": "L-101",
  "action": "OPEN",
  "result": "SUCCESS",
  "timestamp": "2026-04-29T10:00:00Z"
}

Possible error results:
- LOCKER_NOT_FOUND
- LOCKER_NOT_AVAILABLE
- HARDWARE_TIMEOUT

### POST /mock/lockers/{lockerId}/close
Purpose: simulate closing locker door.

Response example:
{
  "lockerId": "L-101",
  "action": "CLOSE",
  "result": "SUCCESS",
  "timestamp": "2026-04-29T10:00:05Z"
}

Possible error results:
- LOCKER_NOT_FOUND
- DOOR_OBSTRUCTION
- HARDWARE_TIMEOUT

### GET /mock/lockers/{lockerId}/status
Purpose: retrieve locker hardware status.

Response example:
{
  "lockerId": "L-101",
  "door": "CLOSED",
  "connectivity": "ONLINE",
  "lastAction": "CLOSE",
  "timestamp": "2026-04-29T10:00:06Z"
}

Possible status values:
- door: OPEN, CLOSED, UNKNOWN
- connectivity: ONLINE, OFFLINE, DEGRADED

## Integration Guidelines
- Backend should treat this API as external and potentially unreliable.
- Failures should produce controlled business-level responses.
- Retries, circuit breaker, and timeout policies are future enhancements (not MVP).

## MVP Usage
- Optional during pickup/deposit flows to simulate open/close.
- Must not block core business state updates in local demo mode unless explicitly configured.
