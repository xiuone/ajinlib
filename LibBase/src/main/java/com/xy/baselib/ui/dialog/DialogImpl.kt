package com.xy.baselib.ui.dialog

import android.view.View

interface DialogImpl {
    fun initDialogView(dialog: BaseDialog)
    fun dialogProportion(): Double
    fun dialogGravity(): Int
    fun dialogLayoutRes():Int
    fun registerKeyBoard():Boolean = false
    fun useImmersionBar():Boolean = false
    fun getBarView():View?=null
    fun showAnimation(view: View?){}
}