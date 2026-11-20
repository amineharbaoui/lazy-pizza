plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.compose)
    alias(libs.plugins.custom.testing)
}

android {
    namespace = "com.example.core.ui"
}

dependencies {
    implementation(libs.javax.inject)

    testImplementation(projects.core.testing)
}
