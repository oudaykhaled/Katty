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
public final class RefreshFactsUseCase_Factory implements Factory<RefreshFactsUseCase> {
  private final Provider<CatFactRepository> repositoryProvider;

  private RefreshFactsUseCase_Factory(Provider<CatFactRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public RefreshFactsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static RefreshFactsUseCase_Factory create(Provider<CatFactRepository> repositoryProvider) {
    return new RefreshFactsUseCase_Factory(repositoryProvider);
  }

  public static RefreshFactsUseCase newInstance(CatFactRepository repository) {
    return new RefreshFactsUseCase(repository);
  }
}
