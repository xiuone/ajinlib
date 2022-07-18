package com.xy.baselib.mvp.view

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes

interface BaseView {
    fun loadSuc(){}
    fun showError():View? =  null
    fun showError(@LayoutRes layoutRes:Int):View? =  null
    fun showPre():View? =  null
    fun showPre(@LayoutRes layoutRes:Int):View? =  null
    fun showLoading(@IdRes idRes: Int, @StringRes strRes:Int) :View? =  null
    fun showLoading(@IdRes idRes: Int,str:String) :View? =  null
    fun showLoading(@LayoutRes layoutRes:Int,@IdRes idRes: Int, @StringRes strRes:Int):View? =  null
    fun showLoading(@LayoutRes layoutRes:Int,@IdRes idRes: Int, strRes:String?):View? =  null
    fun disLoading(){}
    fun getPageContext():Context? =  null
}