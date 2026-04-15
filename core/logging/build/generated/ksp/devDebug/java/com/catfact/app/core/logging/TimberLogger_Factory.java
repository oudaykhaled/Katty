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
public final class TimberLogger_Factory implements Factory<TimberLogger> {
  @Override
  public TimberLogger get() {
    return newInstance();
  }

  public static TimberLogger_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TimberLogger newInstance() {
    return new TimberLogger();
  }

  private static final class InstanceHolder {
    static final TimberLogger_Factory INSTANCE = new TimberLogger_Factory();
  }
}
