package com.xy.base.assembly

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.xy.base.R
import  com.xy.base.dialog.base.BaseDialog
import com.xy.base.assembly.base.BaseAssemblyViewWithContext
import com.xy.base.dialog.listener.DialogCancelSureView
import com.xy.base.assembly.load.LoadDialogView
import com.xy.base.dialog.LoadProgressDialog
import com.xy.base.listener.LoadViewListener

abstract class BaseAssemblyImpl(protected val rootView:View,
                                private val contentView:View?=null,
                                private val statusView:ViewGroup?=null,
                                protected val loadDialog: LoadProgressDialog? = null,
                                protected val activity:Activity?=null) : BaseAssemblyViewWithContext, DialogCancelSureView,
    LoadViewListener, LoadDialogView,LifecycleObserver {

    protected val TAG by lazy { this::class.java.name }

    protected val context: Context by lazy { rootView.context }

    protected val loadView by lazy { LayoutInflater.from(context).inflate(loadRes(),null) }
    protected val unNetView by lazy { LayoutInflater.from(context).inflate(unNetRes(),null) }
    protected val errorView by lazy { LayoutInflater.from(context).inflate(errorRes(),null) }
    protected val emptyView by lazy { LayoutInflater.from(context).inflate(emptyRes(),null) }



    open fun onCreate(){}


    override fun getPageContext(): Context  = context
    override fun getCurrentAct(): Activity? = activity

    /**
     * 提示框  取消  确定
     */
    override fun onCreateDialogCancelView(dialog: BaseDialog): View?  = dialog.findViewById(R.id.cancel_button)
    override fun onCreateDialogSureView(dialog: BaseDialog): View? = dialog.findViewById(R.id.sure_button)


    /**
     * 加载框  多用于上传的时候   或者修改数据
     */
    override fun loadProgressTvIdRes(): Int = R.id.tvLoad
    override fun onCreateLoadDialog(): LoadProgressDialog? = loadDialog
    override fun loadProgressString(): String?  = ""


    private fun addStatusView(view: View):View?{
        val statusChildCount = statusView?.childCount?:0
        if (statusChildCount > 0 ){
            val childView = statusView?.getChildAt(0)
            if (childView == view){
                return statusView
            }
        }
        statusView?.removeAllViews()
        statusView?.addView(view)
        return statusView
    }


    /**
     * 加载状态
     */
    override fun createContentView(): View? = contentView

    override fun createLoadView(): View? = addStatusView(loadView)

    override fun createUnNetView(): View? = addStatusView(unNetView)

    override fun createErrorView(): View? = addStatusView(errorView)

    override fun createEmptyView(): View? = addStatusView(emptyView)

    override fun createErrorReLoadView(): View?  = errorView?.findViewById(R.id.re_load_view)

    override fun createUnNetReLoadView(): View? = errorView?.findViewById(R.id.re_load_view)


    protected open fun loadRes():Int = R.layout.status_load
    protected open fun unNetRes():Int = R.layout.status_un_net
    protected open fun errorRes():Int = R.layout.status_error
    protected open fun emptyRes():Int = R.layout.status_empty

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPause(owner: LifecycleOwner?) {}

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume(owner: LifecycleOwner?) {}


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroyed(owner: LifecycleOwner) {}
 }