import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.custom.code.coverage)
    alias(libs.plugins.ktlint)
}

subprojects {
    apply(plugin = "com.autonomousapps.dependency-analysis")
    apply(plugin = rootProject.libs.plugins.ktlint.get().pluginId)

    ktlint {
        reporters {
            reporter(ReporterType.CHECKSTYLE)
        }
    }
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

val unitTestAll = tasks.register("unitTestAll") {
    group = "verification"
    description = "Runs all unit tests across all modules (JVM :test + Android :testDebugUnitTest)."
}

subprojects {
    plugins.withId(rootProject.libs.plugins.android.application.get().pluginId) {
        unitTestAll.configure { dependsOn(tasks.named("testDebugUnitTest")) }
    }
    plugins.withId(rootProject.libs.plugins.android.library.get().pluginId) {
        unitTestAll.configure { dependsOn(tasks.named("testDebugUnitTest")) }
    }
    plugins.withId(rootProject.libs.plugins.jetbrains.kotlin.jvm.get().pluginId) {
        unitTestAll.configure { dependsOn(tasks.named("test")) }
    }
}
