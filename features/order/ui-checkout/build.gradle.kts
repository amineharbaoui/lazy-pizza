plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.compose)
    alias(libs.plugins.custom.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.order.ui.checkout"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)
    implementation(projects.core.model)

    implementation(projects.features.order.domain)
    implementation(projects.features.auth.domain)
    implementation(projects.features.cart.domain)

    implementation(libs.hilt.navigation.compose)
}
