# ADR 0004: MVI Architecture with Side Effect Delivery

## Status
Accepted

## Context
We need a predictable, testable presentation layer that separates UI state from one-shot effects (navigation, snackbar messages).

## Decision
- Use sealed interface events dispatched to ViewModel via `onEvent()`
- Expose `@Immutable` UI state via `StateFlow`
- Deliver one-shot side effects via `Channel<SideEffect>` consumed with `receiveAsFlow()`
- Collect state with `collectAsStateWithLifecycle` for lifecycle-aware observation
- Use `rememberUpdatedState` for lambda captures in LaunchedEffect blocks

## Consequences
- Unidirectional data flow: Event → ViewModel → State/Effect → UI
- Channel ensures each side effect is consumed exactly once
- State survives configuration changes via ViewModel
- Turbine-based testing can assert on both state emissions and side effects
