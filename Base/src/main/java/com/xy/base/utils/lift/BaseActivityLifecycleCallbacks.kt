package com.xy.base.utils.lift

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.xy.base.utils.softkey.SoftKeyBoardDetector
import com.xy.base.utils.softkey.SoftKeyBoardDetectorHeightController

open class BaseActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks{
    var currentActivity:Activity?=null
    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        SoftKeyBoardDetectorHeightController.instant.detectKeyBord(p0)
    }

    override fun onActivityStarted(p0: Activity) {}

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityPaused(p0: Activity) {}

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(p0: Activity) {
        SoftKeyBoardDetector.unregister(p0)
    }
}