plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.hilt)
}

android {
    namespace = "com.example.menu.data"
}

dependencies {
    implementation(projects.features.menu.domain)

    implementation(projects.core.model)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)

    implementation(libs.androidx.annotation)
}
