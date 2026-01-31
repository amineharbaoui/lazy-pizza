plugins {
    alias(libs.plugins.custom.kotlin.module)
    alias(libs.plugins.custom.testing)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
