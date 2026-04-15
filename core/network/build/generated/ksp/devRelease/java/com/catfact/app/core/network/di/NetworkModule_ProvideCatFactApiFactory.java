package com.catfact.app.core.network.di;

import com.catfact.app.core.network.api.CatFactApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import retrofit2.Retrofit;

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
public final class NetworkModule_ProvideCatFactApiFactory implements Factory<CatFactApi> {
  private final Provider<Retrofit> retrofitProvider;

  private NetworkModule_ProvideCatFactApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public CatFactApi get() {
    return provideCatFactApi(retrofitProvider.get());
  }

  public static NetworkModule_ProvideCatFactApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideCatFactApiFactory(retrofitProvider);
  }

  public static CatFactApi provideCatFactApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideCatFactApi(retrofit));
  }
}
