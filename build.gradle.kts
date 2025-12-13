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
    // Apply Dependency Analysis plugin at the root to get the aggregate buildHealth task
    alias(libs.plugins.dependency.analysis)
}

// Apply Dependency Analysis plugin to all subprojects so projectHealth/buildHealth can run
subprojects {
    // Use plugin id directly here as plugin aliases are not available outside the plugins block
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
