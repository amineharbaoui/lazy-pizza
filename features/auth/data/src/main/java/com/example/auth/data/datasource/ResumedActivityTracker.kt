package com.example.auth.data.datasource

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

// Firebase phone verification needs an Activity: tracking it here keeps Android types out of the domain and the ViewModel.
@Singleton
internal class ResumedActivityTracker @Inject constructor() : Application.ActivityLifecycleCallbacks {

    private var resumed: WeakReference<Activity>? = null

    val resumedActivity: Activity?
        get() = resumed?.get()

    override fun onActivityResumed(activity: Activity) {
        resumed = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        if (resumedActivity === activity) resumed = null
    }

    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?,
    ) = Unit

    override fun onActivityStarted(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle,
    ) = Unit

    override fun onActivityDestroyed(activity: Activity) = Unit
}
