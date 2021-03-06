package com.xy.baselib.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.*


open class ActivityController : Application.ActivityLifecycleCallbacks {
    private val activities: MutableList<Activity> = ArrayList()
    private fun addAct(activity: Activity?) {
        if (activity == null) return
        for (act in activities) {
            if (activity.javaClass == act.javaClass) {
                return
            }
        }
        activities.add(activity)
    }

    private fun removeAct(activity: Activity) {
        activities.remove(activity)
    }

    fun closeAct(mClass: Class<*>) {
        for (activity in activities) {
            if (activity.javaClass == mClass) {
                if (!activity.isFinishing) {
                    activity.finish()
                    activities.remove(activity)
                    return
                }
            }
        }
    }

    fun closeAllAct() {
        for (activity in activities) {
            activity.finish()
        }
    }

    fun getActivities():MutableList<Activity>{
        return activities
    }
    

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        addAct(activity)
    }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        removeAct(activity)
    }

    companion object{
        val controller:ActivityController = ActivityController()
    }
}
