package com.xy.base.widget.bar.mswitch

interface SwitchBarListener {
    fun onSwitchCallBack(isSelect:Boolean)
    fun onSwitchIntercept(isSelect:Boolean):Boolean = false
}