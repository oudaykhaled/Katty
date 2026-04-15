# ADR 0005: Navigation 3 with Type-Safe Routes

## Status
Accepted

## Context
Android's Navigation 3 library provides type-safe routing via `@Serializable` data classes implementing `NavKey`. This eliminates string-based route arguments that are error-prone.

## Decision
- Define routes as `@Serializable` objects/data classes inside `AppRoute` implementing `NavKey`
- Use `rememberNavBackStack` for back stack management
- Use `NavDisplay` with `entryProvider` for type-safe route-to-screen mapping
- Support deep links via `catfact://fact/{id}` scheme parsed in `MainActivity`
- Add ProGuard keep rules for serialized route classes

## Consequences
- Compile-time type safety for all navigation arguments
- Deep links are handled at the activity level and pushed onto the nav back stack
- ProGuard rules prevent R8 from stripping serialized route metadata
- Navigation state survives process death via `rememberSaveable`
