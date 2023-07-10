package xy.xy.base.utils.glide

import java.util.*
import kotlin.collections.ArrayList

object GlideProgressManger {
    private val listenersMap by lazy { HashMap<String, ArrayList<GlideProgressListener>>() }

    fun addListener(url: String?, listener: GlideProgressListener?) {
        synchronized(this){
            if (url == null || listener == null)return
            val list = listenersMap[url] ?:ArrayList()
            for (item in list){
                if (item == listener)return@synchronized
            }
            list.add(listener)
            listenersMap[url] = list
        }
    }

    fun removeListener(url: String?,listener: GlideProgressListener?) {
        synchronized(this){
            if (url == null || listener == null)return
            val list = listenersMap[url] ?:ArrayList()
            for (item in list){
                if (item == listener){
                    list.remove(item)
                    break
                }
            }
            if (list.isNullOrEmpty()) {
                listenersMap.remove(url)
            }else{
                listenersMap[url] = list
            }
        }
    }

    fun getProgressListener(url: String?): ArrayList<GlideProgressListener> {
        synchronized(this){
            if (url == null)return ArrayList()
            return listenersMap[url]?:ArrayList()
        }
    }
}