plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.compose)
    alias(libs.plugins.custom.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.custom.testing)
}

android {
    namespace = "com.example.auth.ui.login"
}

dependencies {
    implementation(projects.core.designsystem)

    implementation(projects.features.auth.domain)
    implementation(projects.features.cart.domain)

    implementation(libs.hilt.navigation.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    testImplementation(projects.core.testing)
}
