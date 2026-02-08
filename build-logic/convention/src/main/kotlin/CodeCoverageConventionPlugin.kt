import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class CodeCoverageConventionPlugin : Plugin<Project> {

    private val excludedModules = setOf(
        ":app",
        ":core",
        ":features",
        ":core:common",
        ":core:designsystem",
        ":core:model",
        ":core:network",
        ":core:testing",
        ":core:ui",
    )

    override fun apply(target: Project) = with(target) {
        check(this == rootProject) {
            "CodeCoverageConventionPlugin must be applied only to the root project."
        }

        val coveredModules = subprojects.filterNot { it.path in excludedModules }

        pluginManager.alias(libs.plugins.kover)

        coveredModules.forEach { subProject ->
            subProject.pluginManager.alias(this@with.libs.plugins.kover)
        }

        extensions.getByType<KoverProjectExtension>().apply {
            reports {
                filters {
                    excludes {
                        // Compose generated
                        classes(
                            "*Preview*",
                            "ComposableSingletons*",
                        )
                        annotatedBy(
                            "androidx.compose.runtime.Composable",
                        )

                        // Android generated
                        classes(
                            "*BuildConfig*",
                            "*R",
                            "*R$*",
                            "*Manifest*",
                            "*Binding*",
                        )

                        // DI / Hilt / Dagger generated
                        packages(
                            "dagger.hilt.internal.aggregatedroot.codegen",
                            "hilt_aggregated_deps",
                        )
                        annotatedBy(
                            "dagger.Module",
                            "dagger.hilt.InstallIn",
                            "javax.annotation.processing.Generated",
                            "dagger.internal.DaggerGenerated",
                            "dagger.hilt.android.internal.lifecycle.HiltViewModelMap\$KeySet",
                        )
                        classes(
                            "*\$InstanceHolder",
                            "*Hilt_*",
                            "*BindsModule*",
                            "*\$DefaultImpls",
                        )

                        // DB: Room
                        packages(
                            "**.data.datasource.db.**",
                            "**.dao.**",
                            "**.entity.**",
                        )
                        classes(
                            "*_Impl*",
                        )

                        // Tests / fakes / mocks
                        classes(
                            "*Test",
                            "*Tests",
                            "*Fake*",
                            "*Mock*",
                        )
                    }
                }
            }
        }

        dependencies {
            coveredModules.forEach { subProject ->
                kover(project(subProject.path))
            }
        }
    }
}
