package com.xy.baselib.ui.dialog

import android.content.Context
import android.view.View

open class BaseImplDialog(context: Context,val impl:DialogImpl) :BaseDialog(context){
    override fun initView() {
        impl.initDialogView(this)
    }

    override fun showAnimation(view: View?) {
        impl.showAnimation(view)
    }

    override fun proportion(): Double = impl.dialogProportion()

    override fun gravity(): Int = impl.dialogGravity()

    override fun layoutRes(): Int = impl.dialogLayoutRes()

    override fun useImmersionBar(): Boolean = impl.useImmersionBar()

    override fun registerKeyBoard(): Boolean = impl.registerKeyBoard()

    override fun getBarView(): View? = impl.getBarView()
}