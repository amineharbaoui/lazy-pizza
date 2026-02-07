plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.hilt)
}

android {
    namespace = "com.example.core.network"
}

dependencies {
    implementation(projects.core.common)
}
