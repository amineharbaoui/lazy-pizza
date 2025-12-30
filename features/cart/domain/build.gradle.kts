plugins {
    alias(libs.plugins.custom.kotlin.module)
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
