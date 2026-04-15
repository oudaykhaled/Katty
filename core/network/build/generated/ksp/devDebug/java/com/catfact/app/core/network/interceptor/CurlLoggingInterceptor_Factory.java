package com.catfact.app.core.network.interceptor;

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
public final class CurlLoggingInterceptor_Factory implements Factory<CurlLoggingInterceptor> {
  @Override
  public CurlLoggingInterceptor get() {
    return newInstance();
  }

  public static CurlLoggingInterceptor_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CurlLoggingInterceptor newInstance() {
    return new CurlLoggingInterceptor();
  }

  private static final class InstanceHolder {
    static final CurlLoggingInterceptor_Factory INSTANCE = new CurlLoggingInterceptor_Factory();
  }
}
