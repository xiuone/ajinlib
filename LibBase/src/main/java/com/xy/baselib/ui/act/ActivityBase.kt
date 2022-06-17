package com.xy.baselib.ui.act

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleObserver

abstract class ActivityBase : FragmentActivity(), ActivityResultCallback<ActivityResult> {
    val ACTIVITY_BASE_LAUNCH:String = "ACTIVITY:BASE:LAUNCH:";
    private val activityResultLauncherList: HashMap<String,ActivityResultLauncher<Intent>> by lazy { HashMap() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLaunch(ACTIVITY_BASE_LAUNCH,this)
    }

    fun addLifecycleObservers(lifecycleObserver: LifecycleObserver?) {
        if (lifecycleObserver == null) return
        lifecycle.addObserver(lifecycleObserver)
    }


    /**
     * 注册跳转
     */
    protected fun registerLaunch(tag: String,launcher:ActivityResultCallback<ActivityResult>){
        activityResultLauncherList[tag] = registerForActivityResult(StartActivityForResult(), launcher)
    }

    /**
     * 进入历史界面
     */
    fun startActivityForResult(tag:String,intent: Intent) {
        activityResultLauncherList[tag]?.launch(intent)
    }

    fun switchFullScreen(toFull: Boolean) {
        requestedOrientation = if (toFull) { ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE } else { ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
    }

    override fun onActivityResult(result: ActivityResult?) {}

}