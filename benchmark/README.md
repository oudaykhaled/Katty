# Macrobenchmark Module (`:benchmark`)

Comprehensive macrobenchmark suite for the CatFact app covering startup, scroll, interaction, navigation, and idle-recomposition scenarios.

## Running Benchmarks

Requires a connected physical device or emulator.

```bash
# Full benchmark suite
./gradlew :benchmark:connectedDevBenchmarkAndroidTest

# Build only (verify compilation)
./gradlew :benchmark:assembleDevBenchmark :app:assembleDevBenchmark
```

> **Note:** `testInstrumentationRunnerArguments` includes `EMULATOR,DEBUGGABLE` suppression so benchmarks run on emulators and debuggable builds, but **results from physical devices are more representative**.

## Test Matrix

### Startup (6 tests)

| Test | StartupMode | CompilationMode | Metric | Budget |
|------|-------------|-----------------|--------|--------|
| `coldStartup` | COLD | DEFAULT | StartupTimingMetric | < 1500 ms |
| `warmStartup` | WARM | DEFAULT | StartupTimingMetric | < 500 ms |
| `hotStartup` | HOT | DEFAULT | StartupTimingMetric | — |
| `coldStartupNoCompilation` | COLD | None | StartupTimingMetric | — |
| `coldStartupPartialCompilation` | COLD | Partial (Require) | StartupTimingMetric | — |
| `coldStartupFullCompilation` | COLD | Full | StartupTimingMetric | — |

The compilation comparison tests quantify the value of baseline profiles by showing startup latency across different dex optimization levels.

### Scroll (2 tests)

| Test | Description | Metric | Budget |
|------|-------------|--------|--------|
| `scrollFactsList` | 5x DOWN + 3x UP flings | FrameTimingMetric | P50 < 8ms, P90 < 16ms, P99 < 32ms |
| `scrollAfterDataLoads` | Wait 2s then scroll | FrameTimingMetric | P50 < 8ms, P90 < 16ms, P99 < 32ms |

### Interactions (3 tests)

| Test | Description | Metric |
|------|-------------|--------|
| `bookmarkToggle` | Toggle bookmark 3x on first card | FrameTimingMetric |
| `pullToRefresh` | Pull-to-refresh gesture | FrameTimingMetric |
| `searchInteraction` | Type query, wait, clear | FrameTimingMetric |

### Navigation (2 tests)

| Test | Description | Metric |
|------|-------------|--------|
| `tabSwitching` | Facts → Favorites → Facts (2 cycles) | FrameTimingMetric |
| `navigateToDetailAndBack` | Click card → detail → back | FrameTimingMetric |

### Idle (1 test)

| Test | Description | Metric |
|------|-------------|--------|
| `idleFrameStability` | Load content, idle 5s | FrameTimingMetric |

## Output Locations

After running benchmarks:

```
benchmark/build/outputs/
  connected_android_test_additional_output/
    devBenchmark/connected/<device>/
      ├── FactsBenchmark_*.perfetto-trace    # Perfetto traces
      └── *-benchmarkData.json               # Macrobenchmark JSON results
  androidTest-results/
    connected/benchmark/flavors/dev/
      └── TEST-*.xml                         # JUnit XML results
```

## Performance Budgets

Defined in `docs/performance/performance-budgets.md`:

| Metric | Budget |
|--------|--------|
| Warm startup | < 500 ms |
| Cold startup | < 1500 ms |
| P50 frame duration | < 8 ms |
| P90 frame duration | < 16 ms |
| P99 frame duration | < 32 ms |

## Perfetto Trace Analysis

Open `.perfetto-trace` files in [Perfetto UI](https://ui.perfetto.dev/) and look for:

- **Choreographer#doFrame** slices > 16 ms to spot jank
- **Compose:recompose** depth to verify stability config effectiveness
- **measure / layout** slices during scroll and tab transitions
- **reportFullyDrawn** duration for startup timing

## Baseline Profiles

`BaselineProfileGenerator.kt` covers all critical user paths but is `@Ignore`d due to AGP 9 compatibility issues with the `androidx.baselineprofile` producer plugin. The generator is ready to re-enable once the plugin ships AGP 9 support.

The app includes `ProfileInstaller` so baseline profiles will be applied at install time once generated.

## Known Limitations

- **Emulator suppression:** `EMULATOR,DEBUGGABLE` errors are suppressed. Results on emulators are less reliable than physical devices.
- **Baseline profiles:** Generation disabled pending AGP 9 plugin compatibility.
- **Network dependency:** Some benchmarks (pull-to-refresh, search) depend on cached data or network availability. Run on a device with connectivity for full coverage.
