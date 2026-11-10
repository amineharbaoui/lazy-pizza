plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.compose)
    alias(libs.plugins.custom.hilt)
    alias(libs.plugins.custom.testing)
    alias(libs.plugins.custom.screenshot.testing)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.order.ui.pastorders"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)
    implementation(projects.core.model)

    implementation(projects.features.order.domain)
    implementation(projects.features.auth.domain)

    implementation(libs.hilt.navigation.compose)

    testImplementation(projects.core.testing)
    testImplementation(projects.features.auth.data)
    testImplementation(testFixtures(projects.features.auth.data))
    testImplementation(projects.features.order.data)
    testImplementation(testFixtures(projects.features.order.data))
}
