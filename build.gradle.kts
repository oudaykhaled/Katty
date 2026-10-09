plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.androidx.room) apply false
    alias(libs.plugins.androidx.baselineprofile) apply false
    alias(libs.plugins.detekt) apply false
    jacoco
}

jacoco {
    toolVersion = "0.8.15"
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    pluginManager.withPlugin("jacoco") {
        tasks.withType<Test>().configureEach {
            extensions.configure<org.gradle.testing.jacoco.plugins.JacocoTaskExtension> {
                // Robolectric defines application classes without a protection-domain location.
                // Record those classes while leaving JDK implementation classes uninstrumented.
                isIncludeNoLocationClasses = true
                excludes = listOf("jdk.internal.*")
            }
        }
    }

    extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        parallel = true
    }
}

tasks.register("allInstrumentedTests") {
    group = "verification"
    description = "Run instrumented tests only on modules that have androidTest sources"
    dependsOn(
        subprojects.filter { sub ->
            sub.file("src/androidTest").let { dir ->
                dir.exists() && dir.walkTopDown().any { it.extension == "kt" || it.extension == "java" }
            }
        }.map { "${it.path}:connectedDevDebugAndroidTest" }
    )
}

val coverageExcludedModules = setOf(":benchmark", ":core:testing")

tasks.register<JacocoReport>("jacocoCombinedReport") {
    group = "verification"
    description = "Aggregated code-coverage report across all modules"

    subprojects.filter { it.path !in coverageExcludedModules }.forEach { sub ->
        dependsOn(sub.tasks.matching { it.name == "testDevDebugUnitTest" })
    }

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/combined/html"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/combined/jacoco.xml"))
    }

    val fileFilter = listOf(
        "**/R.class", "**/R\$*.class", "**/BuildConfig.*", "**/Manifest*.*",
        "**/*_Hilt*.class", "**/Hilt_*.class", "**/*_Factory.class",
        "**/*_MembersInjector.class", "**/*Module.class", "**/*Module\$*.class",
        "**/*Injector*.class", "**/*Component*.class", "**/dagger/**",
        "**/hilt_aggregated_deps/**", "**/*_GeneratedInjector.class",
        "**/*\$\$serializer.class", "**/ComposableSingletons*.class",
        "**/*Factory\$InstanceHolder.class", "**/*\$Companion.class",
        "**/*\$*Preview*.class", "**/composepreviews/**",
        "**/*\$lambda\$*.class",
        "**/*_Impl.class", "**/*_Impl\$*.class", "**/*Database_Impl*.class",
        "**/*\$\$inlined\$*.class",
        "**/CatFactApplication.class", "**/MainActivity.class",
        "**/*Activity.class", "**/CatFactDatabase.class",
        "**/*Route*.class", "**/BottomTab.class", "**/BottomTab\$*.class"
    )

    val modules = subprojects.filter { it.path !in coverageExcludedModules }

    classDirectories.setFrom(
        modules.map { sub ->
            fileTree(sub.layout.buildDirectory) {
                include(
                    "intermediates/built_in_kotlinc/devDebug/compileDevDebugKotlin/classes/**",
                    "tmp/kotlin-classes/devDebug/**"
                )
                exclude(fileFilter)
            }
        }
    )

    sourceDirectories.setFrom(
        modules.flatMap { sub ->
            listOf(
                "${sub.projectDir}/src/main/java",
                "${sub.projectDir}/src/main/kotlin"
            )
        }
    )

    executionData.setFrom(
        modules.map { sub ->
            fileTree(sub.layout.buildDirectory) {
                include(
                    "outputs/unit_test_code_coverage/devDebugUnitTest/**/*.exec",
                    "outputs/managed_device_code_coverage/devDebug/**/*.ec",
                    "outputs/code_coverage/devDebugAndroidTest/**/*.ec"
                )
            }
        }
    )
}

tasks.register("allJvmTests") {
    group = "verification"
    description = "Run the development debug JVM tests in every application and library module"
    dependsOn(
        subprojects.filter { it.path != ":benchmark" && it.buildFile.isFile }
            .map { "${it.path}:testDevDebugUnitTest" }
    )
}

// Keep the gate scoped to application rules and the offline data merge boundary.
// UI coverage is reported separately without hiding Kotlin top-level declarations.
val criticalCoverageModules = listOf(project(":core:domain"), project(":core:database"))
val criticalCoverageClasses = criticalCoverageModules.map { module ->
    fileTree(module.layout.buildDirectory) {
        include(
            "intermediates/built_in_kotlinc/devDebug/compileDevDebugKotlin/classes/**",
            "tmp/kotlin-classes/devDebug/**"
        )
        exclude(
            "**/*_Factory.class", "**/*_MembersInjector.class", "**/hilt_aggregated_deps/**",
            "**/*_Impl.class", "**/*_Impl\$*.class", "**/*\$\$serializer.class",
            "**/*\$Companion.class", "**/di/**"
        )
    }
}
val criticalCoverageData = criticalCoverageModules.map { module ->
    fileTree(module.layout.buildDirectory) {
        include("outputs/unit_test_code_coverage/devDebugUnitTest/**/*.exec")
    }
}

tasks.register<JacocoReport>("jacocoCriticalReport") {
    group = "verification"
    description = "Report JVM coverage of domain rules and local persistence"
    dependsOn(criticalCoverageModules.map { "${it.path}:testDevDebugUnitTest" })
    classDirectories.setFrom(criticalCoverageClasses)
    sourceDirectories.setFrom(criticalCoverageModules.map { "${it.projectDir}/src/main/java" })
    executionData.setFrom(criticalCoverageData)
    reports {
        xml.required.set(true)
        html.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/critical/jacoco.xml"))
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/critical/html"))
    }
}

tasks.register<JacocoCoverageVerification>("verifyCoreCoverage") {
    group = "verification"
    description = "Require 80% line and 70% branch JVM coverage across domain and database modules"
    dependsOn("jacocoCriticalReport")
    classDirectories.setFrom(criticalCoverageClasses)
    sourceDirectories.setFrom(criticalCoverageModules.map { "${it.projectDir}/src/main/java" })
    executionData.setFrom(criticalCoverageData)
    // Fail a missing-data gate rather than allowing JaCoCo's default skip behavior.
    setOnlyIf { true }
    doFirst {
        check(executionData.files.any { it.isFile && it.length() > 0L }) {
            "Core coverage execution data is missing. Run the development debug JVM tests."
        }
        check(classDirectories.asFileTree.files.any { it.extension == "class" }) {
            "Core coverage class files are missing. Compile the development debug modules."
        }
    }
    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.70".toBigDecimal()
            }
        }
    }
}
