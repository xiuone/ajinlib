package com.xy.baselib


abstract class NotifyBase<T> {
    private val notifyListener = ArrayList<T>()

    fun addNotify(view: T?){
        synchronized(this){
            view?.run {
                if (notifyListener.contains(this))return@run
                notifyListener.add(this)
            }
        }
    }
    fun removeNotify(view: T?){
        synchronized(this){
            view?.run {
                notifyListener.remove(view)
            }
        }
    }

    fun clear(){
        synchronized(this){
            notifyListener.clear()
        }
    }

    protected fun findItem(method:(T)->Unit){
        synchronized(this){
            for (listener in notifyListener){
                method(listener)
            }
        }
    }
}