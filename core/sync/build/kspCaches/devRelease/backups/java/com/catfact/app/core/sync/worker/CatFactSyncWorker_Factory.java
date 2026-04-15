package com.catfact.app.core.sync.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.catfact.app.core.domain.repository.CatFactRepository;
import com.catfact.app.core.logging.Logger;
import com.catfact.app.core.telemetry.EventTracker;
import dagger.internal.DaggerGenerated;
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
public final class CatFactSyncWorker_Factory {
  private final Provider<CatFactRepository> repositoryProvider;

  private final Provider<Logger> loggerProvider;

  private final Provider<EventTracker> eventTrackerProvider;

  private CatFactSyncWorker_Factory(Provider<CatFactRepository> repositoryProvider,
      Provider<Logger> loggerProvider, Provider<EventTracker> eventTrackerProvider) {
    this.repositoryProvider = repositoryProvider;
    this.loggerProvider = loggerProvider;
    this.eventTrackerProvider = eventTrackerProvider;
  }

  public CatFactSyncWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, repositoryProvider.get(), loggerProvider.get(), eventTrackerProvider.get());
  }

  public static CatFactSyncWorker_Factory create(Provider<CatFactRepository> repositoryProvider,
      Provider<Logger> loggerProvider, Provider<EventTracker> eventTrackerProvider) {
    return new CatFactSyncWorker_Factory(repositoryProvider, loggerProvider, eventTrackerProvider);
  }

  public static CatFactSyncWorker newInstance(Context appContext, WorkerParameters workerParams,
      CatFactRepository repository, Logger logger, EventTracker eventTracker) {
    return new CatFactSyncWorker(appContext, workerParams, repository, logger, eventTracker);
  }
}
