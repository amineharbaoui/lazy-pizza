plugins {
    alias(libs.plugins.custom.android.module)
}

android {
    namespace = "com.example.menu"
}

dependencies {
    implementation(libs.javax.inject)
}
