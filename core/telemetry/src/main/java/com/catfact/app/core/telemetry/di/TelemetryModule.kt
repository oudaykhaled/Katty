package com.catfact.app.core.telemetry.di

import com.catfact.app.core.telemetry.CrashReporter
import com.catfact.app.core.telemetry.EventTracker
import com.catfact.app.core.telemetry.NoOpCrashReporter
import com.catfact.app.core.telemetry.NoOpEventTracker
import com.catfact.app.core.telemetry.NoOpPerformanceTracer
import com.catfact.app.core.telemetry.PerformanceTracer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TelemetryModule {
    @Binds @Singleton
    abstract fun bindEventTracker(impl: NoOpEventTracker): EventTracker
    @Binds @Singleton
    abstract fun bindPerformanceTracer(impl: NoOpPerformanceTracer): PerformanceTracer
    @Binds @Singleton
    abstract fun bindCrashReporter(impl: NoOpCrashReporter): CrashReporter
}
