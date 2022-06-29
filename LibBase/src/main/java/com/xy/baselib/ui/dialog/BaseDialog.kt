package com.xy.baselib.ui.dialog

import android.R
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import com.gyf.immersionbar.ImmersionBar
import com.xy.baselib.MoveKeyBoardController
import com.xy.baselib.exp.getScreenWidth
import com.xy.baselib.softkey.SoftKeyBoardDetector

abstract class BaseDialog(context: Context) : Dialog(context) , DialogInterface.OnDismissListener{
    protected var rootView:View?=null
    private val disListenerList by lazy { ArrayList<DialogInterface.OnDismissListener>() }
    private var activity: Activity?=null
    protected val moveKeyBoardController: MoveKeyBoardController by lazy { MoveKeyBoardController(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.decorView?.setPadding(15, 0, 15, 0)
        val lp = window?.attributes
        lp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        val width = WindowManager.LayoutParams.FILL_PARENT
        val proportion: Double = proportion()
        if (proportion != 0.0)
            lp?.width = ((context.getScreenWidth() * proportion).toInt())
        else
            lp?.width = width
        window?.attributes = lp
        window?.setBackgroundDrawableResource(R.color.transparent)
        window?.setGravity(gravity())
        setContent()
        initView()
    }

    open fun setContent(){
        val view = LayoutInflater.from(context).inflate(layoutRes(), null)
        setContentView(view)
        super.setOnDismissListener(this)
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        rootView = view
    }

    override fun show() {
        super.show()
        showAnimation(rootView)
    }

    fun showBindActivity(activity: Activity){
        show()
        this.activity = activity
    }

    fun bindKeyBoardShow(activity :Activity){
        showBindActivity(activity)
        if (registerKeyBoard()) {
            SoftKeyBoardDetector.register(activity, moveKeyBoardController)
            moveKeyBoardController.keyBoardHide(context, 0)
        }
        if (useImmersionBar()) {
           immersionBar(activity)
        }
    }

    fun immersionBar(activity: Activity){
        val keyboardMode =
            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        ImmersionBar.with(activity, this)
            .supportActionBar(false)
            .navigationBarEnable(false)
            .statusBarDarkFont(true)
            .transparentBar()
            .statusBarView(getBarView())
            .keyboardEnable(true, keyboardMode)
            .init()
    }


    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        synchronized(this){
            listener?.run {
                disListenerList.add(this)
            }
        }
    }

    /**
     * 显示动画
     */
    override fun onDismiss(p0: DialogInterface?) {
        synchronized(this){
            for (listener in disListenerList){
                listener.onDismiss(p0)
            }
        }
        val currentActivity = activity
        if (useImmersionBar() && currentActivity != null) {
            ImmersionBar.destroy(currentActivity, this)
        }
        if (registerKeyBoard() && currentActivity != null)
            SoftKeyBoardDetector.removeListener(currentActivity,moveKeyBoardController)
    }


    abstract fun showAnimation(view: View?)


    abstract fun initView()
    @LayoutRes
    open fun layoutRes(): Int = R.layout.select_dialog_item

    abstract fun proportion(): Double
    abstract fun gravity(): Int


    open fun getBarView():View? = null

    open fun registerKeyBoard():Boolean = false

    open fun useImmersionBar():Boolean = false
}