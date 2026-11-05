plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.hilt)
}

android {
    namespace = "com.example.menu.data"

    testFixtures { enable = true }
}

dependencies {
    implementation(projects.features.menu.domain)

    implementation(projects.core.model)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)

    implementation(libs.androidx.annotation)

    testFixturesImplementation(projects.features.menu.domain)
    testFixturesImplementation(projects.core.model)
    testFixturesImplementation(libs.kotlinx.coroutines.core)
}
