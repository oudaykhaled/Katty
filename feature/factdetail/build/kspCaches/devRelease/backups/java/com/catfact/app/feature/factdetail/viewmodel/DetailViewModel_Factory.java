package com.catfact.app.feature.factdetail.viewmodel;

import androidx.lifecycle.SavedStateHandle;
import com.catfact.app.core.domain.repository.CatFactRepository;
import com.catfact.app.core.domain.usecase.SaveNoteUseCase;
import com.catfact.app.core.domain.usecase.ToggleBookmarkUseCase;
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
public final class DetailViewModel_Factory {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<CatFactRepository> repositoryProvider;

  private final Provider<ToggleBookmarkUseCase> toggleBookmarkUseCaseProvider;

  private final Provider<SaveNoteUseCase> saveNoteUseCaseProvider;

  private final Provider<EventTracker> eventTrackerProvider;

  private DetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<CatFactRepository> repositoryProvider,
      Provider<ToggleBookmarkUseCase> toggleBookmarkUseCaseProvider,
      Provider<SaveNoteUseCase> saveNoteUseCaseProvider,
      Provider<EventTracker> eventTrackerProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.repositoryProvider = repositoryProvider;
    this.toggleBookmarkUseCaseProvider = toggleBookmarkUseCaseProvider;
    this.saveNoteUseCaseProvider = saveNoteUseCaseProvider;
    this.eventTrackerProvider = eventTrackerProvider;
  }

  public DetailViewModel get(String factId) {
    return newInstance(savedStateHandleProvider.get(), factId, repositoryProvider.get(), toggleBookmarkUseCaseProvider.get(), saveNoteUseCaseProvider.get(), eventTrackerProvider.get());
  }

  public static DetailViewModel_Factory create(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<CatFactRepository> repositoryProvider,
      Provider<ToggleBookmarkUseCase> toggleBookmarkUseCaseProvider,
      Provider<SaveNoteUseCase> saveNoteUseCaseProvider,
      Provider<EventTracker> eventTrackerProvider) {
    return new DetailViewModel_Factory(savedStateHandleProvider, repositoryProvider, toggleBookmarkUseCaseProvider, saveNoteUseCaseProvider, eventTrackerProvider);
  }

  public static DetailViewModel newInstance(SavedStateHandle savedStateHandle, String factId,
      CatFactRepository repository, ToggleBookmarkUseCase toggleBookmarkUseCase,
      SaveNoteUseCase saveNoteUseCase, EventTracker eventTracker) {
    return new DetailViewModel(savedStateHandle, factId, repository, toggleBookmarkUseCase, saveNoteUseCase, eventTracker);
  }
}
