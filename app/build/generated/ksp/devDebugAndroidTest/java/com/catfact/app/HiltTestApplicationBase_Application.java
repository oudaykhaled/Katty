package com.catfact.app;

import dagger.hilt.android.internal.testing.TestApplicationComponentManager;
import dagger.hilt.android.internal.testing.TestApplicationComponentManagerHolder;
import dagger.hilt.internal.GeneratedComponentManager;
import java.lang.Object;
import java.lang.Override;
import javax.annotation.processing.Generated;

@Generated("dagger.hilt.android.processor.internal.customtestapplication.CustomTestApplicationProcessor")
public final class HiltTestApplicationBase_Application extends BaseApplication implements GeneratedComponentManager<Object>, TestApplicationComponentManagerHolder {
  private final Object componentManagerLock = new Object();

  private volatile TestApplicationComponentManager componentManager;

  @Override
  public final GeneratedComponentManager<Object> componentManager() {
    if (componentManager == null) {
      synchronized (componentManagerLock) {
        if (componentManager == null) {
          componentManager = new TestApplicationComponentManager(this);
        }
      }
    }
    return componentManager;
  }

  @Override
  public final Object generatedComponent() {
    return componentManager().generatedComponent();
  }
}
