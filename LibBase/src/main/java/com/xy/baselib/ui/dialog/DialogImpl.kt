package com.xy.baselib.ui.dialog

import android.view.View

interface DialogImpl {
    fun initDialogView(dialog: BaseDialog)
    fun dialogProportion(): Double
    fun dialogGravity(): Int
    fun dialogLayoutRes():Int
    fun showAnimation(view: View?){}
}