import com.android.build.api.dsl.CommonExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.accessors.runtime.extensionOf
import org.gradle.plugin.use.PluginDependency
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

val Project.libs
    get(): LibrariesForLibs = extensionOf(this, "libs") as LibrariesForLibs

fun PluginManager.alias(notation: Provider<PluginDependency>) {
    apply(notation.get().pluginId)
}

fun DependencyHandler.implementation(notation: Any) {
    add("implementation", notation)
}

fun DependencyHandler.debugImplementation(notation: Any) {
    add("debugImplementation", notation)
}

fun DependencyHandler.testImplementation(notation: Any) {
    add("testImplementation", notation)
}

fun DependencyHandler.testRuntimeOnly(notation: Any) {
    add("testRuntimeOnly", notation)
}

fun DependencyHandler.ksp(notation: Any) {
    add("ksp", notation)
}

fun DependencyHandler.kover(notation: Any) {
    add("kover", notation)
}

fun Project.configureKotlin() {
    extensions.configure<KotlinProjectExtension>("kotlin") {
        val compilerOptions = when (this) {
            is KotlinAndroidProjectExtension -> compilerOptions
            is KotlinJvmProjectExtension -> compilerOptions
            else -> return@configure
        }

        compilerOptions.apply {
            jvmTarget.set(JvmTarget.JVM_17)
            allWarningsAsErrors.set(false)
            freeCompilerArgs.addAll(
                "-Xannotation-default-target=param-property",
            )
        }
    }
}

typealias AndroidBaseExtension = CommonExtension<*, *, *, *, *, *>
