plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.compose)
    alias(libs.plugins.custom.screenshot.testing)
}

android {
    namespace = "com.example.core.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.adaptive)
}
