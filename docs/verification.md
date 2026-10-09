# Verification evidence

## Verdict and environment

**PASS-WITH-GAPS for the exercised local flows.** Final reports contain **161 JVM tests and 46 Android instrumented tests, with zero failures, errors or skips**. Android lint has **zero errors and 17 warnings**: 11 in app and six in the design system. Detekt has zero findings. Physical-device performance, TalkBack and the wider device matrix remain outside this verification.

Quality pass dated 9 October 2026 against upstream main `70558c0d2f6fb44a4a06367f82cf9f18fa82d252` plus the local quality changes. Host: macOS arm64, JDK 17, Android SDK 37.0. Generated reports remain under ignored build directories; historical Git objects were not rewritten.

Device: task-owned `codex_katty_transferflow_api35` AVD, Android 15/API 35 arm64 Google Play image, Pixel 6 profile, two CPU cores and 2 GiB RAM. Its serial is `emulator-5690`, with a private adb server on port `5038`. Private emulator autoscanning was disabled with `ADB_LOCAL_TRANSPORT_MAX_PORT=5553`; the owned emulator registered with that server directly. Pre-existing emulators, the shared adb server and physical devices were not restarted or modified.

An earlier run using the shared adb service was interrupted by a server EOF and produced an incomplete test report. The complete runs below used a fresh Gradle process and the isolated connection, replacing that interrupted result.

## Executed source and device gates

```sh
ANDROID_ADB_SERVER_PORT=5038 ANDROID_SERIAL=emulator-5690 \
  ./gradlew allJvmTests lintDevDebug detekt \
  :app:assembleDevDebug :app:assembleDevDebugAndroidTest allInstrumentedTests \
  -Pandroid.injected.device.serial=emulator-5690 \
  --no-daemon --no-parallel --max-workers=2
```

Result: **BUILD SUCCESSFUL**, 4m 29s. This ran 161 JVM tests and the then-current 44 instrumented tests. After the final tab-reselect and manifest deep-link corrections, the affected app gates were run again:

```sh
ANDROID_ADB_SERVER_PORT=5038 ANDROID_SERIAL=emulator-5690 \
  ./gradlew :app:connectedDevDebugAndroidTest :app:testDevDebugUnitTest \
  :app:lintDevDebug :app:detekt :app:assembleDevDebug \
  -Pandroid.injected.device.serial=emulator-5690 \
  --no-daemon --no-parallel --max-workers=2
```

Result: **BUILD SUCCESSFUL**, 1m 8s. The final app suite has 15 tests, replacing its earlier 13-test result. The current distinct instrumented suite total is **46**, rather than the sum of repeated executions:

| Suite | Tests | Result |
| --- | ---: | --- |
| App navigation/lifecycle (13) and Hilt/WorkManager (2) | 15 | Passed |
| Room/database | 11 | Passed |
| Detail Compose UI | 9 | Passed |
| Facts Compose UI | 7 | Passed |
| Favorites Compose UI | 4 | Passed |
| **Total** | **46** | **Zero failures or skips** |

The app navigation and Hilt worker tests use an explicitly substituted fake repository. Feature UI tests exercise supplied screen state. Database instrumentation uses real Room. These tests are separate from the production application and live API smoke below.

The app regressions cover separate detail ViewModels and drafts, Save Note and bookmark/remove actions, both selected-tab reselections from detail, warm VIEW intents without test-only intent flags, consumed-link activity recreation, unsaved-draft activity recreation, and a generated Hilt worker executing through WorkManager to `SUCCEEDED`. Facts tests cover first-page loading, refresh cursor reset, duplicate pagination prevention, cancellation, and a stationary viewport after pagination failure.

Lint warnings include the documented target/AGP pins, launcher icons, unused resources, a provider manifest declaration warning, vector compatibility and modifier conventions. This is not a warning-free claim. Some Compose test-rule APIs emit deprecation warnings; their verified scheduling behavior was retained during this pass.

## Core coverage and storage

A separate executed `verifyCoreCoverage` gate enforces 80% lines and 70% branches across authored domain/database classes. Measurement: **146/155 lines (94.19%)**, **42/44 branches (95.45%)**. Robolectric application-classloader coverage is enabled; DAO transactions and the migration record execution. Android/DI/Room-generated code is excluded explicitly, while Kotlin top-level classes are retained. This is not whole-app coverage.

The database module has 35 JVM tests, including real SQLite through Robolectric, a file-backed database close/reopen, additive v1→v2 migration, annotation preservation, server feed ordering and background cache-refresh isolation. Domain has 24 JVM tests; the other 102 exercise presentation, networking, sync, application wiring and supporting code.

## Normal application and live network verification

The normal debug APK was installed and launched outside the Hilt test runner. This uses `CatFactApplication`, the production Room repository and the public API. No facts were manually inserted into Room for this smoke.

Observed Android requests:

- `GET facts?page=1&limit=20` → **HTTP 200**, feeding the visible list.
- `GET facts?page=1&limit=50` → **HTTP 200**, from background cache refresh.
- `GET fact` → **HTTP 200**; tapping **New Fact** replaced the displayed hero text.

Real background execution after the application factory fix:

```text
Sync: sync_started | runAttempt=0
Network: GET facts?page=1&limit=50 -> 200
Sync: sync_completed | factCount=50
WM-WorkerWrapper: Worker result SUCCESS
work id: d4817add-8513-4281-aa3b-0a367038b82a
worker: com.catfact.app.core.sync.worker.CatFactSyncWorker
```

This replaces the reproduced startup failure where the default WorkManager factory tried reflection and could not construct the injected worker. The application now supplies `HiltWorkerFactory`, removes the default initializer and resolves the sync manager after application injection completes.

Verified real actions:

- Opened a live fact, bookmarked it, saved the note `Favorite`, reopened it and found it in Favorites.
- Added unsaved text to produce `FavoriteDraft`, sent the app to the launcher, killed PID `6160` with `am kill`, and verified `pidof` returned no process. A launcher reopen created PID `6663` and restored the same detail and unsaved draft. After leaving and reopening detail, the saved note remained `Favorite` and the bookmark remained set. The initial kill request was ignored while the process was recent; a later successful kill was verified before claiming process death.
- Opened Android's share chooser; its preview contained the selected fact text. No recipient was selected and nothing was sent.
- On the final APK, tapping the selected Facts tab from detail returned to the Facts root. Both Facts and Favorites reselections also passed instrumented regressions.
- Sent an external `ACTION_VIEW` for `catfact://fact/<cached-id>` without an explicit activity component or `SINGLE_TOP` intent flag. Android delivered it to the current top activity, displayed the requested second fact, and the app Back action returned to the previous first detail. This verifies the manifest's `singleTop` behavior in addition to parser tests.

## Screenshots and local artifacts

Actual emulator captures, with live API data and test-authored local annotations:

| Capture | Evidence |
| --- | --- |
| [Facts](screenshots/facts.png) | Live hero, New Fact, search and bookmarked list item |
| [Restored draft](screenshots/draft-restored.png) | `FavoriteDraft` and Save Note restored after process death |
| [Favorites](screenshots/favorites.png) | Persisted bookmarked fact after reopening |

Detailed local session artifacts are in `/tmp/katty-runtime-evidence`: screenshot/XML pairs, `events.jsonl`, `process-death.json`, `production-before-worker-fix.log` and `production-after-worker-fix.log`. Final corrected navigation captures are `22-final-reselect-facts`, `23-final-external-warm-link` and `24-final-link-back-to-previous-detail`. These temporary artifacts are not part of a fresh clone.

Build logs for this session: `/tmp/katty-private-final.log` and `/tmp/katty-navigation-final.log`. Machine-readable current results are under each module's `build/outputs/androidTest-results/connected/debug/flavors/dev` and `build/test-results/testDevDebugUnitTest`.

## Boundaries

CI is configured in `.github/workflows/android.yml`; a local pass does not establish a remote GitHub Actions pass. The live API smoke establishes the observed requests and responses at this run, not future server availability.

The runtime matrix here is one API 35 arm64 emulator. TalkBack, RTL, large-font rendering, other Android versions and physical-device performance measurements are not claimed as executed. Share verification stopped at chooser creation and preview. Process death is a separate normal-app test; activity recreation is also covered by instrumentation.

Stable baseline-profile producer/consumer wiring is configured and the authored profile is retained. Profile generation and release performance results are recorded separately when executed. Two historical benchmark scenarios remain ignored and documented in `benchmark/README.md`.

## Baseline-profile and benchmark packaging

```sh
./gradlew --no-daemon :app:assembleDevNonMinifiedRelease \
  :benchmark:assembleDevNonMinifiedRelease \
  :app:assembleDevBenchmarkRelease :benchmark:assembleDevBenchmarkRelease \
  --max-workers=2 --console=plain
```

Result: **BUILD SUCCESSFUL**, 2m 6s, 419 tasks. Both app targets and instrumentation drivers compile with the updated stable Baseline Profile plugin. This validates variant wiring and packaging; profile collection and benchmark measurements were not executed. The documented Detekt plugin deprecation remains relevant to a future Gradle 10 upgrade.
