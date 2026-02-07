plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.hilt)
}

android {
    namespace = "com.example.order.data"
}

dependencies {
    implementation(projects.features.order.domain)
    implementation(projects.core.model)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
}
