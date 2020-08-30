package com.jianbian.baselib.mvp.impl

import android.view.View

interface MultiChoseListener<T> {
    fun onMultiChoseView(view:View,item:T,position:Int,select:Boolean,repeat :Boolean)
}