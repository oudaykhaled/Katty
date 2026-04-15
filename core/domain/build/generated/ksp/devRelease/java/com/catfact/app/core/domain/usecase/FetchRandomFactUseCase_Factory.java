package com.catfact.app.core.domain.usecase;

import com.catfact.app.core.domain.repository.CatFactRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class FetchRandomFactUseCase_Factory implements Factory<FetchRandomFactUseCase> {
  private final Provider<CatFactRepository> repositoryProvider;

  private FetchRandomFactUseCase_Factory(Provider<CatFactRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public FetchRandomFactUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static FetchRandomFactUseCase_Factory create(
      Provider<CatFactRepository> repositoryProvider) {
    return new FetchRandomFactUseCase_Factory(repositoryProvider);
  }

  public static FetchRandomFactUseCase newInstance(CatFactRepository repository) {
    return new FetchRandomFactUseCase(repository);
  }
}
