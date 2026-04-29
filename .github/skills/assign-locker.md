# Skill: Assign Locker Deterministically

## Purpose
Reusable decision skill for assigning the most appropriate locker during delivery creation.

## Inputs
- packageSize: one of S, M, L
- lockers: collection with at least
  - lockerId
  - size (S, M, L)
  - status (AVAILABLE, OCCUPIED, OUT_OF_SERVICE)
  - hasActivePackage (boolean)

## Output
Deterministic assignment result:
- success: true/false
- lockerId: selected locker ID when success=true
- reason: explicit message when success=false

## Steps
1. Validate package size.
2. Filter lockers by status AVAILABLE.
3. Exclude occupied lockers and lockers with hasActivePackage=true.
4. Keep only lockers whose size can fit package size.
5. Sort candidates by locker size ascending.
6. Apply deterministic tie-breaker (lockerId ascending).
7. Select first candidate.
8. Return structured result.

## Size Fit Rules
- S package fits S, M, L.
- M package fits M, L.
- L package fits L only.

## Determinism Rule
For the same input dataset, output must always choose the same locker.
No randomness, no time-based influence, and no unstable iteration order.

## No Locker Available Behavior
If no candidate exists:
- success=false
- lockerId=null
- reason="NO_AVAILABLE_LOCKER"

The caller should surface this as a clear business outcome, not as an internal server error.

## Validation Failures
If package size is invalid:
- success=false
- lockerId=null
- reason="INVALID_PACKAGE_SIZE"
