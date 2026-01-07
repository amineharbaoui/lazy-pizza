import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidModuleConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.alias(libs.plugins.android.library)
            pluginManager.alias(libs.plugins.kotlin.android)

            extensions.configure<LibraryExtension> {
                compileSdk = libs.versions.compileSdk.get().toInt()

                defaultConfig {
                    minSdk = libs.versions.minSdk.get().toInt()
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }

                configureKotlin()
            }
        }
    }
}
