package com.catfact.app.feature.factdetail.viewmodel;

import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class DetailViewModel_Factory_Impl implements DetailViewModel.Factory {
  private final DetailViewModel_Factory delegateFactory;

  DetailViewModel_Factory_Impl(DetailViewModel_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public DetailViewModel create(String factId) {
    return delegateFactory.get(factId);
  }

  public static Provider<DetailViewModel.Factory> create(DetailViewModel_Factory delegateFactory) {
    return InstanceFactory.create(new DetailViewModel_Factory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<DetailViewModel.Factory> createFactoryProvider(
      DetailViewModel_Factory delegateFactory) {
    return InstanceFactory.create(new DetailViewModel_Factory_Impl(delegateFactory));
  }
}
