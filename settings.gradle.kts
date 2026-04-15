pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CatFact"

includeBuild("build-logic")

include(":app")

include(":core:domain")
include(":core:database")
include(":core:network")
include(":core:designsystem")
include(":core:sync")
include(":core:logging")
include(":core:telemetry")
include(":core:testing")

include(":feature:facts")
include(":feature:favorites")
include(":feature:factdetail")

include(":benchmark")
