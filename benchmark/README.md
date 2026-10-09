# Macrobenchmark harness

The benchmark module contains startup, scrolling, interaction, navigation, and idle scenarios. The app's benchmark target inherits the release build and is profileable, non-debuggable, and signed with the local debug key for installation. The separate instrumentation driver is not the measured app.

```sh
# Compile the target and test APKs
./gradlew :app:assembleDevBenchmarkRelease :benchmark:assembleDevBenchmarkRelease

# Measure on an authorized physical device
./gradlew :benchmark:connectedDevBenchmarkReleaseAndroidTest
```

Exploratory emulator runs require `-PbenchmarkAllowEmulator=true`. This suppresses only the emulator warning; target debuggability is never suppressed. Emulator timing is not evidence of physical-device performance.

Raw output is generated under `benchmark/build/outputs/connected_android_test_additional_output`. Record the device model, API level, build revision, compilation mode, iterations, and JSON/Perfetto output when publishing a result. The previous README's debug-build numbers have been removed from current claims because they are not measurements of this updated release target.

The authored `app/src/main/baseline-prof.txt` is retained. Stable Baseline Profile plugin integration provides `./gradlew :app:generateDevReleaseBaselineProfile`; successful collection writes a variant profile. Automatic generation is disabled for ordinary builds. The generator's stale AGP compatibility ignore was removed after updating to the compatible plugin; its executed status is recorded in the verification notes.

Two benchmark scenarios retain historical `@Ignore` annotations, explained at their source. They are not counted as executed checks. The remaining scenarios also require a populated cache or live network; compilation alone does not establish that the journeys pass.

See [verification evidence](../docs/verification.md) for what was actually run during this quality pass.
