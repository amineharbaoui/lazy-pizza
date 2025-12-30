pluginManagement {
    includeBuild("build-logic")
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
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "LazyPizza"
include(":app")

include(":core:designsystem")
include(":core:ui")
include(":core:model")

include(":features:menu:data")
include(":features:menu:domain")
include(":features:menu:ui-home")
include(":features:menu:ui-pizza-detail")

include(":features:cart:data")
include(":features:cart:domain")
include(":features:cart:ui-cart")

include(":features:auth:data")
include(":features:auth:ui-login")
include(":features:auth:domain")

include(":features:order:domain")
include(":features:order:data")
include(":features:order:ui-checkout")
include(":features:order:ui-past-orders")
include(":core:common")
include(":core:network")
