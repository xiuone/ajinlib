package com.xy.baselib.mvp.view

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes

interface BaseView {
    fun loadSuc()
    fun showError():View
    fun showError(@LayoutRes layoutRes:Int):View
    fun showPre():View
    fun showPre(@LayoutRes layoutRes:Int):View
    fun showLoading(@IdRes idRes: Int, @StringRes strRes:Int) :View
    fun showLoading(@IdRes idRes: Int,str:String) :View
    fun showLoading(@LayoutRes layoutRes:Int,@IdRes idRes: Int, @StringRes strRes:Int):View
    fun showLoading(@LayoutRes layoutRes:Int,@IdRes idRes: Int, strRes:String?):View
    fun disLoading()
    fun getPageContext():Context?
}