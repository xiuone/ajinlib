package com.xy.base.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.xy.base.R
import com.xy.base.listener.LoadViewListener
import com.xy.base.assembly.load.BaseAssemblyViewLoadDialog
import com.xy.base.assembly.load.LoadDialogView
import com.xy.base.dialog.LoadProgressDialog
import com.xy.base.dialog.base.BaseDialog
import com.xy.base.dialog.listener.DialogCancelSureView
import com.xy.base.dialog.listener.DialogImplListener

/**
 * 加载状态
 */
abstract class FragmentBaseStatusAssembly : FragmentBaseStatus() , BaseAssemblyViewLoadDialog,
    DialogImplListener,DialogCancelSureView, LoadViewListener, LoadDialogView {

    protected var actContentView:View?=null
    protected var statusView: ViewGroup?=null

    protected val loadView by lazy { LayoutInflater.from(context).inflate(loadRes(),null) }
    protected val unNetView by lazy { LayoutInflater.from(context).inflate(unNetRes(),null) }
    protected val errorView by lazy { LayoutInflater.from(context).inflate(errorRes(),null) }
    protected val emptyView by lazy { LayoutInflater.from(context).inflate(emptyRes(),null) }


    val loadDialog by lazy { context?.run { LoadProgressDialog(this, this@FragmentBaseStatusAssembly) } }


    @LayoutRes
    override fun dialogLayoutRes():Int = R.layout.dialog_xiu_common_load
    override fun dialogProportion(): Double = 0.7
    override fun dialogInitView(dialog: BaseDialog){}
    override fun dialogShow(dialog: BaseDialog){}


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
    override fun createContentView(): View? = actContentView

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



    /**
     * 提示框  取消  确定
     */
    override fun onCreateDialogCancelView(dialog: BaseDialog): View?  = dialog.findViewById(R.id.cancel_button)
    override fun onCreateDialogSureView(dialog: BaseDialog): View? = dialog.findViewById(R.id.sure_button)

    /**
     * 加载框  多用于上传的时候   或者修改数据
     */
    override fun onCreateLoadDialog(): LoadProgressDialog? = loadDialog
    override fun loadProgressString(): String?  = ""
    override fun loadProgressTvIdRes() = R.id.tvLoad

}