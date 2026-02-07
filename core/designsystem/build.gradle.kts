plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.compose)
}

android {
    namespace = "com.example.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.adaptive)
}
