import com.android.build.api.dsl.CommonExtension
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import io.github.takahirom.roborazzi.RoborazziExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

/**
 * Generates one screenshot test per @Preview of the module (Roborazzi on Robolectric).
 * Reference images are committed in `src/test/screenshots`.
 *
 * - Record: `./gradlew :<module>:recordRoborazziDebug`
 * - Verify: `./gradlew :<module>:verifyRoborazziDebug`
 */
class ScreenshotTestConventionPlugin : Plugin<Project> {

    @OptIn(ExperimentalRoborazziApi::class)
    override fun apply(target: Project) {
        with(target) {
            pluginManager.alias(libs.plugins.roborazzi)

            val android = extensions.getByType(CommonExtension::class.java)
            android.testOptions.unitTests.isIncludeAndroidResources = true

            extensions.configure<RoborazziExtension> {
                outputDir.set(layout.projectDirectory.dir("src/test/screenshots"))

                generateComposePreviewRobolectricTests {
                    enable.set(true)
                    packages.set(provider { listOfNotNull(android.namespace) })
                    includePrivatePreviews.set(true)
                    // Renders previews in inspection mode: local placeholder images, no network.
                    testerQualifiedClassName.set("com.example.core.screenshottesting.InspectionModePreviewTester")
                    useScanOptionParametersInTester.set(true)
                    robolectricConfig.set(
                        mapOf(
                            "sdk" to "[$ROBOLECTRIC_SDK]",
                            "qualifiers" to "RobolectricDeviceQualifiers.MediumPhone",
                        ),
                    )
                }
            }

            tasks.withType<Test>().configureEach {
                // Robolectric only runs on JUnit 4: the vintage engine runs the generated tests next to JUnit 6 tests.
                useJUnitPlatform()
                systemProperty("robolectric.pixelCopyRenderMode", "hardware")
                // Robolectric's SDK 36 shadows call into jdk.internal.access, which Java 21 doesn't export.
                jvmArgs("--add-exports=java.base/jdk.internal.access=ALL-UNNAMED")
            }

            dependencies {
                // The generated tests load the tester by its class name.
                testRuntimeOnly(project(":core:screenshot-testing"))
                testImplementation(libs.roborazzi)
                testImplementation(libs.roborazzi.compose)
                testImplementation(libs.roborazzi.compose.preview.scanner.support)
                testImplementation(libs.composable.preview.scanner)
                testImplementation(libs.robolectric)
                testImplementation(libs.junit4)
                testImplementation(platform(libs.androidx.compose.bom))
                testImplementation(libs.androidx.compose.ui.test.junit4)
                testRuntimeOnly(libs.junit.vintage.engine)
                testRuntimeOnly(libs.junit.platform.launcher)
            }
        }
    }

    private companion object {
        // Changing it re-renders every reference image. SDK 35+ needs Java 21 to run the tests.
        const val ROBOLECTRIC_SDK = 36
    }
}
