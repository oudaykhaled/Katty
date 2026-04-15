package com.catfact.app.core.network.di;

import com.catfact.app.core.network.interceptor.InterceptorEntry;
import com.catfact.app.core.network.interceptor.RetryInterceptor;
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
public final class InterceptorModule_ProvideRetryInterceptorEntryFactory implements Factory<InterceptorEntry> {
  private final Provider<RetryInterceptor> interceptorProvider;

  private InterceptorModule_ProvideRetryInterceptorEntryFactory(
      Provider<RetryInterceptor> interceptorProvider) {
    this.interceptorProvider = interceptorProvider;
  }

  @Override
  public InterceptorEntry get() {
    return provideRetryInterceptorEntry(interceptorProvider.get());
  }

  public static InterceptorModule_ProvideRetryInterceptorEntryFactory create(
      Provider<RetryInterceptor> interceptorProvider) {
    return new InterceptorModule_ProvideRetryInterceptorEntryFactory(interceptorProvider);
  }

  public static InterceptorEntry provideRetryInterceptorEntry(RetryInterceptor interceptor) {
    return Preconditions.checkNotNullFromProvides(InterceptorModule.INSTANCE.provideRetryInterceptorEntry(interceptor));
  }
}
