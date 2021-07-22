package com.xy.baselib.ui.act

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.OnKeyboardListener
import com.xy.baselib.R
import com.xy.baselib.ui.dialog.LoadingDialog
import org.greenrobot.eventbus.EventBus

class BaseActController(private val activity:Activity?,private val baseView:View?,private val onKeyboardListener: OnKeyboardListener
                        ,private val subscribe: Any,private val listener:BaseActListener) {
    private val dialogs = ArrayList<Dialog>()

    var contentView : FrameLayout?=null
    var preView : FrameLayout?=null
    var errorView : FrameLayout?=null
    var titleView: FrameLayout?=null



    /**
     * 设置状态栏的view
     */
    fun setTitleLayout(@LayoutRes layout: Int){
        setTitleView(LayoutInflater.from(activity).inflate(layout,null))
    }

    fun setTitleView(view: View?){
        view?:return
        if (!listener.setTitleView(view))return
        titleView?.removeAllViews()
        titleView?.addView(view)
    }

    /**
     * 设置正文
     */
    fun setContentLayout(@LayoutRes layout:Int){
        setContentLayout(LayoutInflater.from(activity).inflate(layout,null))
    }

    fun setContentLayout(view: View?){
        view?:return
        if (!listener.setContentLayout(view))return
        contentView?.removeAllViews()
        contentView?.addView(view)
    }


    /**
     * 设置加载失败的时候
     */
    fun setErrorLayout(@LayoutRes layout: Int){
        setErrorView(LayoutInflater.from(activity).inflate(layout,null))
    }
    fun setErrorView(view: View?){
        view?:return
        if (!listener.setErrorView(view))return
        errorView?.removeAllViews()
        errorView?.addView(view)
    }

    /**
     * 设置预加载
     */
    fun setPreloadingLayout(@LayoutRes layout: Int){
        setPreloadingView(LayoutInflater.from(activity).inflate(layout,null))
    }
    fun setPreloadingView(view: View?){
        view?:return
        if (!listener.setPreloadingView(view))return
        preView?.removeAllViews()
        preView?.addView(view)
    }






    /**
     * 显示预加载
     */
    fun showPreLoading(){
        Handler(Looper.getMainLooper()).run {
            preView?.visibility = View.VISIBLE
            errorView?.visibility = View.GONE
            contentView?.visibility = View.GONE
        }
    }

    /**
     * 加载失败
     */
    fun showError(){
        Handler(Looper.getMainLooper()).run {
            preView?.visibility = View.GONE
            errorView?.visibility = View.VISIBLE
            contentView?.visibility = View.GONE
        }
    }

    /**
     * 加载完成
     */
    fun loadSuc(){
        Handler(Looper.getMainLooper()).run {
            preView?.visibility = View.GONE
            errorView?.visibility = View.GONE
            contentView?.visibility = View.VISIBLE
        }
    }


    fun showDialog(dialog: Dialog?){
        val activity = this.activity?:return
        if (activity.isFinishing)return
        dialog?:return

        dialog.show()
        for (oldDialog in dialogs) {
            if (oldDialog == dialog)
                return
        }
        dialogs.add(dialog)
    }


    fun onCreate(){
        contentView = baseView?.findViewById(R.id.content_frame_layout)
        preView = baseView?.findViewById(R.id.pre_loading_frame_layout)
        errorView = baseView?.findViewById(R.id.err_loading_frame_layout)
        titleView = baseView?.findViewById(R.id.title_layout_frame_layout)
        if (listener.registerEventBus())
            EventBus.getDefault().register(subscribe)
    }


    fun initStatusStatusBar(){
        val activity = this.activity?:return
        val bar = ImmersionBar.with(activity)
            .supportActionBar(false)
            .navigationBarEnable(false)
            .transparentBar()
            .statusBarDarkFont(listener.statusBarDurk())
        val view = listener.statusBarView()
        if (view != null)
            bar.titleBar(view)
        bar.keyboardEnable(true, listener.keyboardMode())
        bar.setOnKeyboardListener (onKeyboardListener)
        bar.init()
    }


    fun onDestroy() {
        for (dialog in dialogs){
            if (dialog.isShowing)
                dialog.dismiss()
        }
        if (listener.registerEventBus())
            EventBus.getDefault().unregister(subscribe)
    }
}