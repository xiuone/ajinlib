package com.xy.baselib.ui.dialog

import android.content.Context
import android.view.View

open class BaseImplDialog(context: Context,val impl:DialogImpl) :BaseDialog(context){
    override fun initView() {
        impl.initDialogView(this)
    }

    override fun proportion(): Double {
        return impl.dialogProportion()
    }

    override fun gravity(): Int {
        return impl.dialogGravity()
    }

    override fun layoutRes(): Int {
        return impl.dialogLayoutRes()
    }

    override fun showAnimation(view: View?) {
        impl.showAnimation(view)
    }
}