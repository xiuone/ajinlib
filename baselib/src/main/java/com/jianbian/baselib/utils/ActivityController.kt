package com.jianbian.baselib.utils

import android.app.Activity
import android.util.Log
import java.util.*


object ActivityController {
    private val activities: MutableList<Activity> = ArrayList()
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
        activities.remove(activity)
    }

    fun closeAct(mClass: Class<*>) {
        for (activity in activities) {
            if (activity != null && activity.javaClass == mClass) {
                if (!activity.isFinishing) activity.finish()
                return
            }
        }
    }

    fun closeAllAct() {
        closeAct(getActivitys())
    }

    fun closeAct(data:List<Activity>){
        for (index in data.indices) {
            if (!data[index].isFinishing){
                data[index].finish()
            }
        }
    }

    fun getActivitys():MutableList<Activity>{
        return activities
    }
}
