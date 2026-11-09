plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.hilt)
    alias(libs.plugins.custom.testing)
}

android {
    namespace = "com.example.order.data"

    testFixtures { enable = true }
}

dependencies {
    implementation(projects.features.order.domain)
    implementation(projects.core.model)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)

    testFixturesApi(projects.features.order.domain)
    testFixturesImplementation(projects.core.model)
    testFixturesImplementation(libs.kotlinx.coroutines.core)
}
