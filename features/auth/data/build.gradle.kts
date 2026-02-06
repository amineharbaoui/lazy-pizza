plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.hilt)
    alias(libs.plugins.custom.testing)
}

android {
    namespace = "com.example.auth.data"
}

dependencies {
    implementation(projects.features.auth.domain)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
}
