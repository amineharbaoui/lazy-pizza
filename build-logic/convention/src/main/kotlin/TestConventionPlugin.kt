import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies

class TestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            tasks.withType(Test::class.java).configureEach {
                useJUnitPlatform()
            }

            dependencies {
                testImplementation(libs.junit.jupiter)
                testImplementation(libs.junit.jupiter.api)
                testImplementation(libs.junit.jupiter.params)
                testRuntimeOnly(libs.junit.jupiter.engine)
                testRuntimeOnly(libs.junit.platfom.lancher)

                testImplementation(libs.mockk)
                testImplementation(libs.mockk.bdd)

                testImplementation(libs.assertj)

                testImplementation(libs.turbine)

                testImplementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}
