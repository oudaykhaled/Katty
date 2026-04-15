package com.catfact.app.core.logging;

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
public final class NoOpLogger_Factory implements Factory<NoOpLogger> {
  @Override
  public NoOpLogger get() {
    return newInstance();
  }

  public static NoOpLogger_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static NoOpLogger newInstance() {
    return new NoOpLogger();
  }

  private static final class InstanceHolder {
    static final NoOpLogger_Factory INSTANCE = new NoOpLogger_Factory();
  }
}
