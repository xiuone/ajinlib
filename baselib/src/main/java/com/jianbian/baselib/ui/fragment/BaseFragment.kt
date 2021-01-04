package com.jianbian.baselib.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.OnKeyboardListener
import com.jianbian.baselib.BaseApp
import com.jianbian.baselib.R
import com.jianbian.baselib.ui.dialog.LoadingDialog
import com.jianbian.baselib.utils.GlideUtils
import com.jianbian.baselib.utils.setOnClick
import com.lzy.okgo.OkGo
import kotlinx.android.synthetic.main.layout_base_view.*
import me.jessyan.autosize.AutoSize
import org.greenrobot.eventbus.EventBus

abstract class BaseFragment :Fragment() , OnKeyboardListener {
    protected var defindPage:Int = 1
    protected var pageSize:Int = 20
    protected var page = 1
    private var loadingDialog: LoadingDialog ?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.layout_base_view,null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (registerEventBus())
            EventBus.getDefault().register(this)
    }

    fun setResetView(view: View){
        view.setOnClick(View.OnClickListener {
            reLoadData()
        })
    }


    /**
     * 设置正文
     */
    fun setContentLayout(@LayoutRes layout:Int){
        setContentLayout(LayoutInflater.from(context).inflate(layout,null))
    }

    fun setContentLayout(view: View){
        content_frame_layout.removeAllViews()
        content_frame_layout.addView(view)
    }

    /**
     * 设置状态栏的view
     */
    fun setTitleLayout(@LayoutRes layout: Int){
        setTitleView(LayoutInflater.from(context).inflate(layout,null))
    }
    fun setTitleView(view: View){
        title_layout_frame_layout.removeAllViews()
        title_layout_frame_layout.addView(view)
    }
    fun getTitleFrameLayout():View= title_layout_frame_layout
    fun getContentLayout():View = content_frame_layout
    fun getErrorFrameLayout():View =err_loading_frame_layout

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
    fun setPreloadingLayout(@LayoutRes layout: Int){
        setPreloadingView(LayoutInflater.from(context).inflate(layout,null))
    }
    fun setPreloadingView(view: View){
        pre_loading_frame_layout.removeAllViews()
        pre_loading_frame_layout.addView(view)
    }

    /**
     * 设置加载失败的时候
     */
    fun setErrorLayout(@LayoutRes layout: Int){
        setErrorView(LayoutInflater.from(context).inflate(layout,null))
    }
    fun setErrorView(view: View){
        err_loading_frame_layout.removeAllViews()
        err_loading_frame_layout.addView(view)
    }

    /**
     * 显示预加载
     */
    fun showPreLoading(){
        pre_loading_frame_layout.visibility = View.VISIBLE
        err_loading_frame_layout.visibility = View.GONE
        content_frame_layout.visibility = View.GONE
    }

    /**
     * 加载失败
     */
    fun showError(){
        pre_loading_frame_layout.visibility = View.GONE
        err_loading_frame_layout.visibility = View.VISIBLE
        content_frame_layout.visibility = View.GONE
    }

    override fun onKeyboardChange(isPopup: Boolean, keyboardHeight: Int) {

    }

    /**
     * 加载完成
     */
    open fun loadSuc(){
        pre_loading_frame_layout.visibility = View.GONE
        err_loading_frame_layout.visibility = View.GONE
        content_frame_layout.visibility = View.VISIBLE
    }

    fun showLoading(str: String?) {
        if (loadingDialog == null)
            loadingDialog= LoadingDialog(context!!)
        loadingDialog!!.show()
        loadingDialog?.setText(str)
    }

    fun disLoading() {
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