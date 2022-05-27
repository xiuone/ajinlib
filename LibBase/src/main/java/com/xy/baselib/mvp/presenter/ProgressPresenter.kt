package com.xy.baselib.mvp.presenter

import android.content.Context
import android.view.Gravity
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import com.xy.baselib.R
import com.xy.baselib.mvp.view.BaseView
import com.xy.baselib.ui.dialog.BaseDialog
import com.xy.baselib.ui.dialog.DialogImpl
import com.xy.baselib.ui.dialog.LoadProgressBaseDialog

abstract class ProgressPresenter<T : BaseView>(view:T) :BasePresenter<T>(view),DialogImpl{
    private var loadProgressDialog: LoadProgressBaseDialog?=null

    private fun createProgressDialog():LoadProgressBaseDialog?{
        val context:Context? = view?.getPageContext();
        if (loadProgressDialog == null && context != null){
            loadProgressDialog = LoadProgressBaseDialog(context,this)
        }
        return loadProgressDialog
    }

    /**
     * 显示进度
     * @param str
     */
    fun show(@StringRes strRes:Int) {
        mainHandler.post {
            createProgressDialog()?.show(progressIdRes(),strRes)
        }
    }

    /**
     * 隐藏进度
     */
    fun dismiss() {
        mainHandler.post {
            createProgressDialog()?.dismiss()
        }
    }


    override fun initDialogView(dialog: BaseDialog) {

    }

    override fun dialogProportion(): Double = 0.5

    override fun dialogGravity(): Int =Gravity.CENTER

    override fun dialogLayoutRes(): Int = R.layout.dialog_common_load

    override fun onDestroyed(owner: LifecycleOwner) {
        loadProgressDialog?.dismiss()
        loadProgressDialog = null
    }

    open fun progressIdRes():Int = R.id.tvLoad
}