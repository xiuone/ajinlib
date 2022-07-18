package com.xy.baselib.ui.act

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleObserver
import com.xy.baselib.notify.config.ConfigController
import com.xy.baselib.config.BaseObject
import com.xy.baselib.notify.config.ConfigListener

abstract class ActivityBase : FragmentActivity(), ActivityResultCallback<ActivityResult> ,ConfigListener{
    val ACTIVITY_BASE_LAUNCH:String = "ACTIVITY:BASE:LAUNCH:";
    private val activityResultLauncherList: HashMap<String,ActivityResultLauncher<Intent>> by lazy { HashMap() }
    protected val configController by lazy { ConfigController() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLaunch(ACTIVITY_BASE_LAUNCH,this)
        BaseObject.configNotify.addNotify(this)
    }

    fun addLifecycleObservers(lifecycleObserver: LifecycleObserver?) {
        if (lifecycleObserver == null) return
        lifecycle.addObserver(lifecycleObserver)
    }


    override fun attachBaseContext(newBase: Context) {
        val newContext = configController.attachBaseContext(newBase)
        super.attachBaseContext(newContext)
    }

    override fun onChangeConfig() {
        configController.onChangeConfig(this)
        recreate()
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

    override fun onDestroy() {
        super.onDestroy()
        BaseObject.configNotify.removeNotify(this)
    }

}