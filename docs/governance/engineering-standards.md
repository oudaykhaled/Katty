# Engineering Standards

## Code Quality
- **Detekt**: Zero tolerance (`maxIssues: 0`), Composable function naming exempted
- **Lint**: All modules run lint with `--continue`
- **Max line length**: 120 characters
- **No wildcard imports**
- **Magic numbers**: Excluded in test files only

## Testing Requirements
- Every use case has at least one unit test
- Every ViewModel has Turbine-based flow tests
- Fakes over mocks: `FakeCatFactRepository` in `:core:testing`
- `StandardTestDispatcher` for deterministic coroutine testing
- MockWebServer for interceptor tests
- Instrumentation tests verify Hilt DI graph, navigation flows, and accessibility

## Architecture
- MVI with sealed events, `@Immutable` state, Channel side effects
- Offline-first: Room as SSOT, WorkManager sync
- Dependency inversion: Interfaces in `:core:domain`, implementations in data modules
- Convention plugins eliminate Gradle duplication

## CI/CD
- 7-stage pipeline: Lint → Detekt → Unit Tests → Instrumented Tests → Coverage → Build → Maestro E2E
- JaCoCo coverage aggregation across all modules
- APK size reporting in build step
