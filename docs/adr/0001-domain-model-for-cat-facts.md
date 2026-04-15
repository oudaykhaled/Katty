# ADR 0001: Domain Model for Cat Facts

## Status
Accepted

## Context
We need a domain model that represents cat facts with support for bookmarking, personal notes, sync status tracking, and length-based categorization. The model must be Compose-stable to minimize unnecessary recompositions.

## Decision
- Create an `@Immutable` data class `CatFact` with copy-on-write helper methods (`withBookmark()`, `withNote()`, `withSyncStatus()`)
- Use `FactCategory` enum derived from fact length (Short ≤50, Medium ≤120, Long >120)
- Use `SyncStatus` enum (SYNCED, PENDING, FAILED) as a state machine
- Use `ErrorKind` sealed interface for typed error handling

## Consequences
- Domain models are pure Kotlin with Compose runtime dependency only for stability annotations
- Copy-on-write helpers prevent accidental mutation and make transformations explicit
- Enum-based categorization is deterministic and testable
- Sealed error types enable exhaustive `when` expressions
