plugins {
    id("catfact.android.library")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.catfact.app.core.network"

    defaultConfig {
        buildConfigField("String", "VERSION_NAME", "\"${rootProject.findProperty("appVersionName") ?: "1.0.0"}\"")
    }

    productFlavors {
        getByName("dev") {
            buildConfigField("String", "BASE_URL", "\"https://catfact.ninja/\"")
        }
        getByName("prod") {
            buildConfigField("String", "BASE_URL", "\"https://catfact.ninja/\"")
        }
    }

    buildTypes {
        debug {
            buildConfigField("Boolean", "DEBUG_INTERCEPTORS", "true")
        }
        release {
            buildConfigField("Boolean", "DEBUG_INTERCEPTORS", "false")
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:domain"))

    api(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    api(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.robolectric)
}
