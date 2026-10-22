plugins {
    alias(libs.plugins.custom.kotlin.module)
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.features.menu.domain)
    implementation(projects.features.cart.domain)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
