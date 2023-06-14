package com.xy.base.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.xy.base.assembly.base.BaseAssembly
import com.xy.base.assembly.base.BaseAssemblyView
import com.xy.base.utils.config.ConfigController
import com.xy.base.act.ActivityBaseStatusBar
import com.xy.base.act.ActivityBase
import com.xy.base.assembly.BaseAssemblyImpl
import com.xy.base.listener.OpenPageListener
import com.xy.base.utils.permission.PermissionDialogDenied
import com.xy.base.utils.permission.PermissionDialogReason
import com.xy.base.utils.permission.PermissionUiListener

abstract class FragmentBase : Fragment(), ActivityResultCallback<ActivityResult>,OpenPageListener,
    PermissionDialogReason.ReasonUiListener,PermissionDialogDenied.DeniedUiListener,PermissionUiListener {
    protected val TAG by lazy { this::class.java.name }
    private val FRAGMENT_BASE_LAUNCH:String = "FRAGMENT:BASE:LAUNCH:";
    private val activityResultLauncherList: HashMap<String,ActivityResultLauncher<Intent>> by lazy { HashMap() }
    private val configController by lazy { ConfigController(::needReCreate) }
    protected val permissionDialogReason by lazy { context.run {
        if (this == null) null else PermissionDialogReason(this,this@FragmentBase)
    } }
    protected val permissionDialogDenied by lazy {
        context.run { if (this == null) null else PermissionDialogDenied(this,this@FragmentBase)
        } }
    private val assemblyList by lazy { ArrayList<BaseAssembly<*>>() }


    override fun onCreatePermissionDenied(): PermissionDialogDenied? = permissionDialogDenied

    override fun onCreatePermissionReason(): PermissionDialogReason?  = permissionDialogReason


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

    override fun onResume() {
        super.onResume()
        context?.run {
            configController.onResume(this)
        }
    }

    protected open fun needReCreate(){}


    protected fun <T: BaseAssemblyView> addAssembly(assembly: BaseAssembly<T>?) {
        if (assembly == null) return
        lifecycle.addObserver(assembly)
        assembly.onCreateInit()
        assemblyList.add(assembly)
    }


    protected fun addLifecycleObserver(vararg lifeList: BaseAssemblyImpl){
        for (lift in lifeList){
            lifecycle.addObserver(lift)
            lift.onCreate()
        }
    }

    protected fun <T: BaseAssemblyView> addAssembly(assembly: BaseAssembly<T>?,savedInstanceState : Bundle?) {
        if (assembly == null) return
        lifecycle.addObserver(assembly)
        assembly.onCreateInit(savedInstanceState)
        assemblyList.add(assembly)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        for (assembly in assemblyList){
            assembly.onSaveInstanceState(outState)
        }
    }

    /**
     * 注册跳转
     */
    override fun registerLaunch(tag: String,launcher:ActivityResultCallback<ActivityResult>){
        activityResultLauncherList[tag] = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), launcher)
    }

    /**
     * 进入历史界面
     */
    override fun startActivityForResult(intent: Intent) = startActivityForResult(FRAGMENT_BASE_LAUNCH,intent)
    /**
     * 进入历史界面
     */
    override fun startActivityForResult(tag:String,intent: Intent) {
        activityResultLauncherList[tag]?.launch(intent)
    }



    override fun onActivityResult(result: ActivityResult?) {}


    open fun bindLifecycleObserve() {}


}