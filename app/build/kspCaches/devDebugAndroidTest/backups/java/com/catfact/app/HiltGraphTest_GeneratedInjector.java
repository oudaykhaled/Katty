package com.catfact.app;

import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedEntryPoint;
import javax.annotation.processing.Generated;

@OriginatingElement(
    topLevelClass = HiltGraphTest.class
)
@GeneratedEntryPoint
@InstallIn(SingletonComponent.class)
@Generated("dagger.hilt.processor.internal.root.TestInjectorGenerator")
public interface HiltGraphTest_GeneratedInjector {
  void injectTest(HiltGraphTest hiltGraphTest);
}
