plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.hilt)
}

android {
    namespace = "com.example.menu.data"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(projects.features.menu.domain)

    implementation(projects.core.model)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
}
