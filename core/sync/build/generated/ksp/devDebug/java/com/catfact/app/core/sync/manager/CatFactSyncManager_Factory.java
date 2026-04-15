package com.catfact.app.core.sync.manager;

import androidx.work.WorkManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
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
public final class CatFactSyncManager_Factory implements Factory<CatFactSyncManager> {
  private final Provider<WorkManager> workManagerProvider;

  private CatFactSyncManager_Factory(Provider<WorkManager> workManagerProvider) {
    this.workManagerProvider = workManagerProvider;
  }

  @Override
  public CatFactSyncManager get() {
    return newInstance(workManagerProvider.get());
  }

  public static CatFactSyncManager_Factory create(Provider<WorkManager> workManagerProvider) {
    return new CatFactSyncManager_Factory(workManagerProvider);
  }

  public static CatFactSyncManager newInstance(WorkManager workManager) {
    return new CatFactSyncManager(workManager);
  }
}
