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
include(":features:cart")
include(":features:history")

//include(":features:menu:domain")
include(":features:menu:data")
include(":features:menu:ui-home")
include(":features:menu:ui-pizza-detail")
include(":features:cart:data")
include(":features:cart:domain")
include(":features:cart:ui-cart")
include(":features:auth")
include(":features:auth:data")
//include(":features:auth:domain")
include(":features:auth:ui")
include(":features:auth:domain")
include(":features:menu:domain")
