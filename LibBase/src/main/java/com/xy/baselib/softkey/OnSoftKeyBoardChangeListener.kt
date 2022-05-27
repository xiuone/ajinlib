package com.xy.baselib.softkey

import android.content.Context

interface OnSoftKeyBoardChangeListener {
    fun keyBoardShow(context: Context,height: Int){}
    fun keyBoardHide(context: Context,height: Int){}
}