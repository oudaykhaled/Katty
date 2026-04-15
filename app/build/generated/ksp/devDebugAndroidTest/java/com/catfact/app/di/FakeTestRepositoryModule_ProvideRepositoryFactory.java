package com.catfact.app.di;

import com.catfact.app.core.domain.repository.CatFactRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class FakeTestRepositoryModule_ProvideRepositoryFactory implements Factory<CatFactRepository> {
  @Override
  public CatFactRepository get() {
    return provideRepository();
  }

  public static FakeTestRepositoryModule_ProvideRepositoryFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CatFactRepository provideRepository() {
    return Preconditions.checkNotNullFromProvides(FakeTestRepositoryModule.INSTANCE.provideRepository());
  }

  private static final class InstanceHolder {
    static final FakeTestRepositoryModule_ProvideRepositoryFactory INSTANCE = new FakeTestRepositoryModule_ProvideRepositoryFactory();
  }
}
