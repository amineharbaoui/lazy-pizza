plugins {
    alias(libs.plugins.custom.android.application)
    alias(libs.plugins.custom.compose)
    alias(libs.plugins.custom.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.example.lazypizza"

    defaultConfig {
        applicationId = "com.example.lazypizza"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {

    implementation(projects.core.designsystem)
    implementation(projects.core.ui)

    implementation(projects.features.menu.uiHome)
    implementation(projects.features.menu.uiPizzaDetail)
    implementation(projects.features.menu.data)

    implementation(projects.features.cart.uiCart)
    implementation(projects.features.cart.domain)
    implementation(projects.features.cart.data)

    implementation(projects.features.order.uiCheckout)
    implementation(projects.features.order.data)

    implementation(projects.features.order.uiPastOrders)

    implementation(projects.features.auth.uiLogin)
    implementation(projects.features.auth.data)

    implementation(projects.core.common)
    implementation(projects.core.network)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)

    implementation(libs.hilt.navigation.compose)

    implementation(libs.coil.network.okhttp)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
}
