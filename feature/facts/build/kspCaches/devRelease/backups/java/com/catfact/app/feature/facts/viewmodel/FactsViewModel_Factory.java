package com.catfact.app.feature.facts.viewmodel;

import com.catfact.app.core.domain.usecase.FetchRandomFactUseCase;
import com.catfact.app.core.domain.usecase.GetFactsUseCase;
import com.catfact.app.core.domain.usecase.RefreshFactsUseCase;
import com.catfact.app.core.domain.usecase.ToggleBookmarkUseCase;
import com.catfact.app.core.telemetry.EventTracker;
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
public final class FactsViewModel_Factory implements Factory<FactsViewModel> {
  private final Provider<GetFactsUseCase> getFactsUseCaseProvider;

  private final Provider<FetchRandomFactUseCase> fetchRandomFactUseCaseProvider;

  private final Provider<ToggleBookmarkUseCase> toggleBookmarkUseCaseProvider;

  private final Provider<RefreshFactsUseCase> refreshFactsUseCaseProvider;

  private final Provider<EventTracker> eventTrackerProvider;

  private FactsViewModel_Factory(Provider<GetFactsUseCase> getFactsUseCaseProvider,
      Provider<FetchRandomFactUseCase> fetchRandomFactUseCaseProvider,
      Provider<ToggleBookmarkUseCase> toggleBookmarkUseCaseProvider,
      Provider<RefreshFactsUseCase> refreshFactsUseCaseProvider,
      Provider<EventTracker> eventTrackerProvider) {
    this.getFactsUseCaseProvider = getFactsUseCaseProvider;
    this.fetchRandomFactUseCaseProvider = fetchRandomFactUseCaseProvider;
    this.toggleBookmarkUseCaseProvider = toggleBookmarkUseCaseProvider;
    this.refreshFactsUseCaseProvider = refreshFactsUseCaseProvider;
    this.eventTrackerProvider = eventTrackerProvider;
  }

  @Override
  public FactsViewModel get() {
    return newInstance(getFactsUseCaseProvider.get(), fetchRandomFactUseCaseProvider.get(), toggleBookmarkUseCaseProvider.get(), refreshFactsUseCaseProvider.get(), eventTrackerProvider.get());
  }

  public static FactsViewModel_Factory create(Provider<GetFactsUseCase> getFactsUseCaseProvider,
      Provider<FetchRandomFactUseCase> fetchRandomFactUseCaseProvider,
      Provider<ToggleBookmarkUseCase> toggleBookmarkUseCaseProvider,
      Provider<RefreshFactsUseCase> refreshFactsUseCaseProvider,
      Provider<EventTracker> eventTrackerProvider) {
    return new FactsViewModel_Factory(getFactsUseCaseProvider, fetchRandomFactUseCaseProvider, toggleBookmarkUseCaseProvider, refreshFactsUseCaseProvider, eventTrackerProvider);
  }

  public static FactsViewModel newInstance(GetFactsUseCase getFactsUseCase,
      FetchRandomFactUseCase fetchRandomFactUseCase, ToggleBookmarkUseCase toggleBookmarkUseCase,
      RefreshFactsUseCase refreshFactsUseCase, EventTracker eventTracker) {
    return new FactsViewModel(getFactsUseCase, fetchRandomFactUseCase, toggleBookmarkUseCase, refreshFactsUseCase, eventTracker);
  }
}
