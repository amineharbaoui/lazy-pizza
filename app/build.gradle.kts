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

    signingConfigs {
        create("release") {
            storeFile = file(providers.gradleProperty("LAZYPIZZA_STORE_FILE").get())
            storePassword = providers.gradleProperty("LAZYPIZZA_STORE_PASSWORD").get()
            keyAlias = providers.gradleProperty("LAZYPIZZA_KEY_ALIAS").get()
            keyPassword = providers.gradleProperty("LAZYPIZZA_KEY_PASSWORD").get()
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("release")
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
