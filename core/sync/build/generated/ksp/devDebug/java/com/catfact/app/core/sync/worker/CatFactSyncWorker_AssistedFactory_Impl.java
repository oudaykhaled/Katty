package com.catfact.app.core.sync.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
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
public final class CatFactSyncWorker_AssistedFactory_Impl implements CatFactSyncWorker_AssistedFactory {
  private final CatFactSyncWorker_Factory delegateFactory;

  CatFactSyncWorker_AssistedFactory_Impl(CatFactSyncWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public CatFactSyncWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<CatFactSyncWorker_AssistedFactory> create(
      CatFactSyncWorker_Factory delegateFactory) {
    return InstanceFactory.create(new CatFactSyncWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<CatFactSyncWorker_AssistedFactory> createFactoryProvider(
      CatFactSyncWorker_Factory delegateFactory) {
    return InstanceFactory.create(new CatFactSyncWorker_AssistedFactory_Impl(delegateFactory));
  }
}
