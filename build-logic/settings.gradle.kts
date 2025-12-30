rootProject.name = "build-logic"

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            from(files("${settingsDir.parent}/gradle/libs.versions.toml"))
        }
    }
}

include(":convention")
