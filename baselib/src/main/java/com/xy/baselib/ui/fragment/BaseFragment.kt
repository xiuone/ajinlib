package com.xy.baselib.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.OnKeyboardListener
import com.xy.baselib.R
import com.xy.baselib.ui.dialog.LoadingDialog
import com.xy.baselib.utils.setOnClick
import com.lzy.okgo.OkGo
import org.greenrobot.eventbus.EventBus

abstract class BaseFragment :Fragment() , OnKeyboardListener {
    protected var defindPage:Int = 1
    protected var pageSize:Int = 20
    protected var page = 1
    private var loadingDialog: LoadingDialog ?=null
    protected var contentView : FrameLayout?=null
    protected var preView : FrameLayout?=null
    protected var errorView : FrameLayout?=null
    protected var titleView: FrameLayout?=null

    private var rootView:View ?= null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_base_view,null)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentView = rootView?.findViewById(R.id.content_frame_layout)
        preView = rootView?.findViewById(R.id.pre_loading_frame_layout)
        errorView = rootView?.findViewById(R.id.err_loading_frame_layout)
        titleView = rootView?.findViewById(R.id.title_layout_frame_layout)
        initView()
        if (registerEventBus())
            EventBus.getDefault().register(this)
    }

    open fun setGoBack(view: View?){
        view?.setOnClick(View.OnClickListener {
            activity?.finish()
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
        setContentLayout(LayoutInflater.from(context).inflate(layout,null))
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
        setTitleView(LayoutInflater.from(context).inflate(layout,null))
    }
    open fun setTitleView(view: View?){
        titleView?.removeAllViews()
        titleView?.addView(view)
    }
    open fun setStatusBarMode(view: View?, dark: Boolean) {
        val bar = ImmersionBar.with(this,true)
            .reset()
            .supportActionBar(false)
            .navigationBarEnable(false)
            .transparentBar()
            .statusBarDarkFont(dark)
            .navigationBarDarkIcon(dark)
            .navigationBarDarkIcon(dark)
        if (view != null)
            bar.titleBar(view)
        bar.keyboardEnable(true, WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED)
            .setOnBarListener {  }
        bar.init()
    }

    /**
     * 设置预加载
     */
    open fun setPreloadingLayout(@LayoutRes layout: Int){
        setPreloadingView(LayoutInflater.from(context).inflate(layout,null))
    }
    open fun setPreloadingView(view: View?){
        preView?.removeAllViews()
        preView?.addView(view)
    }

    /**
     * 设置加载失败的时候
     */
    open fun setErrorLayout(@LayoutRes layout: Int){
        setErrorView(LayoutInflater.from(context).inflate(layout,null))
    }
    open fun setErrorView(view: View?){
        if (view == null)return
        errorView?.removeAllViews()
        errorView?.addView(view)
    }

    /**
     * 显示预加载
     */
    open fun showPreLoading(){
        preView?.visibility = View.VISIBLE
        errorView?.visibility = View.GONE
        contentView?.visibility = View.GONE
    }

    /**
     * 加载失败
     */
    open fun showError(){
        preView?.visibility = View.GONE
        errorView?.visibility = View.VISIBLE
        contentView?.visibility = View.GONE
    }

    override fun onKeyboardChange(isPopup: Boolean, keyboardHeight: Int) {

    }

    /**
     * 加载完成
     */
    open fun loadSuc(){
        preView?.visibility = View.GONE
        errorView?.visibility = View.GONE
        contentView?.visibility = View.VISIBLE
    }

    open fun showLoading(str: String?) {
        if (isDetached)return
        if (loadingDialog == null)
            loadingDialog= LoadingDialog(context!!)
        loadingDialog?.show()
        loadingDialog?.setText(str)
    }

    open fun disLoading() {
        if (isDetached)return
        loadingDialog?.dismiss()
    }

    open fun getData(page:Int,pageSize:Int){}
    open fun reLoadData(){}
    open fun registerEventBus():Boolean = false
    open fun sizeInDp():Float = 360F

    override fun onDestroy() {
        super.onDestroy()
        OkGo.getInstance().cancelTag(this)
        if (registerEventBus())
            EventBus.getDefault().unregister(this)
    }

    abstract fun initView()
}