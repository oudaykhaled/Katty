package com.catfact.app.core.network.di;

import com.catfact.app.core.network.interceptor.InterceptorEntry;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class NetworkModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<Set<InterceptorEntry>> interceptorsProvider;

  private NetworkModule_ProvideOkHttpClientFactory(
      Provider<Set<InterceptorEntry>> interceptorsProvider) {
    this.interceptorsProvider = interceptorsProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(interceptorsProvider.get());
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(
      Provider<Set<InterceptorEntry>> interceptorsProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(interceptorsProvider);
  }

  public static OkHttpClient provideOkHttpClient(Set<InterceptorEntry> interceptors) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClient(interceptors));
  }
}
