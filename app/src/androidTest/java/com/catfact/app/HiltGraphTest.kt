package com.catfact.app

import com.catfact.app.core.domain.repository.CatFactRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class HiltGraphTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: CatFactRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun hiltDependencyGraphResolves() {
        assertNotNull(repository)
    }
}
