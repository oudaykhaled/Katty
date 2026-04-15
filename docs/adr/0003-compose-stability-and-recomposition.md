# ADR 0003: Compose Stability and Recomposition Control

## Status
Accepted

## Context
Jetpack Compose skips recomposition of composables whose parameters haven't changed, but only if those parameters are "stable." Unstable parameters cause unnecessary recompositions, degrading performance.

## Decision
- Annotate domain models with `@Immutable` and enums with `@Stable`
- Use `ImmutableList` from kotlinx-collections-immutable in UI state
- Configure the Compose compiler with `compose_compiler_config.conf` listing all stable domain types
- Use `derivedStateOf` for computed values (e.g., scroll-to-top FAB visibility)
- Use `snapshotFlow` for side-effecting observation of Compose state (e.g., pagination threshold)
- Separate stateful Route composables from stateless Screen composables

## Consequences
- Compose compiler can skip recomposition for fact cards whose data hasn't changed
- `LazyColumn` with `key` and `contentType` enables efficient ViewHolder pooling
- Route/Screen separation makes screens preview-friendly and independently testable
- `derivedStateOf` prevents unnecessary recomposition on every scroll event
