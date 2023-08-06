package xy.xy.base.web.down

import java.io.File

interface DownLoadListener {
    fun start(url:String){}
    fun running(url:String,progress:Int,totalSize:Long){}
    fun taskComplete(url:String,path:File?){}
    fun taskFail(url:String?){}
    fun taskCancel(url:String){}
}