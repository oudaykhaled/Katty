package com.catfact.app.core.telemetry;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class NoOpPerformanceTracer_Factory implements Factory<NoOpPerformanceTracer> {
  @Override
  public NoOpPerformanceTracer get() {
    return newInstance();
  }

  public static NoOpPerformanceTracer_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static NoOpPerformanceTracer newInstance() {
    return new NoOpPerformanceTracer();
  }

  private static final class InstanceHolder {
    static final NoOpPerformanceTracer_Factory INSTANCE = new NoOpPerformanceTracer_Factory();
  }
}
