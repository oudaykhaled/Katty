package com.catfact.app;

import com.catfact.app.core.sync.manager.CatFactSyncManager;
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
public final class CatFactApplication_MembersInjector implements MembersInjector<CatFactApplication> {
  private final Provider<CatFactSyncManager> syncManagerProvider;

  private CatFactApplication_MembersInjector(Provider<CatFactSyncManager> syncManagerProvider) {
    this.syncManagerProvider = syncManagerProvider;
  }

  @Override
  public void injectMembers(CatFactApplication instance) {
    injectSyncManager(instance, syncManagerProvider.get());
  }

  public static MembersInjector<CatFactApplication> create(
      Provider<CatFactSyncManager> syncManagerProvider) {
    return new CatFactApplication_MembersInjector(syncManagerProvider);
  }

  @InjectedFieldSignature("com.catfact.app.CatFactApplication.syncManager")
  public static void injectSyncManager(CatFactApplication instance,
      CatFactSyncManager syncManager) {
    instance.syncManager = syncManager;
  }
}
