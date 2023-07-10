package  xy.xy.base.utils.notify

import xy.xy.base.utils.runBackThread
import xy.xy.base.utils.runMain


abstract class NotifyBase<T> {
    private val tagHashMap by lazy { HashMap<Any,T>() }
    protected val relation by lazy { "-" }

    open fun addNotify(tag:String?,view: T?){
        if (tag == null || view == null)return
        synchronized(this){
            val value = tagHashMap[tag]
            if (value != null)return
            tagHashMap[tag] = view
        }
    }

    open fun removeNotify(life: Any?){
        if (life == null )return
        synchronized(this){
            tagHashMap.remove(life)
        }
    }

    protected fun findItem(method:(T)->Unit){
        runBackThread({
            synchronized(this){
                for (entry in tagHashMap.entries){
                    runMain({
                        method(entry.value)
                    })
                }
            }
        })
    }
}