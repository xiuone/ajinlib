package xy.xy.base.assembly.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import xy.xy.base.utils.TagNumber

abstract class BaseAssemblyBase<T>(protected var view: T? = null) : LifecycleObserver {
    private val lifecycleObserverList by lazy { ArrayList<LifecycleObserver>() }
    protected val TAG by lazy { TagNumber.getTag(this::class.java.name) }
    protected val mainHandler by lazy { Handler(Looper.getMainLooper()) }

    open fun onCreateInit(){
        synchronized(lifecycleObserverList){
            for (item in lifecycleObserverList) {
                if (item is BaseAssemblyBase<*>)
                    item.onCreateInit()
            }
        }
    }

    open fun onCreateInit(savedInstanceState : Bundle?){
        synchronized(lifecycleObserverList){
            for (item in lifecycleObserverList) {
                if (item is BaseAssemblyBase<*>)
                    item.onCreateInit(savedInstanceState)
            }
        }
    }

    fun  addLifecycleObservers(lifecycleObserver: LifecycleObserver) {
        synchronized(lifecycleObserverList){
            lifecycleObserverList.add(lifecycleObserver)
            if (lifecycleObserver is BaseAssemblyBase<*>)
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
                if (item is BaseAssemblyBase<*>)
                    item.onDestroyed(owner)
            }
        }
    }

    open fun onSaveInstanceState(outState: Bundle?) {}

}