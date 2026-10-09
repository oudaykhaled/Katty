# ADR 0002: Separate feed order, cached facts, and local annotations

Status: accepted; corrected during the October 2026 persistence review.

## Context

The upstream facts API is read-only. The server owns fact text and page ordering; the user owns bookmarks and notes. Replacing a whole row can erase user data. Sorting by cache timestamps also puts newer page-two rows above page one, which breaks infinite scrolling. Deleting a fact during refresh can invalidate an open detail and make a subsequent note update silently do nothing.

## Decision

Room owns displayed data. Schema v2 adds nullable `feedPosition`, independent of the real `cachedAt` timestamp. A first-page refresh clears feed membership and merges the response transactionally. Later pages append at explicit positions and preserve the earliest occurrence of duplicates. Random hero facts are cached without changing active feed membership. Remote merges update only server-owned fields; local annotation edits change only the intended user-owned field.

Inactive cache rows are retained, so favorites, note edits and open details remain valid after refresh. This is acceptable for the sample's small read-only corpus; no automatic cache eviction is claimed. Local UPDATE counts are checked so missing-row writes cannot report success. WorkManager uses a separate cache-only refresh operation, preserving interactive feed membership and order. It does not upload annotations. A regression loads four interactive pages, refreshes the background cache, then appends page five without gaps.

## Migration and verification

The v1→v2 ALTER migration preserves existing rows and assigns their initial feed rank using the old deterministic cache order. It does not recreate the database. A real file-backed migration test verifies the old schema and every retained field. Room regressions also verify page order, duplicate pages, repeated refresh, inactive detail edits, database reopening, network failure, and edits during a refresh.

New random/list facts share a content-derived identity. Historical random UUID records are kept to preserve user data; the same text may therefore coexist under an old and new ID. No destructive historical deduplication is attempted.
