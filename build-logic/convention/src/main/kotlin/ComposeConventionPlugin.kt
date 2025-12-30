import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.alias(libs.plugins.kotlin.compose)

            extensions.findByType(ApplicationExtension::class.java)?.let(::configureCompose)
            extensions.findByType(LibraryExtension::class.java)?.let(::configureCompose)

            dependencies {
                implementation(platform(libs.androidx.compose.bom))
                implementation(libs.bundles.compose.core)
                implementation(libs.androidx.compose.ui.tooling.preview)
                debugImplementation(libs.androidx.compose.ui.tooling)
            }
        }
    }

    private fun configureCompose(extensions: AndroidBaseExtension) {
        extensions.apply {
            buildFeatures.compose = true
        }
    }
}
