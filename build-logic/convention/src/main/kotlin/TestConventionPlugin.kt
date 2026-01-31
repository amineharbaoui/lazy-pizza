import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

class TestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            tasks.withType<Test>().configureEach {
                useJUnitPlatform()
            }

            dependencies {
                testImplementation(libs.junit.jupiter)
                testRuntimeOnly(libs.junit.jupiter.engine)
                testRuntimeOnly(libs.junit.platform.launcher)

                testImplementation(libs.mockk)
                testImplementation(libs.mockk.bdd)

                testImplementation(libs.assertj)
                testImplementation(libs.turbine)
                testImplementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}
