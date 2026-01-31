import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.alias(libs.plugins.android.application)
            pluginManager.alias(libs.plugins.kotlin.android)

            extensions.configure<ApplicationExtension> {
                compileSdk = libs.versions.compileSdk.get().toInt()

                defaultConfig {
                    targetSdk = libs.versions.targetSdk.get().toInt()
                    minSdk = libs.versions.minSdk.get().toInt()
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                configureKotlin()
            }
        }
    }
}
