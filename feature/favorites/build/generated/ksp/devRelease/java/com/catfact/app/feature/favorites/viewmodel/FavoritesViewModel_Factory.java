package com.catfact.app.feature.favorites.viewmodel;

import com.catfact.app.core.domain.usecase.GetFavoritesUseCase;
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
public final class FavoritesViewModel_Factory implements Factory<FavoritesViewModel> {
  private final Provider<GetFavoritesUseCase> getFavoritesUseCaseProvider;

  private final Provider<ToggleBookmarkUseCase> toggleBookmarkUseCaseProvider;

  private final Provider<EventTracker> eventTrackerProvider;

  private FavoritesViewModel_Factory(Provider<GetFavoritesUseCase> getFavoritesUseCaseProvider,
      Provider<ToggleBookmarkUseCase> toggleBookmarkUseCaseProvider,
      Provider<EventTracker> eventTrackerProvider) {
    this.getFavoritesUseCaseProvider = getFavoritesUseCaseProvider;
    this.toggleBookmarkUseCaseProvider = toggleBookmarkUseCaseProvider;
    this.eventTrackerProvider = eventTrackerProvider;
  }

  @Override
  public FavoritesViewModel get() {
    return newInstance(getFavoritesUseCaseProvider.get(), toggleBookmarkUseCaseProvider.get(), eventTrackerProvider.get());
  }

  public static FavoritesViewModel_Factory create(
      Provider<GetFavoritesUseCase> getFavoritesUseCaseProvider,
      Provider<ToggleBookmarkUseCase> toggleBookmarkUseCaseProvider,
      Provider<EventTracker> eventTrackerProvider) {
    return new FavoritesViewModel_Factory(getFavoritesUseCaseProvider, toggleBookmarkUseCaseProvider, eventTrackerProvider);
  }

  public static FavoritesViewModel newInstance(GetFavoritesUseCase getFavoritesUseCase,
      ToggleBookmarkUseCase toggleBookmarkUseCase, EventTracker eventTracker) {
    return new FavoritesViewModel(getFavoritesUseCase, toggleBookmarkUseCase, eventTracker);
  }
}
