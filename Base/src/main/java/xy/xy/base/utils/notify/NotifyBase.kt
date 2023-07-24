package  xy.xy.base.utils.notify

import xy.xy.base.utils.runBackThread
import xy.xy.base.utils.runMain


abstract class NotifyBase<T> {
    private val tagHashMap by lazy { HashMap<Any,ArrayList<T>>() }
    protected val relation by lazy { "-" }

    open fun addNotify(tag:String?,view: T?){
        if (tag == null || view == null)return
        synchronized(this){
            val value = tagHashMap[tag]?:ArrayList()
            if (!value.contains(view)){
                value.add(view)
                tagHashMap[tag] = value
            }
        }
    }

    open fun removeNotify(tag: Any?,view: T?){
        if (tag == null )return
        synchronized(this){
            val value = tagHashMap[tag]?:ArrayList()
            value.remove(view)
            if (value.isEmpty()){
                tagHashMap.remove(tag)
            }else{
                tagHashMap[tag] = value
            }
        }
    }

    protected fun findItem(method:(T)->Unit){
        runBackThread({
            synchronized(this){
                for (entry in tagHashMap.entries){
                    for (item in entry.value){
                        runMain({
                            method(item)
                        })
                    }
                }
            }
        })
    }
}