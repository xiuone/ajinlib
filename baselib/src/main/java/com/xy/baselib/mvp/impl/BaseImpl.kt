package com.xy.baselib.mvp.impl

interface BaseImpl {
    fun showPreLoading()
    fun showError()
    fun loadSuc()
    fun showLoading(str:String?)
    fun disLoading()
}