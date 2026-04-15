# Failure Mode Analysis

## Network Failures
| Failure | Impact | Mitigation |
|---------|--------|------------|
| No connectivity | Cannot fetch new facts | Offline-first: cached facts displayed, ConnectivityBanner shown |
| API 5xx error | Fetch fails | RetryInterceptor with exponential backoff (500ms, 1s, 2s) |
| API timeout | Fetch hangs | OkHttp explicit 30s timeout on connect/read/write |
| Malformed JSON | Deserialization crash | `ignoreUnknownKeys = true`, `coerceInputValues = true` in Json config |

## Database Failures
| Failure | Impact | Mitigation |
|---------|--------|------------|
| Schema migration failure | Crash on upgrade | Room `exportSchema = true`, tested with `MigrationTestHelper` |
| Concurrent write conflict | Data loss | Room `@Transaction` for `replaceAllFacts()`, preserving pending mutations |
| Disk full | Write fails | Room catches SQLiteFullException; UI shows error |

## Process Death
| Failure | Impact | Mitigation |
|---------|--------|------------|
| Process killed during note edit | Draft lost | `SavedStateHandle` persists draft to Bundle, restored on recreation |
| Process killed during sync | Partial data | WorkManager auto-retries; idempotent sync design |

## UI Failures
| Failure | Impact | Mitigation |
|---------|--------|------------|
| Empty facts list | Blank screen | Empty state composable with illustration |
| Bookmark optimistic update fails | Inconsistent UI | Rollback via Room observation (SSOT corrects UI automatically) |
