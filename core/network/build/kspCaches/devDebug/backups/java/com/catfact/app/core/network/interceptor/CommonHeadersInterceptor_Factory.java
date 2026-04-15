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
public final class CommonHeadersInterceptor_Factory implements Factory<CommonHeadersInterceptor> {
  @Override
  public CommonHeadersInterceptor get() {
    return newInstance();
  }

  public static CommonHeadersInterceptor_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CommonHeadersInterceptor newInstance() {
    return new CommonHeadersInterceptor();
  }

  private static final class InstanceHolder {
    static final CommonHeadersInterceptor_Factory INSTANCE = new CommonHeadersInterceptor_Factory();
  }
}
