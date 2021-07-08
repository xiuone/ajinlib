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

abstract class BaseAct2 :AppCompatActivity(), OnKeyboardListener ,BaseImpl,BaseActListener{
    private var loadingDialog: LoadingDialog ?=null
    protected var baseActController:BaseActController?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_base_view)
        baseActController = BaseActController(this,this,this,this)
        baseActController?.onCreate()
        baseActController?.initStatusStatusBar()
        initView()
    }

    /**
     * 设置返回按钮
     */
    open fun setGoBackView(view: View?){
        view?.setOnClick(View.OnClickListener {
            onKeyDown(KeyEvent.KEYCODE_BACK,  null)
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
            loadingDialog = loadingDialog?: LoadingDialog(this@BaseAct2)
            baseActController?.showDialog(loadingDialog)
            loadingDialog?.setText(str)
        }
    }

    override fun disLoading() {
        if (!isFinishing)
            loadingDialog?.dismiss()
    }

    open fun reLoadData(){}
    override fun onKeyboardChange(isPopup: Boolean, keyboardHeight: Int) {}
    override fun setTitleView(view: View?) :Boolean = true
    override fun setContentLayout(view: View?) :Boolean = true
    override fun setErrorView(view: View?) :Boolean = true
    override fun setPreloadingView(view: View?):Boolean = true
    override fun statusBarView():View? = null
    override fun statusBarDurk():Boolean = true
    override fun registerEventBus():Boolean = false
    abstract fun initView()

    override fun onDestroy() {
        super.onDestroy()
        baseActController?.onDestroy()
    }
}