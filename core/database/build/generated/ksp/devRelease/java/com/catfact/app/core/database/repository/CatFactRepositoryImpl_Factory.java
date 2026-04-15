package com.catfact.app.core.database.repository;

import com.catfact.app.core.database.dao.CatFactDao;
import com.catfact.app.core.logging.Logger;
import com.catfact.app.core.network.api.CatFactApi;
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
public final class CatFactRepositoryImpl_Factory implements Factory<CatFactRepositoryImpl> {
  private final Provider<CatFactDao> catFactDaoProvider;

  private final Provider<CatFactApi> catFactApiProvider;

  private final Provider<Logger> loggerProvider;

  private CatFactRepositoryImpl_Factory(Provider<CatFactDao> catFactDaoProvider,
      Provider<CatFactApi> catFactApiProvider, Provider<Logger> loggerProvider) {
    this.catFactDaoProvider = catFactDaoProvider;
    this.catFactApiProvider = catFactApiProvider;
    this.loggerProvider = loggerProvider;
  }

  @Override
  public CatFactRepositoryImpl get() {
    return newInstance(catFactDaoProvider.get(), catFactApiProvider.get(), loggerProvider.get());
  }

  public static CatFactRepositoryImpl_Factory create(Provider<CatFactDao> catFactDaoProvider,
      Provider<CatFactApi> catFactApiProvider, Provider<Logger> loggerProvider) {
    return new CatFactRepositoryImpl_Factory(catFactDaoProvider, catFactApiProvider, loggerProvider);
  }

  public static CatFactRepositoryImpl newInstance(CatFactDao catFactDao, CatFactApi catFactApi,
      Logger logger) {
    return new CatFactRepositoryImpl(catFactDao, catFactApi, logger);
  }
}
