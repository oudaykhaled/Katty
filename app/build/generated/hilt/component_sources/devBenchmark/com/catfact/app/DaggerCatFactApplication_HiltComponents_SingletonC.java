package com.catfact.app;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.WorkManager;
import com.catfact.app.core.database.CatFactDatabase;
import com.catfact.app.core.database.dao.CatFactDao;
import com.catfact.app.core.database.di.DatabaseModule_ProvideCatFactDaoFactory;
import com.catfact.app.core.database.di.DatabaseModule_ProvideDatabaseFactory;
import com.catfact.app.core.database.repository.CatFactRepositoryImpl;
import com.catfact.app.core.domain.usecase.FetchRandomFactUseCase;
import com.catfact.app.core.domain.usecase.GetFactsUseCase;
import com.catfact.app.core.domain.usecase.GetFavoritesUseCase;
import com.catfact.app.core.domain.usecase.RefreshFactsUseCase;
import com.catfact.app.core.domain.usecase.SaveNoteUseCase;
import com.catfact.app.core.domain.usecase.ToggleBookmarkUseCase;
import com.catfact.app.core.logging.NoOpLogger;
import com.catfact.app.core.network.api.CatFactApi;
import com.catfact.app.core.network.di.InterceptorModule_ProvideCommonHeadersInterceptorEntryFactory;
import com.catfact.app.core.network.di.InterceptorModule_ProvideRetryInterceptorEntryFactory;
import com.catfact.app.core.network.di.NetworkModule_ProvideCatFactApiFactory;
import com.catfact.app.core.network.di.NetworkModule_ProvideJsonFactory;
import com.catfact.app.core.network.di.NetworkModule_ProvideOkHttpClientFactory;
import com.catfact.app.core.network.di.NetworkModule_ProvideRetrofitFactory;
import com.catfact.app.core.network.interceptor.CommonHeadersInterceptor;
import com.catfact.app.core.network.interceptor.InterceptorEntry;
import com.catfact.app.core.network.interceptor.RetryInterceptor;
import com.catfact.app.core.sync.di.SyncModule_ProvideWorkManagerFactory;
import com.catfact.app.core.sync.manager.CatFactSyncManager;
import com.catfact.app.core.telemetry.NoOpEventTracker;
import com.catfact.app.feature.factdetail.viewmodel.DetailViewModel;
import com.catfact.app.feature.factdetail.viewmodel.DetailViewModel_HiltModules;
import com.catfact.app.feature.factdetail.viewmodel.DetailViewModel_HiltModules_BindsModule_Bind_LazyMapKey;
import com.catfact.app.feature.factdetail.viewmodel.DetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.catfact.app.feature.facts.viewmodel.FactsViewModel;
import com.catfact.app.feature.facts.viewmodel.FactsViewModel_HiltModules;
import com.catfact.app.feature.facts.viewmodel.FactsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.catfact.app.feature.facts.viewmodel.FactsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.catfact.app.feature.favorites.viewmodel.FavoritesViewModel;
import com.catfact.app.feature.favorites.viewmodel.FavoritesViewModel_HiltModules;
import com.catfact.app.feature.favorites.viewmodel.FavoritesViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.catfact.app.feature.favorites.viewmodel.FavoritesViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SetBuilder;
import dagger.internal.SingleCheck;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import kotlinx.serialization.json.Json;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

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
public final class DaggerCatFactApplication_HiltComponents_SingletonC {
  private DaggerCatFactApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public CatFactApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements CatFactApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public CatFactApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements CatFactApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public CatFactApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements CatFactApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public CatFactApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements CatFactApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public CatFactApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements CatFactApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public CatFactApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements CatFactApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public CatFactApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements CatFactApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public CatFactApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends CatFactApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends CatFactApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    FragmentCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends CatFactApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends CatFactApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    ActivityCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    Map keySetMapOfClassOfAndBooleanBuilder() {
      MapBuilder mapBuilder = MapBuilder.<String, Boolean>newMapBuilder(3);
      mapBuilder.put(DetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, DetailViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(FactsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, FactsViewModel_HiltModules.KeyModule.provide());
      mapBuilder.put(FavoritesViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, FavoritesViewModel_HiltModules.KeyModule.provide());
      return mapBuilder.build();
    }

    @Override
    public void injectMainActivity(MainActivity arg0) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(keySetMapOfClassOfAndBooleanBuilder());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends CatFactApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    Provider<FactsViewModel> factsViewModelProvider;

    Provider<FavoritesViewModel> favoritesViewModelProvider;

    Provider<DetailViewModel.Factory> factoryProvider;

    ViewModelCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        SavedStateHandle savedStateHandleParam, ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    GetFactsUseCase getFactsUseCase() {
      return new GetFactsUseCase(singletonCImpl.catFactRepositoryImplProvider.get());
    }

    FetchRandomFactUseCase fetchRandomFactUseCase() {
      return new FetchRandomFactUseCase(singletonCImpl.catFactRepositoryImplProvider.get());
    }

    ToggleBookmarkUseCase toggleBookmarkUseCase() {
      return new ToggleBookmarkUseCase(singletonCImpl.catFactRepositoryImplProvider.get());
    }

    RefreshFactsUseCase refreshFactsUseCase() {
      return new RefreshFactsUseCase(singletonCImpl.catFactRepositoryImplProvider.get());
    }

    GetFavoritesUseCase getFavoritesUseCase() {
      return new GetFavoritesUseCase(singletonCImpl.catFactRepositoryImplProvider.get());
    }

    Map hiltViewModelMapMapOfClassOfAndProviderOfViewModelBuilder() {
      MapBuilder mapBuilder = MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(2);
      mapBuilder.put(FactsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (factsViewModelProvider)));
      mapBuilder.put(FavoritesViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (favoritesViewModelProvider)));
      return mapBuilder.build();
    }

    SaveNoteUseCase saveNoteUseCase() {
      return new SaveNoteUseCase(singletonCImpl.catFactRepositoryImplProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.factsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.favoritesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.factoryProvider = SingleCheck.provider(new SwitchingProvider<DetailViewModel.Factory>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2));
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(hiltViewModelMapMapOfClassOfAndProviderOfViewModelBuilder());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return LazyClassKeyMap.<Object>of(Collections.<String, Object>singletonMap(DetailViewModel_HiltModules_BindsModule_Bind_LazyMapKey.lazyClassKeyName, factoryProvider.get()));
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.catfact.app.feature.facts.viewmodel.FactsViewModel
          return (T) new FactsViewModel(viewModelCImpl.getFactsUseCase(), viewModelCImpl.fetchRandomFactUseCase(), viewModelCImpl.toggleBookmarkUseCase(), viewModelCImpl.refreshFactsUseCase(), singletonCImpl.noOpEventTrackerProvider.get());

          case 1: // com.catfact.app.feature.favorites.viewmodel.FavoritesViewModel
          return (T) new FavoritesViewModel(viewModelCImpl.getFavoritesUseCase(), viewModelCImpl.toggleBookmarkUseCase(), singletonCImpl.noOpEventTrackerProvider.get());

          case 2: // com.catfact.app.feature.factdetail.viewmodel.DetailViewModel.Factory
          return (T) new DetailViewModel.Factory() {
            @Override
            public DetailViewModel create(String factId) {
              return new DetailViewModel(viewModelCImpl.savedStateHandle, factId, singletonCImpl.catFactRepositoryImplProvider.get(), viewModelCImpl.toggleBookmarkUseCase(), viewModelCImpl.saveNoteUseCase(), singletonCImpl.noOpEventTrackerProvider.get());
            }
          };

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends CatFactApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends CatFactApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends CatFactApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    Provider<WorkManager> provideWorkManagerProvider;

    Provider<CatFactSyncManager> catFactSyncManagerProvider;

    Provider<CatFactDatabase> provideDatabaseProvider;

    Provider<CatFactDao> provideCatFactDaoProvider;

    Provider<RetryInterceptor> retryInterceptorProvider;

    Provider<InterceptorEntry> provideRetryInterceptorEntryProvider;

    Provider<CommonHeadersInterceptor> commonHeadersInterceptorProvider;

    Provider<InterceptorEntry> provideCommonHeadersInterceptorEntryProvider;

    Provider<OkHttpClient> provideOkHttpClientProvider;

    Provider<Json> provideJsonProvider;

    Provider<Retrofit> provideRetrofitProvider;

    Provider<CatFactApi> provideCatFactApiProvider;

    Provider<NoOpLogger> noOpLoggerProvider;

    Provider<CatFactRepositoryImpl> catFactRepositoryImplProvider;

    Provider<NoOpEventTracker> noOpEventTrackerProvider;

    SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    Set setOfInterceptorEntryBuilder() {
      SetBuilder setBuilder = SetBuilder.<InterceptorEntry>newSetBuilder(2);
      setBuilder.add(provideRetryInterceptorEntryProvider.get());
      setBuilder.add(provideCommonHeadersInterceptorEntryProvider.get());
      return setBuilder.build();
    }

    Set<InterceptorEntry> setOfInterceptorEntry() {
      return setOfInterceptorEntryBuilder();
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideWorkManagerProvider = DoubleCheck.provider(new SwitchingProvider<WorkManager>(singletonCImpl, 1));
      this.catFactSyncManagerProvider = DoubleCheck.provider(new SwitchingProvider<CatFactSyncManager>(singletonCImpl, 0));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<CatFactDatabase>(singletonCImpl, 4));
      this.provideCatFactDaoProvider = DoubleCheck.provider(new SwitchingProvider<CatFactDao>(singletonCImpl, 3));
      this.retryInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<RetryInterceptor>(singletonCImpl, 9));
      this.provideRetryInterceptorEntryProvider = DoubleCheck.provider(new SwitchingProvider<InterceptorEntry>(singletonCImpl, 8));
      this.commonHeadersInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<CommonHeadersInterceptor>(singletonCImpl, 11));
      this.provideCommonHeadersInterceptorEntryProvider = DoubleCheck.provider(new SwitchingProvider<InterceptorEntry>(singletonCImpl, 10));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 7));
      this.provideJsonProvider = DoubleCheck.provider(new SwitchingProvider<Json>(singletonCImpl, 12));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 6));
      this.provideCatFactApiProvider = DoubleCheck.provider(new SwitchingProvider<CatFactApi>(singletonCImpl, 5));
      this.noOpLoggerProvider = DoubleCheck.provider(new SwitchingProvider<NoOpLogger>(singletonCImpl, 13));
      this.catFactRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<CatFactRepositoryImpl>(singletonCImpl, 2));
      this.noOpEventTrackerProvider = DoubleCheck.provider(new SwitchingProvider<NoOpEventTracker>(singletonCImpl, 14));
    }

    @Override
    public void injectCatFactApplication(CatFactApplication arg0) {
      injectCatFactApplication2(arg0);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private CatFactApplication injectCatFactApplication2(CatFactApplication instance) {
      CatFactApplication_MembersInjector.injectSyncManager(instance, catFactSyncManagerProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @Override
      @SuppressWarnings("unchecked")
      public T get() {
        switch (id) {
          case 0: // com.catfact.app.core.sync.manager.CatFactSyncManager
          return (T) new CatFactSyncManager(singletonCImpl.provideWorkManagerProvider.get());

          case 1: // androidx.work.WorkManager
          return (T) SyncModule_ProvideWorkManagerFactory.provideWorkManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.catfact.app.core.database.repository.CatFactRepositoryImpl
          return (T) new CatFactRepositoryImpl(singletonCImpl.provideCatFactDaoProvider.get(), singletonCImpl.provideCatFactApiProvider.get(), singletonCImpl.noOpLoggerProvider.get());

          case 3: // com.catfact.app.core.database.dao.CatFactDao
          return (T) DatabaseModule_ProvideCatFactDaoFactory.provideCatFactDao(singletonCImpl.provideDatabaseProvider.get());

          case 4: // com.catfact.app.core.database.CatFactDatabase
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.catfact.app.core.network.api.CatFactApi
          return (T) NetworkModule_ProvideCatFactApiFactory.provideCatFactApi(singletonCImpl.provideRetrofitProvider.get());

          case 6: // retrofit2.Retrofit
          return (T) NetworkModule_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.provideOkHttpClientProvider.get(), singletonCImpl.provideJsonProvider.get());

          case 7: // okhttp3.OkHttpClient
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient(singletonCImpl.setOfInterceptorEntry());

          case 8: // java.util.Set<com.catfact.app.core.network.interceptor.InterceptorEntry> com.catfact.app.core.network.di.InterceptorModule#provideRetryInterceptorEntry
          return (T) InterceptorModule_ProvideRetryInterceptorEntryFactory.provideRetryInterceptorEntry(singletonCImpl.retryInterceptorProvider.get());

          case 9: // com.catfact.app.core.network.interceptor.RetryInterceptor
          return (T) new RetryInterceptor();

          case 10: // java.util.Set<com.catfact.app.core.network.interceptor.InterceptorEntry> com.catfact.app.core.network.di.InterceptorModule#provideCommonHeadersInterceptorEntry
          return (T) InterceptorModule_ProvideCommonHeadersInterceptorEntryFactory.provideCommonHeadersInterceptorEntry(singletonCImpl.commonHeadersInterceptorProvider.get());

          case 11: // com.catfact.app.core.network.interceptor.CommonHeadersInterceptor
          return (T) new CommonHeadersInterceptor();

          case 12: // kotlinx.serialization.json.Json
          return (T) NetworkModule_ProvideJsonFactory.provideJson();

          case 13: // com.catfact.app.core.logging.NoOpLogger
          return (T) new NoOpLogger();

          case 14: // com.catfact.app.core.telemetry.NoOpEventTracker
          return (T) new NoOpEventTracker();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
