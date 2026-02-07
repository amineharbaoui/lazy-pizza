plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.compose)
}

android {
    namespace = "com.example.core.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.adaptive)
}
