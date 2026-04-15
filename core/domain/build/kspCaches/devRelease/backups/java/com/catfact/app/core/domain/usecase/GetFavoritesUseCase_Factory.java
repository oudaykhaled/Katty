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
public final class GetFavoritesUseCase_Factory implements Factory<GetFavoritesUseCase> {
  private final Provider<CatFactRepository> repositoryProvider;

  private GetFavoritesUseCase_Factory(Provider<CatFactRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetFavoritesUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetFavoritesUseCase_Factory create(Provider<CatFactRepository> repositoryProvider) {
    return new GetFavoritesUseCase_Factory(repositoryProvider);
  }

  public static GetFavoritesUseCase newInstance(CatFactRepository repository) {
    return new GetFavoritesUseCase(repository);
  }
}
