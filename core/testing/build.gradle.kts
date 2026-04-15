plugins {
    id("catfact.android.library")
}

android {
    namespace = "com.catfact.app.core.testing"
}

dependencies {
    implementation(project(":core:domain"))

    implementation(libs.junit)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.turbine)
    implementation(libs.mockk)
    implementation(libs.hilt.android.testing)
}
