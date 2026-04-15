# Modularization Strategy

## Module Dependency Rules

1. **Feature modules** depend on `:core:*` modules but NEVER on each other
2. **`:core:domain`** is pure Kotlin + Compose runtime (no Android framework dependencies)
3. **`:core:database`** depends on `:core:domain` and `:core:network` (maps network responses to entities)
4. **`:core:network`** depends only on `:core:domain`
5. **`:core:designsystem`** depends only on `:core:domain` (for model types in component params)
6. **`:core:sync`** depends on `:core:domain`, `:core:database`, `:core:network`, `:core:logging`
7. **`:core:testing`** depends only on `:core:domain`
8. **`:app`** depends on all feature and core modules

## Module Categories

| Category | Modules | Purpose |
|----------|---------|---------|
| App | `:app` | Entry point, navigation, DI wiring |
| Feature | `:feature:facts`, `:feature:favorites`, `:feature:factdetail` | Screen-specific UI and ViewModels |
| Core | `:core:domain`, `:core:database`, `:core:network`, `:core:designsystem`, `:core:sync`, `:core:logging`, `:core:telemetry`, `:core:testing` | Shared infrastructure |
| Test | `:benchmark` | Performance testing |

## Build Convention Plugins

- `catfact.android.library`: Base Android library config (compileSdk, minSdk, flavors, JaCoCo, packaging)
- `catfact.android.feature`: Extends library with Compose, Hilt, KSP, and serialization
