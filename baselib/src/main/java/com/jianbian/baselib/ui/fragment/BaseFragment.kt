package com.jianbian.baselib.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.gyf.barlibrary.ImmersionBar
import com.jianbian.baselib.R
import com.jianbian.baselib.ui.dialog.LoadingDialog
import kotlinx.android.synthetic.main.layout_base_view.*

abstract class BaseFragment :Fragment() {
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
    fun getTitleFrameLayout():View{
        return title_layout_frame_layout
    }
    fun setStatusBarMode(view: View?, dark: Boolean) {
        val bar = ImmersionBar.with(this)
            .supportActionBar(false)
            .navigationBarEnable(false)
            .transparentBar()
            .statusBarDarkFont(dark)
        if (view != null)
            bar.titleBar(view)
        bar.keyboardEnable(true, WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED)
        bar.init()
    }

    /**
     * 设置底部导航栏
     */
    fun setNavigationLayout(@LayoutRes layout: Int){
        setNavigationView(LayoutInflater.from(context).inflate(layout,null))
    }
    fun setNavigationView(view: View){
        navigation_bar_frame_layout.removeAllViews()
        navigation_bar_frame_layout.addView(view)
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

    open fun getData(page:Int){}

    abstract fun initView()

}