package com.xy.baselib.ui.act

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.OnKeyboardListener
import com.xy.baselib.R
import com.xy.baselib.ui.dialog.LoadingDialog
import com.xy.baselib.utils.ActivityController
import com.xy.baselib.utils.setOnClick
import com.lzy.okgo.OkGo
import com.xy.baselib.mvp.impl.BaseImpl
import org.greenrobot.eventbus.EventBus

abstract class BaseAct2 :AppCompatActivity(), OnKeyboardListener ,BaseImpl{
    private var loadingDialog: LoadingDialog ?=null
    protected var defindPage:Int = 0
    protected var pageSize = 20;
    protected var page = defindPage
    protected var contentView :FrameLayout?=null
    protected var preView :FrameLayout?=null
    protected var errorView :FrameLayout?=null
    protected var titleView:FrameLayout?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityController.addAct(this)
        setContentView(R.layout.layout_base_view)
        contentView = findViewById(R.id.content_frame_layout)
        preView = findViewById(R.id.pre_loading_frame_layout)
        errorView = findViewById(R.id.err_loading_frame_layout)
        titleView = findViewById(R.id.title_layout_frame_layout)
        initView()
        setStatusBarMode(statusBarView(),statusBarDurk())
        if (registerEventBus())
            EventBus.getDefault().register(this)
    }

    /**
     * 设置返回按钮
     */
    open fun setGoBackView(view: View?){
        view?.setOnClick(View.OnClickListener {
            finish()
        })
    }

    open fun setResetView(view: View?){
        view?.setOnClick(View.OnClickListener {
            reLoadData()
        })
    }

    /**
     * 设置正文
     */
    open fun setContentLayout(@LayoutRes layout:Int){
        setContentLayout(LayoutInflater.from(this).inflate(layout,null))
    }

    open fun setContentLayout(view: View?){
        if (view == null)return
        contentView?.removeAllViews()
        contentView?.addView(view)
    }

    /**
     * 设置状态栏的view
     */
    open fun setTitleLayout(@LayoutRes layout: Int){
        setTitleView(LayoutInflater.from(this).inflate(layout,null))
    }
    open fun setTitleView(view: View?){
        if (view == null)return
        titleView?.removeAllViews()
        titleView?.addView(view)
    }

    open fun setStatusBarMode(view: View?, dark: Boolean) {
        val bar = ImmersionBar.with(this)
            .supportActionBar(false)
            .navigationBarEnable(false)
            .transparentBar()
            .statusBarDarkFont(dark)
        if (view != null)
            bar.titleBar(view)
        bar.keyboardEnable(true, WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED)
        bar.setOnKeyboardListener (this)
        bar.init()
    }

    /**
     * 设置预加载
     */
    open fun setPreloadingLayout(@LayoutRes layout: Int){
        setPreloadingView(LayoutInflater.from(this).inflate(layout,null))
    }
    open fun setPreloadingView(view: View?){
        if (view == null)return
        preView?.removeAllViews()
        preView?.addView(view)
    }

    /**
     * 设置加载失败的时候
     */
    open fun setErrorLayout(@LayoutRes layout: Int){
        setErrorView(LayoutInflater.from(this).inflate(layout,null))
    }
    open fun setErrorView(view: View?){
        if (view == null)return
        errorView?.removeAllViews()
        errorView?.addView(view)
    }

    /**
     * 显示预加载
     */
    override fun showPreLoading(){
        Handler(Looper.getMainLooper()).run {
            preView?.visibility = View.VISIBLE
            errorView?.visibility = View.GONE
            contentView?.visibility = View.GONE
        }
    }

    /**
     * 加载失败
     */
    override fun showError(){
        Handler(Looper.getMainLooper()).run {
            preView?.visibility = View.GONE
            errorView?.visibility = View.VISIBLE
            contentView?.visibility = View.GONE
        }
    }

    /**
     * 加载完成
     */
    override fun loadSuc(){
        Handler(Looper.getMainLooper()).run {
            preView?.visibility = View.GONE
            errorView?.visibility = View.GONE
            contentView?.visibility = View.VISIBLE
        }
    }

    override fun showLoading(str: String?) {
        if (!isFinishing) {
            if (loadingDialog == null)
                loadingDialog = LoadingDialog(this)
            loadingDialog?.show()
            loadingDialog?.setText(str)
        }
    }

    override fun disLoading() {
        if (!isFinishing)
            loadingDialog?.dismiss()
    }

    open fun getData(page:Int,pageSize:Int){}
    open fun reLoadData(){}
    override fun onKeyboardChange(isPopup: Boolean, keyboardHeight: Int) {

    }


    abstract fun initView()
    abstract fun statusBarView():View?
    open fun statusBarDurk():Boolean = true
    open fun registerEventBus():Boolean = false


    override fun onDestroy() {
        super.onDestroy()
        OkGo.getInstance().cancelTag(this)
        ActivityController.removeAct(this)
        if (registerEventBus())
            EventBus.getDefault().unregister(this)
    }
}