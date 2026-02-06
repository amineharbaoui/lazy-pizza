import org.gradle.initialization.DependenciesAccessors
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.room.gradle.plugin)
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
