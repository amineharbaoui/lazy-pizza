plugins {
    alias(libs.plugins.custom.android.module)
    alias(libs.plugins.custom.hilt)
    alias(libs.plugins.custom.room)
}

android {
    namespace = "com.example.cart.data"

    buildFeatures { buildConfig = true }

    buildTypes {
        debug {
            buildConfigField("long", "CART_TTL_SECONDS", "1800")
            buildConfigField("long", "CART_TOUCH_THROTTLE_SECONDS", "60")
        }
        release {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            buildConfigField("long", "CART_TTL_SECONDS", "60")
            buildConfigField("long", "CART_TOUCH_THROTTLE_SECONDS", "60")
        }
    }
}

dependencies {
    implementation(projects.features.cart.domain)
    implementation(projects.features.auth.domain)

    implementation(projects.core.model)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
}
