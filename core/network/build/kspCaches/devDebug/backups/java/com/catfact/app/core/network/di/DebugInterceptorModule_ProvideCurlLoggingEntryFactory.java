package com.catfact.app.core.network.di;

import com.catfact.app.core.network.interceptor.CurlLoggingInterceptor;
import com.catfact.app.core.network.interceptor.InterceptorEntry;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
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
public final class DebugInterceptorModule_ProvideCurlLoggingEntryFactory implements Factory<InterceptorEntry> {
  private final Provider<CurlLoggingInterceptor> interceptorProvider;

  private DebugInterceptorModule_ProvideCurlLoggingEntryFactory(
      Provider<CurlLoggingInterceptor> interceptorProvider) {
    this.interceptorProvider = interceptorProvider;
  }

  @Override
  public InterceptorEntry get() {
    return provideCurlLoggingEntry(interceptorProvider.get());
  }

  public static DebugInterceptorModule_ProvideCurlLoggingEntryFactory create(
      Provider<CurlLoggingInterceptor> interceptorProvider) {
    return new DebugInterceptorModule_ProvideCurlLoggingEntryFactory(interceptorProvider);
  }

  public static InterceptorEntry provideCurlLoggingEntry(CurlLoggingInterceptor interceptor) {
    return Preconditions.checkNotNullFromProvides(DebugInterceptorModule.INSTANCE.provideCurlLoggingEntry(interceptor));
  }
}
