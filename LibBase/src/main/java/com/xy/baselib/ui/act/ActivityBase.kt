package com.xy.baselib.ui.act

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleObserver
import com.xy.baselib.ConfigController
import kotlin.collections.HashMap

abstract class ActivityBase : FragmentActivity(), ActivityResultCallback<ActivityResult> {
    val ACTIVITY_BASE_LAUNCH:String = "ACTIVITY:BASE:LAUNCH:";
    private val activityResultLauncherList: HashMap<String,ActivityResultLauncher<Intent>> by lazy { HashMap() }
    private val configController by lazy { ConfigController(::needReCreate) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configController.checkChangeConfig(this)
        registerLaunch(ACTIVITY_BASE_LAUNCH,this)
    }

    fun addLifecycleObservers(lifecycleObserver: LifecycleObserver?) {
        if (lifecycleObserver == null) return
        lifecycle.addObserver(lifecycleObserver)
    }

    override fun attachBaseContext(newBase: Context) {
        val config = configController.changeConfig(newBase)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    protected open fun needReCreate(){
        recreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        configController.checkChangeConfig(this)
        super.onConfigurationChanged(newConfig)
    }

    override fun onResume() {
        super.onResume()
        configController.checkChangeConfig(this)
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