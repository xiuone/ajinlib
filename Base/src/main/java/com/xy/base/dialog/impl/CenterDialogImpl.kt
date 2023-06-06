package com.xy.base.dialog.impl

import android.content.Context
import android.content.DialogInterface
import com.xy.base.dialog.base.BaseCenterDialog
import com.xy.base.dialog.listener.DialogImplListener

open class CenterDialogImpl(context: Context,private val listener: DialogImplListener) :
    BaseCenterDialog(context){

    override fun initView() {
        listener.dialogInitView(this)
    }

    override fun layoutRes(): Int  = listener.dialogLayoutRes()?: 0

    override fun proportion(): Double = listener.dialogProportion()

    fun getDialogImplListener() = listener

    override fun show() {
        super.show()
        listener.dialogShow(this)
    }

    override fun onDismiss(p0: DialogInterface?) {
        super.onDismiss(p0)
        listener.dialogDismiss(this)
    }
}