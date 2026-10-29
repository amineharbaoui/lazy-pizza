import org.gradle.initialization.DependenciesAccessors
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
    `kotlin-dsl`
}

kotlin {
    compilerOptions {
        // Roborazzi is built with Kotlin 2.3, which the Kotlin 2.0 embedded in Gradle 8.13 can't read. The Gradle 9 upgrade removes it.
        freeCompilerArgs.add("-Xskip-metadata-version-check")
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.room.gradle.plugin)
    compileOnly(libs.roborazzi.gradle.plugin)
    implementation(libs.kover)

    gradle.serviceOf<DependenciesAccessors>().classes.asFiles.forEach {
        compileOnly(files(it.absolutePath))
    }
}

gradlePlugin {
    plugins {
        register("Compose") {
            id = libs.plugins.custom.compose.get().pluginId
            implementationClass = "ComposeConventionPlugin"
        }
        register("Hilt") {
            id = libs.plugins.custom.hilt.get().pluginId
            implementationClass = "HiltConventionPlugin"
        }
        register("AndroidApplication") {
            id = libs.plugins.custom.android.application.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("AndroidModule") {
            id = libs.plugins.custom.android.module.get().pluginId
            implementationClass = "AndroidModuleConventionPlugin"
        }
        register("KotlinModule") {
            id = libs.plugins.custom.kotlin.module.get().pluginId
            implementationClass = "KotlinModuleConventionPlugin"
        }
        register("Room") {
            id = libs.plugins.custom.room.get().pluginId
            implementationClass = "RoomConventionPlugin"
        }
        register("ScreenshotTest") {
            id = libs.plugins.custom.screenshot.testing.get().pluginId
            implementationClass = "ScreenshotTestConventionPlugin"
        }
        register("Test") {
            id = libs.plugins.custom.testing.get().pluginId
            implementationClass = "TestConventionPlugin"
        }
        register("CodeCoverage") {
            id = libs.plugins.custom.code.coverage.get().pluginId
            implementationClass = "CodeCoverageConventionPlugin"
        }
    }
}
