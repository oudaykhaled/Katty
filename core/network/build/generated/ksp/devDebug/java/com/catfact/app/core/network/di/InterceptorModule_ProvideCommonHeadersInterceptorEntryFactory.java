package com.catfact.app.core.network.di;

import com.catfact.app.core.network.interceptor.CommonHeadersInterceptor;
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
public final class InterceptorModule_ProvideCommonHeadersInterceptorEntryFactory implements Factory<InterceptorEntry> {
  private final Provider<CommonHeadersInterceptor> interceptorProvider;

  private InterceptorModule_ProvideCommonHeadersInterceptorEntryFactory(
      Provider<CommonHeadersInterceptor> interceptorProvider) {
    this.interceptorProvider = interceptorProvider;
  }

  @Override
  public InterceptorEntry get() {
    return provideCommonHeadersInterceptorEntry(interceptorProvider.get());
  }

  public static InterceptorModule_ProvideCommonHeadersInterceptorEntryFactory create(
      Provider<CommonHeadersInterceptor> interceptorProvider) {
    return new InterceptorModule_ProvideCommonHeadersInterceptorEntryFactory(interceptorProvider);
  }

  public static InterceptorEntry provideCommonHeadersInterceptorEntry(
      CommonHeadersInterceptor interceptor) {
    return Preconditions.checkNotNullFromProvides(InterceptorModule.INSTANCE.provideCommonHeadersInterceptorEntry(interceptor));
  }
}
