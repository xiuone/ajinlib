package com.xy.base.act

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.RectF
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleObserver
import com.xy.base.R
import com.xy.base.assembly.BaseAssemblyImpl
import com.xy.base.assembly.base.BaseAssembly
import com.xy.base.assembly.base.BaseAssemblyView
import com.xy.base.listener.OpenPageListener
import com.xy.base.utils.Logger
import com.xy.base.utils.config.ConfigController
import com.xy.base.utils.exp.getResColor
import com.xy.base.utils.exp.getViewPosRect
import com.xy.base.utils.exp.startAppActivity
import com.xy.base.utils.permission.PermissionDialogDenied
import com.xy.base.utils.permission.PermissionDialogReason
import com.xy.base.utils.permission.PermissionUiListener
import com.xy.base.utils.runMain
import com.xy.base.utils.softkey.SoftKeyBoardDetector
import kotlin.collections.HashMap

abstract class ActivityBase : FragmentActivity(), ActivityResultCallback<ActivityResult> ,OpenPageListener,
    PermissionDialogReason.ReasonUiListener,PermissionDialogDenied.DeniedUiListener,PermissionUiListener {
    protected val ACTIVITY_BASE_LAUNCH by lazy { "ACTIVITY:BASE:LAUNCH:" }
    protected val TAG by lazy { this::class.java.name }
    private val activityResultLauncherList: HashMap<String,ActivityResultLauncher<Intent>> by lazy { HashMap() }
    private val configController by lazy { ConfigController(::needReCreate) }
    private val unHindKeyView by lazy { ArrayList<View>() }
    private val assemblyList by lazy { ArrayList<BaseAssembly<*>>() }
    protected val permissionDialogReason by lazy { PermissionDialogReason(this,this) }
    protected val permissionDialogDenied by lazy { PermissionDialogDenied(this,this) }

    override fun onCreatePermissionDenied(): PermissionDialogDenied? = permissionDialogDenied

    override fun onCreatePermissionReason(): PermissionDialogReason?  = permissionDialogReason

    override fun onCreate(savedInstanceState: Bundle?) {
        onCreateBeFront()
        super.onCreate(savedInstanceState)
        configController.checkChangeConfig(this)
        registerLaunch(ACTIVITY_BASE_LAUNCH,this)
    }


    protected fun setImageTintList(imageView: ImageView?,colorList: ColorStateList){
        imageView?.run {
            ImageViewCompat.setImageTintList(this, colorList)
        }
    }

    protected open fun  addAssembly(vararg assemblyList: BaseAssembly<*>) {
        if (assemblyList.isNullOrEmpty()) return
        synchronized(this){
            for (assembly in assemblyList){
                lifecycle.addObserver(assembly)
                assembly.onCreateInit()
                this.assemblyList.add(assembly)
            }
        }
    }

    protected open fun addLifecycleObserver(vararg lifeList: BaseAssemblyImpl){
        for (lift in lifeList){
            lifecycle.addObserver(lift)
            lift.onCreate()
        }
    }

    protected open fun addAssemblyAndBuild(savedInstanceState : Bundle?,vararg assemblyList: BaseAssembly<*>) {
        if (assemblyList.isNullOrEmpty()) return
        synchronized(this){
            for (assembly in assemblyList){
                if (assembly != null) {
                    lifecycle.addObserver(assembly)
                    assembly.onCreateInit(savedInstanceState)
                    this.assemblyList.add(assembly)
                }
            }
        }

    }


    override fun attachBaseContext(newBase: Context) {
        val config = configController.changeConfig(newBase)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    protected open fun onCreateBeFront(){}

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
    override fun registerLaunch(tag: String,launcher:ActivityResultCallback<ActivityResult>){
        activityResultLauncherList[tag] = registerForActivityResult(StartActivityForResult(), launcher)
    }

    /**
     * 进入历史界面
     */
    override fun startActivityForResult(intent: Intent) = startActivityForResult(ACTIVITY_BASE_LAUNCH,intent)

    /**
     * 进入历史界面
     */
    override fun startActivityForResult(tag:String,intent: Intent) {
        runMain({
            try {
                activityResultLauncherList[tag]?.launch(intent)
            }catch (e: Exception){
                Logger.e("======${e.message}")
            }
        })
    }

    fun switchFullScreen(toFull: Boolean) {
        requestedOrientation = if (toFull) { ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE } else { ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
    }


    fun clearUnHideKeyView(){
        synchronized(this){
            this.unHindKeyView.clear()
        }
    }

    fun addUnHideKeyView(view: View?){
        if (view == null)return
        synchronized(this){
            this.unHindKeyView.add(view)
        }
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        synchronized(this){
            if (ev?.action == MotionEvent.ACTION_DOWN) {
                val currentFocus = currentFocus
                if (isShouldHideInput(currentFocus, ev)) {
                    onTouchOutKeyBoard()
                }
                return super.dispatchTouchEvent(ev)
            }
            // 必不可少，否则所有的组件都不会有TouchEvent了
            if (window.superDispatchTouchEvent(ev)) {
                return true
            }
            return onTouchEvent(ev)
        }
    }

    private fun isShouldHideInput(view:View?,event: MotionEvent?):Boolean{
        if (event != null) {
            for (unKeyView in unHindKeyView) {
                val rectF = RectF(unKeyView.getViewPosRect())
                if (rectF.contains(event.x,event.y)){
                    return false
                }
            }
            if (view != null && (view is EditText) && event != null) {
                val rectF = RectF(view.getViewPosRect())
                return !rectF.contains(event.x, event.y)
            }
        }
        return false
    }


    protected open fun onTouchOutKeyBoard(){
        SoftKeyBoardDetector.closeKeyBord(currentFocus)
    }

    override fun onActivityResult(result: ActivityResult?) {}


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        synchronized(this){
            for (assembly in assemblyList){
                assembly.onSaveInstanceState(outState)
            }
        }
    }
}