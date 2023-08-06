package xy.xy.base.utils.down

import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.FileCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.lzy.okgo.request.base.Request
import  xy.xy.base.utils.notify.NotifyBase
import xy.xy.base.web.down.DownLoadListener
import java.io.File


object DownloadManager : NotifyBase<DownLoadListener>(){
    private val downMap by lazy { HashMap<String,String>() }
    /**
     * 开始下载
     * @param context
     * @param url
     * @return
     */
    fun downloadFile(url: String?, filePath: String?,name:String) {
        if (url.isNullOrEmpty() || filePath.isNullOrEmpty()){
            findItem { it.taskFail(url) }
        }else{
            cancel(url)
            downMap[url] = url
            OkGo.get<File>(url).tag(url).execute(object :FileCallback(filePath,name){
                override fun onStart(request: Request<File, out Request<Any, Request<*, *>>>?) {
                    super.onStart(request)
                    findItem { it.start(url) }
                }
                override fun onSuccess(response: Response<File>?) {
                    findItem { it.taskComplete(url,response?.body()) }
                }

                override fun downloadProgress(progress: Progress?) {
                    super.downloadProgress(progress)
                    val fraction = progress?.fraction?:0F
                    findItem { it.running(url,(fraction * 100).toInt(),progress?.totalSize?:1) }
                }

                override fun onError(response: Response<File>?) {
                    super.onError(response)
                    findItem { it.taskFail(url) }
                }
            })
        }
    }

    fun cancel(url: String?){
        if (url.isNullOrEmpty())return
        val tag = downMap[url]
        OkGo.getInstance().cancelTag(tag)
        findItem { it.taskCancel(url) }
    }
}