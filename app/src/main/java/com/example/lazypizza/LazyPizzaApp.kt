package com.example.lazypizza

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LazyPizzaApp : Application() {

    @Inject
    lateinit var activityLifecycleCallbacks: Set<@JvmSuppressWildcards ActivityLifecycleCallbacks>

    override fun onCreate() {
        super.onCreate()
        activityLifecycleCallbacks.forEach(::registerActivityLifecycleCallbacks)
    }
}
