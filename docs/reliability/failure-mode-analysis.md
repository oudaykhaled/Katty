# Failure behavior

| Failure | Intended behavior | Evidence |
|---|---|---|
| Network unavailable after facts were fetched | Continue displaying cached Room data; surface refresh failure without deleting annotations | Repository tests and device verification notes |
| First-page refresh or pagination | Merge remote fields while retaining local bookmarks and notes | Room-backed repository regressions |
| Network request cancelled | Propagate cancellation; avoid reporting it as a user-facing network failure | ViewModel and repository tests |
| Multiple pagination events | Permit one in-flight request and advance only after success | Facts ViewModel tests |
| Navigate between different details | Give each entry its own ViewModel and saved draft | Navigation tests |
| Activity recreation while editing | Restore supported draft state through SavedStateHandle | Detail ViewModel tests; device scope recorded separately |
| API or storage error | Surface an error and retain previously stored data where the operation did not commit | Repository and state tests |

The Room v1→v2 migration adds feed position without recreating the database; a file-backed migration regression checks data preservation. Exported schemas remain tracked. Disk-full recovery and production telemetry are not claimed as tested behavior. See [verification notes](../verification.md) for the checks actually executed.
