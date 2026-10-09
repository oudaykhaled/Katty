# Engineering standards

Changes should explain a concrete behavior, keep module responsibilities clear, and include tests for failure and recovery paths when they change. Use constructor-injected boundaries and deterministic coroutine tests. Room behavior needs database-backed tests; navigation and Compose interaction need device tests.

The pull-request workflow builds the dev debug APK, runs Android lint, Detekt and JVM tests, enforces focused core coverage, and runs instrumented tests on an API 35 emulator. Checks fail on errors. Coverage scope and exclusions are explicit in the build; generated Android/DI code may be excluded, but Kotlin source files must not be removed wholesale from the denominator.

Dependency changes use stable published artifacts and a documented compatible toolchain. Upgrade runtime and test families together. Keep the wrapper checksum, Room schemas, authored baseline profiles, and source under version control. Keep generated build output, local SDK paths, signing material, and IDE state out of Git.

Record executed commands and device/build details in verification notes. Do not describe a written test as passed, a debug benchmark as a release measurement, or configured tooling as a completed check. Android lint and Detekt are required CI checks. Narrow rule suppressions require a local explanation; no generated baseline conceals outstanding findings.
