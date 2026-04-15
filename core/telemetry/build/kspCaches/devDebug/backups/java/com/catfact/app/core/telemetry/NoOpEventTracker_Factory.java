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
public final class NoOpEventTracker_Factory implements Factory<NoOpEventTracker> {
  @Override
  public NoOpEventTracker get() {
    return newInstance();
  }

  public static NoOpEventTracker_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static NoOpEventTracker newInstance() {
    return new NoOpEventTracker();
  }

  private static final class InstanceHolder {
    static final NoOpEventTracker_Factory INSTANCE = new NoOpEventTracker_Factory();
  }
}
