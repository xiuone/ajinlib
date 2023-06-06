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

abstract class BaseAssembly<T : BaseAssemblyView>(protected var view: T? = null) : LifecycleObserver {
    private val lifecycleObserverList by lazy { ArrayList<LifecycleObserver>() }
    protected val liftTag = this::class.java.toString()
    protected val mainHandler by lazy { Handler(Looper.getMainLooper()) }

    open fun onCreateInit(){
        synchronized(lifecycleObserverList){
            for (item in lifecycleObserverList) {
                if (item is BaseAssembly<*>)
                    item.onCreateInit()
            }
        }
    }

    open fun onCreateInit(savedInstanceState : Bundle?){
        synchronized(lifecycleObserverList){
            for (item in lifecycleObserverList) {
                if (item is BaseAssembly<*>)
                    item.onCreateInit(savedInstanceState)
            }
        }
    }

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

    fun  addLifecycleObservers(lifecycleObserver: LifecycleObserver) {
        synchronized(lifecycleObserverList){
            lifecycleObserverList.add(lifecycleObserver)
            if (lifecycleObserver is BaseAssembly<*>)
                lifecycleObserver.onCreateInit()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    open fun onAny(owner: LifecycleOwner?) {}

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreate(owner: LifecycleOwner?) {}

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStart(owner: LifecycleOwner?) {}

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onStop(owner: LifecycleOwner?) {}

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume(owner: LifecycleOwner?) {}

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPause(owner: LifecycleOwner?) {}

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroyed(owner: LifecycleOwner) {
        view = null
        mainHandler.removeCallbacksAndMessages(null)
        synchronized(lifecycleObserverList){
            for (item in lifecycleObserverList) {
                if (item is BaseAssembly<*>)
                    item.onDestroyed(owner)
            }
        }
    }

    open fun onSaveInstanceState(outState: Bundle?) {}

    fun getContext() = view?.getPageContext()
}