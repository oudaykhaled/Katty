package com.catfact.app.core.database.di;

import com.catfact.app.core.database.CatFactDatabase;
import com.catfact.app.core.database.dao.CatFactDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideCatFactDaoFactory implements Factory<CatFactDao> {
  private final Provider<CatFactDatabase> databaseProvider;

  private DatabaseModule_ProvideCatFactDaoFactory(Provider<CatFactDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public CatFactDao get() {
    return provideCatFactDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideCatFactDaoFactory create(
      Provider<CatFactDatabase> databaseProvider) {
    return new DatabaseModule_ProvideCatFactDaoFactory(databaseProvider);
  }

  public static CatFactDao provideCatFactDao(CatFactDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCatFactDao(database));
  }
}
