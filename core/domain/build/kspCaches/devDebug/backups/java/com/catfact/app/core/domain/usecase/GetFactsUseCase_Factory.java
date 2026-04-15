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
public final class GetFactsUseCase_Factory implements Factory<GetFactsUseCase> {
  private final Provider<CatFactRepository> repositoryProvider;

  private GetFactsUseCase_Factory(Provider<CatFactRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetFactsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetFactsUseCase_Factory create(Provider<CatFactRepository> repositoryProvider) {
    return new GetFactsUseCase_Factory(repositoryProvider);
  }

  public static GetFactsUseCase newInstance(CatFactRepository repository) {
    return new GetFactsUseCase(repository);
  }
}
