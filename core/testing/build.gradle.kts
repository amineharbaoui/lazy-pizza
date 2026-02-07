plugins {
    alias(libs.plugins.custom.android.module)
}

android {
    namespace = "com.example.core.testing"
}

dependencies {
    implementation(libs.junit.jupiter)
    implementation(libs.kotlinx.coroutines.test)
}
