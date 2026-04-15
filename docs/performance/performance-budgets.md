# Performance Budgets

## Startup
| Metric | Budget | Measurement |
|--------|--------|-------------|
| Warm startup | < 500ms | Macrobenchmark `StartupTimingMetric` |
| Cold startup | < 1500ms | Macrobenchmark `StartupTimingMetric` |

## Frame Timing
| Metric | Budget | Measurement |
|--------|--------|-------------|
| P50 frame duration | < 8ms | Macrobenchmark `FrameTimingMetric` |
| P90 frame duration | < 16ms | Macrobenchmark `FrameTimingMetric` |
| P99 frame duration | < 32ms | Macrobenchmark `FrameTimingMetric` |

## APK Size
| Metric | Budget |
|--------|--------|
| Debug APK | < 20MB |
| Release APK | < 10MB |

## Network
| Metric | Budget |
|--------|--------|
| API response time (P95) | < 2s |
| Retry attempts before failure | 2 (exponential backoff) |

## Database
| Metric | Budget |
|--------|--------|
| Room query time (observeAll) | < 50ms |
| Safe-replace transaction | < 200ms |
