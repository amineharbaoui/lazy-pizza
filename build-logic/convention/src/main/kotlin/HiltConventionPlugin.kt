import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.alias(libs.plugins.hilt)
            pluginManager.alias(libs.plugins.ksp)

            dependencies {
                implementation(libs.hilt.android)
                ksp(libs.hilt.compiler)
            }
        }
    }
}
