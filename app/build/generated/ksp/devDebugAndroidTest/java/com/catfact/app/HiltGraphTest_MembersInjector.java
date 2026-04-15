package com.catfact.app;

import com.catfact.app.core.domain.repository.CatFactRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class HiltGraphTest_MembersInjector implements MembersInjector<HiltGraphTest> {
  private final Provider<CatFactRepository> repositoryProvider;

  private HiltGraphTest_MembersInjector(Provider<CatFactRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public void injectMembers(HiltGraphTest instance) {
    injectRepository(instance, repositoryProvider.get());
  }

  public static MembersInjector<HiltGraphTest> create(
      Provider<CatFactRepository> repositoryProvider) {
    return new HiltGraphTest_MembersInjector(repositoryProvider);
  }

  @InjectedFieldSignature("com.catfact.app.HiltGraphTest.repository")
  public static void injectRepository(HiltGraphTest instance, CatFactRepository repository) {
    instance.repository = repository;
  }
}
