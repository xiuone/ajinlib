package com.xy.base.widget.navi.main

interface NaviClickedListener<T> {
    fun onClickedNavi(position:Int,item:T):Boolean
}