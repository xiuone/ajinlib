package com.xy.baselib.life

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.xy.baselib.config.BaseObject
import com.xy.baselib.softkey.SoftKeyBoardDetector
import com.xy.baselib.softkey.SoftKeyBoardDetectorHeightController

open class BaseActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks{
    var currentActivity:Activity?=null
    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        BaseObject.keyHeightController.detectKeyBord(p0)
    }

    override fun onActivityStarted(p0: Activity) {}

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0;
    }

    override fun onActivityPaused(p0: Activity) {}

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(p0: Activity) {
        SoftKeyBoardDetector.unregister(p0)
    }
}