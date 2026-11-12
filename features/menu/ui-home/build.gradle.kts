plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.compose)
    alias(libs.plugins.custom.hilt)
    alias(libs.plugins.custom.testing)
    alias(libs.plugins.custom.screenshot.testing)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.menu.ui.home"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)
    implementation(projects.core.model)

    implementation(projects.features.menu.domain)
    implementation(projects.features.cart.domain)

    implementation(projects.features.auth.domain)

    implementation(libs.hilt.navigation.compose)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    testImplementation(projects.core.testing)
    testImplementation(projects.features.auth.data)
    testImplementation(testFixtures(projects.features.auth.data))
    testImplementation(projects.features.cart.data)
    testImplementation(testFixtures(projects.features.cart.data))
    testImplementation(projects.features.menu.data)
    testImplementation(testFixtures(projects.features.menu.data))
}
