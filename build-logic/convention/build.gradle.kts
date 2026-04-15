plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "catfact.android.library"
            implementationClass = "CatFactAndroidLibraryPlugin"
        }
        register("androidFeature") {
            id = "catfact.android.feature"
            implementationClass = "CatFactAndroidFeaturePlugin"
        }
    }
}
