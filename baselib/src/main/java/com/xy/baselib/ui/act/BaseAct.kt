package com.xy.baselib.ui.act

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
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

abstract class BaseAct :FragmentActivity(), OnKeyboardListener ,BaseImpl,BaseActListener{
    private var loadingDialog: LoadingDialog ?=null
    protected var baseActController:BaseActController?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_base_view)
        baseActController = BaseActController(this,findViewById(R.id.base_view)
            ,this,this,this)
        baseActController?.onCreate()
        baseActController?.initStatusStatusBar()
        initView()
    }

    /**
     * 设置返回按钮
     */
    open fun setGoBackView(view: View?){
        view?.setOnClick(View.OnClickListener {
            onBackPressed()
        })
    }

    open fun setResetView(view: View?){
        view?.setOnClick(View.OnClickListener {
            reLoadData()
        })
    }

    /**
     * 显示预加载
     */
    override fun showPreLoading(){
        baseActController?.showPreLoading()
    }

    /**
     * 加载失败
     */
    override fun showError(){
        baseActController?.showError()
    }

    /**
     * 加载完成
     */
    override fun loadSuc(){
        baseActController?.loadSuc()
    }

    override fun showLoading(str: String?) {
        Handler(Looper.getMainLooper()).run {
            loadingDialog = loadingDialog?: LoadingDialog(this@BaseAct)
            baseActController?.showDialog(loadingDialog)
            loadingDialog?.setText(str)
        }
    }

    override fun disLoading() {
        if (!isFinishing)
            loadingDialog?.dismiss()
    }

    /**
     * 重新加载
     */
    open fun reLoadData(){}

    /**
     * 软键盘的显示隐藏 及软键盘的高度
     */
    override fun onKeyboardChange(isPopup: Boolean, keyboardHeight: Int) {}

    /**
     * 设置显示 留下扩展自定义
     */
    override fun setTitleView(view: View?) :Boolean = true

    /**
     * 设置正文留下扩展自定义
     */
    override fun setContentLayout(view: View?) :Boolean = true

    /**
     * 设置错误布局 留下扩展自定义
     */
    override fun setErrorView(view: View?) :Boolean = true

    /**
     * 设置与加载布局  留下扩展自定义
     */
    override fun setPreloadingView(view: View?):Boolean = true

    /**
     * 设置状态栏的view  通常用于沉静式布局
     */
    override fun statusBarView():View? = null

    /**
     * 状态栏文字和图标的颜色  true 为黑  false为白
     */
    override fun statusBarDurk():Boolean = true

    /**
     * 注册EventBus事件
     */
    override fun registerEventBus():Boolean = false

    override fun keyboardMode(): Int = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED

    override fun keyboardEnable(): Boolean = true
    /**
     * 初始化的时候用到
     */
    abstract fun initView()

    /**
     * activity销毁的时候
     */
    override fun onDestroy() {
        super.onDestroy()
        baseActController?.onDestroy()
    }
}