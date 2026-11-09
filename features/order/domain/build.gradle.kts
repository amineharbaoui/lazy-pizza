plugins {
    alias(libs.plugins.custom.kotlin.module)
    alias(libs.plugins.custom.testing)
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

    testImplementation(libs.junit.params)
}
