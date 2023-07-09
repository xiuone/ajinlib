package com.xy.base.assembly.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.xy.base.dialog.impl.BottomDialogImpl
import com.xy.base.dialog.impl.CenterDialogImpl
import com.xy.base.dialog.listener.DialogImplListener

abstract class BaseAssemblyWithContext<T : BaseAssemblyViewWithContext>(view: T? = null) : BaseAssemblyBase<T>(view) {

    protected fun createBottomDialog(listener:DialogImplListener?): BottomDialogImpl?{
        val context = getContext()
        if (context == null || listener == null)
            return null
        return BottomDialogImpl(context, listener)
    }

    protected fun createCenterDialog(listener: DialogImplListener?): CenterDialogImpl?{
        val context = getContext()
        if (context == null || listener == null)
            return null
        return CenterDialogImpl(context, listener)
    }

    fun getContext() = view?.getPageContext()

    fun getCurrentAct() = view?.getCurrentAct()
    fun getCurrentFragment() = view?.getCurrentFragment()
}