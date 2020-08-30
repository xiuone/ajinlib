package com.jianbian.baselib.utils

import android.app.Activity
import android.util.Log
import java.util.*


class ActivityController {
    fun addAct(activity: Activity?) {
        if (activity == null) return
        for (act in activities) {
            if (activity.javaClass == act.javaClass) {
                return
            }
        }
        activities.add(activity)
    }

    fun removeAct(activity: Activity) {
        if (activity == null) return
        activities.remove(activity)
    }

    fun closeAct(mClass: Class<*>) {
        if (mClass == null) return
        for (activity in activities) {
            if (activity != null && activity.javaClass == mClass) {
                if (!activity.isFinishing) activity.finish()
                return
            }
        }
    }

    fun closeAllAct() {
        for (activity in activities) {
            if (activity != null) {
                if (!activity.isFinishing)
                    activity.finish()
                return
            }
        }
    }


    companion object {
        private var controller: ActivityController? = null
        private val activities: MutableList<Activity> =
            ArrayList()

        val instance: ActivityController? get() {
                if (controller == null) controller = ActivityController()
                return controller
            }
    }
}
