# Dependency and toolchain policy

Verified against official release notes and repository metadata on 9 October 2026.
The version catalog pins direct dependencies; the wrapper pins the Gradle archive SHA-256.

## Build matrix

| Component | Selected version | Reason |
| --- | --- | --- |
| Android Gradle Plugin | 9.3.3 | Stable 9.3 patch fixes D8/R8 regressions; Kotlin's documented compatibility matrix covers the 9.3 family. |
| Gradle | 9.5.0 | Required minimum for AGP 9.3; distribution SHA-256 verified. |
| Java | 17 | AGP's supported minimum, aligned for the daemon, compiler and CI. |
| Kotlin / Compose compiler / serialization plugin | 2.4.21 | Same compiler version across plugins. |
| KSP | 2.3.12 | Current stable KSP2 release; verified with Room and Hilt generation. |
| Compose BOM | 2026.09.00 | Stable Compose component alignment. |
| compileSdk / targetSdk / minSdk | 37 / 36 / 24 | Core and Navigation 3 require compile SDK 37; target behavior remains at 36. |

Install `platforms;android-37.0` and `build-tools;36.0.0` with the Android SDK manager.
The Gradle daemon criterion requires Java 17 without pinning a machine-specific download URL.
Android modules use AGP's built-in Kotlin support rather than applying the legacy Kotlin Android plugin.

AGP 9.4.1 is newer. The project retains the stable 9.3 patch line while Kotlin's fully supported
AGP compatibility table ends at 9.3.1. The later 9.3 patches contain fixes within that minor line;
moving to 9.4 will be a separate upgrade after the compatibility range expands.

## Runtime and test alignment

| Family | Version |
| --- | --- |
| Lifecycle, including Navigation 3 ViewModel ownership | 2.11.0 |
| Navigation 3 | 1.2.0 |
| Core / Activity | 1.19.1 / 1.13.0 |
| Dagger Hilt / AndroidX Hilt | 2.60.1 / 1.4.0 |
| Room / WorkManager | 2.8.5 / 2.12.0 |
| Retrofit / OkHttp and MockWebServer | 3.0.0 / 5.5.0 |
| Coroutines runtime and tests | 1.11.0 |
| Serialization runtime / immutable collections | 1.11.0 / 0.5.2 |
| Robolectric / MockK / Turbine | 4.17 / 1.14.11 / 1.2.1 |
| AndroidX test core / JUnit extension / Espresso | 1.7.0 / 1.3.0 / 3.7.0 |
| Benchmark / UI Automator / ProfileInstaller | 1.5.0 / 2.4.0 / 1.4.1 |
| JaCoCo / Detekt | 0.8.15 / 1.23.8 |

All selected versions are stable releases. The prior Lifecycle Navigation 3 alpha is removed.
Runtime/test families share version references where they must align. MockWebServer retains the
compatible `mockwebserver` API during this upgrade; migration to `mockwebserver3` is independent work.

## Upgrade procedure

1. Check official Maven metadata, release notes, Kotlin's AGP/Gradle compatibility table and AAR metadata.
2. Upgrade coupled compiler and runtime/test families together. Avoid alpha, beta and release candidate versions.
3. Run `./gradlew :app:assembleDevDebug allJvmTests lintDevDebug verifyCoreCoverage detekt`.
4. Run instrumented tests on an emulator or connected device with `./gradlew allInstrumentedTests`.
5. Treat benchmark results as measurements only from a physical device with the non-debuggable benchmark app.

An exploratory emulator benchmark requires `-PbenchmarkAllowEmulator=true`. This suppresses only
the emulator warning. The target app remains non-debuggable and profileable.

## Coverage collection

`verifyCoreCoverage` enforces at least 80% line and 70% branch coverage across `core:domain`
and `core:database`. `jacocoCriticalReport` writes XML and HTML to
`build/reports/jacoco/critical/`. Generated Room and dependency injection implementations
are excluded; authored DAO transaction bodies, migrations and Kotlin top-level declarations are included.

JVM tests enable JaCoCo's `includeNoLocationClasses` so application classes loaded by
Robolectric's sandbox contribute execution data. JDK implementation classes are excluded
from instrumentation. A missing execution file or missing compiled classes fails the gate.
On the verified schema 2 suite, the report records execution of the authored DAO merge
transactions and the non-destructive migration in addition to repository rules.

## Primary sources

- [AGP 9.3 release notes and compatibility](https://developer.android.com/build/releases/agp-9-3-0-release-notes)
- [Kotlin Gradle and AGP compatibility](https://kotlinlang.org/docs/gradle-configure-project.html)
- [AGP built-in Kotlin migration](https://developer.android.com/build/migrate-to-built-in-kotlin)
- [KSP releases](https://github.com/google/ksp/releases)
- [Dagger releases](https://github.com/google/dagger/releases)
- [Compose BOM mapping](https://developer.android.com/develop/ui/compose/bom/bom-mapping)
- [AndroidX releases](https://developer.android.com/jetpack/androidx/versions)
- [Google Maven metadata](https://dl.google.com/dl/android/maven2/master-index.xml)
- [Maven Central](https://repo.maven.apache.org/maven2/)
- [Gradle 9.5.0 checksum](https://services.gradle.org/distributions/gradle-9.5.0-bin.zip.sha256)
