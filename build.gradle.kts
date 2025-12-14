// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    id(libs.plugins.google.services.get().pluginId) version libs.versions.googleServices.get() apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.dependency.analysis)
}

subprojects {
    apply(plugin = "com.autonomousapps.dependency-analysis")
}

dependencyAnalysis {
    issues {
        all {
            onAny {
                severity("warn")
            }
        }
    }
}
