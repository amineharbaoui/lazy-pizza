plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.compose)
    alias(libs.plugins.custom.hilt)
    alias(libs.plugins.custom.testing)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.order.checkout.ui.pastorders"

    testOptions.unitTests.isIncludeAndroidResources = true
}

roborazzi {
    outputDir.set(file("src/test/screenshots"))
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)
    implementation(projects.core.model)

    implementation(projects.features.order.domain)
    implementation(projects.features.menu.domain)
    implementation(projects.features.cart.domain)
    implementation(projects.features.auth.domain)

    implementation(libs.hilt.navigation.compose)

    testImplementation(projects.core.testing)
    testImplementation(libs.junit4)
    testRuntimeOnly(libs.junit.vintage.engine)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation(projects.features.menu.data)
    testImplementation(projects.features.cart.data)
    testImplementation(projects.features.order.data)
    testImplementation(projects.features.auth.data)
    testImplementation(testFixtures(projects.features.menu.data))
    testImplementation(testFixtures(projects.features.cart.data))
    testImplementation(testFixtures(projects.features.order.data))
    testImplementation(testFixtures(projects.features.auth.data))
}
