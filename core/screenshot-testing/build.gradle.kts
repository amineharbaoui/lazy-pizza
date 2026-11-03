plugins {
    alias(libs.plugins.custom.android.module)
}

android {
    namespace = "com.example.core.screenshottesting"
}

dependencies {
    api(libs.roborazzi.compose.preview.scanner.support)
    implementation(libs.roborazzi.compose)
    implementation(libs.roborazzi.core)
    implementation(libs.differ)
}
