package com.xy.baselib.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import com.xy.baselib.ui.act.ActivityBaseStatusBar
import com.xy.baselib.ui.act.ActivityBase

abstract class FragmentBase : Fragment(), ActivityResultCallback<ActivityResult> {
    private val FRAGMENT_BASE_LAUNCH:String = "FRAGMENT:BASE:LAUNCH:";
    private val activityResultLauncherList: HashMap<String,ActivityResultLauncher<*>> by lazy { HashMap() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindLifecycleObserve()
        registerLaunch(FRAGMENT_BASE_LAUNCH,this)
    }

    fun getActivityBase(): ActivityBase? {
        if (activity is ActivityBase)
            return activity as ActivityBase;
        return null
    }

    fun getActivityBarBase(): ActivityBaseStatusBar? {
        if (activity is ActivityBaseStatusBar)
            return activity as ActivityBaseStatusBar;
        return null
    }

    /**
     * 注册跳转
     */
    protected fun registerLaunch(tag: String,launcher:ActivityResultCallback<ActivityResult>){
        activityResultLauncherList[tag] = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), launcher)
    }

    fun addLifecycleObservers(lifecycleObserver: LifecycleObserver?) {
        if (lifecycleObserver == null) return
        lifecycle.addObserver(lifecycleObserver)
    }

    /**
     * 进入历史界面
     */
    fun startActivityForResult(tag:String,intent: Intent?) {
        activityResultLauncherList[tag]?.launch(intent as Nothing?)
    }

    override fun onActivityResult(result: ActivityResult?) {}


    open fun bindLifecycleObserve() {}


}