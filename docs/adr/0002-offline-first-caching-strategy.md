# ADR 0002: Offline-First Caching Strategy

## Status
Accepted

## Context
Mobile networks are unreliable. The app must display content immediately on launch, even without connectivity. The catfact.ninja API is read-only, so the offline-first pattern adapts to a caching-with-local-mutations model.

## Decision
- Room database is the Single Source of Truth (SSOT) for all displayed data
- UI observes Room via Flow; network writes go to Room first
- `CatFactDao.replaceAllFacts()` uses a safe-replace transaction that preserves locally modified facts (bookmarks, notes) during cache refresh
- `CatFactSyncWorker` periodically refreshes the cache using WorkManager with network constraints
- Favorites and personal notes are local-only mutations stored in Room

## Consequences
- App launches instantly with cached data, even offline
- Users never lose bookmarks or notes during a sync
- The architecture demonstrates production-grade offline patterns even for a read-only API
- WorkManager handles retry, backoff, and battery-efficient scheduling automatically
