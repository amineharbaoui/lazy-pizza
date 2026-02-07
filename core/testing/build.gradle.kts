plugins {
    alias(libs.plugins.custom.android.module)
}

android {
    namespace = "com.example.core.testing"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(libs.junit.jupiter)
    implementation(libs.kotlinx.coroutines.test)
}
