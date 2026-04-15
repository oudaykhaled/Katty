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
public final class ToggleBookmarkUseCase_Factory implements Factory<ToggleBookmarkUseCase> {
  private final Provider<CatFactRepository> repositoryProvider;

  private ToggleBookmarkUseCase_Factory(Provider<CatFactRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ToggleBookmarkUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ToggleBookmarkUseCase_Factory create(
      Provider<CatFactRepository> repositoryProvider) {
    return new ToggleBookmarkUseCase_Factory(repositoryProvider);
  }

  public static ToggleBookmarkUseCase newInstance(CatFactRepository repository) {
    return new ToggleBookmarkUseCase(repository);
  }
}
