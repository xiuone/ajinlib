package xy.xy.base.web.down

import android.content.Context
import android.text.TextUtils
import com.arialyy.annotations.Download
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.common.HttpOption
import com.arialyy.aria.core.task.DownloadTask
import xy.xy.base.R
import xy.xy.base.utils.ContextHolder
import xy.xy.base.utils.Logger
import  xy.xy.base.utils.notify.NotifyBase
import xy.xy.base.utils.exp.getHost
import xy.xy.base.utils.exp.getResString
import xy.xy.base.utils.exp.showToast
import xy.xy.base.utils.exp.createDirs
import xy.xy.base.web.agent.UserAgentObject

class DownloadManager(private val context: Context?) : NotifyBase<DownLoadListener>(){

    private fun getDownHeads(context: Context,url: String): HashMap<String, String> {
        val head = HashMap<String, String>()
        head["user-agent"] = UserAgentObject.getCurrentUserAgent(context)
        head["Host"] = url.getHost()
        head["Accept"] = "*/*"
        return head
    }

    /**
     * 删除下载
     */
    fun delete(path: String?, deleteFile: Boolean = true) {
        Aria.get(context).delRecord(1, path, deleteFile)
    }

    fun cancel(taskId: Long?) {
        if (taskId == null)return
        Aria.download(context).load(taskId).cancel()
    }

    fun resume(taskId: Long){
        Aria.download(this).load(taskId).resume()
    }

    /**
     * 开始下载
     * @param context
     * @param url
     * @return
     */
    fun downloadFile(url: String, savePath: String): Long {
        savePath.createDirs()
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(savePath) || context == null) {
            return DownStatusEnum.NONO.type.toLong()
        }
        val httpOption = HttpOption()
        val heads = getDownHeads(context,url)
        httpOption.addHeaders(heads)

        val downloadId = Aria.download(this)
            .load(url) //读取下载地址
            .option(httpOption)
            .setFilePath(savePath, true) //设置文件保存的完整路径
            .create() //启动下载
        Logger.e("=====downloadId:$downloadId")
        if (downloadId <= 0 ) {
            context.showToast(context.getResString(R.string.download_add_fail))
            return DownStatusEnum.NONO.type.toLong()
        }
        return downloadId
    }

    /**
     * 下载中
     * @param task
     */
    @Download.onTaskRunning
    fun running(task: DownloadTask) {
        findItem {
            it.running(task)
        }
    }

    /**
     * 下载成功
     * @param task
     */
    @Download.onTaskComplete
    fun taskComplete(task: DownloadTask) {
        findItem {
            it.taskComplete(task)
        }
    }

    /**
     * 下载失败
     * @param task
     */
    @Download.onTaskFail
    fun taskFail(task: DownloadTask, e: Exception?) {
        findItem {
            it.taskFail(task)
        }
    }

    /**
     * 暂停下载
     * @param task
     */
    @Download.onTaskStop
    fun taskStop(task: DownloadTask) {
        findItem {
            it.taskStop(task)
        }
    }

    /**
     * 取消下载
     * @param task
     */
    @Download.onTaskCancel
    fun taskCancel(task: DownloadTask) {
        Logger.d("DownLoadManager-----------taskCancel------")
        findItem {
            it.taskCancel(task)
        }
    }


    fun init(){
        Aria.init(context)
        Aria.download(this).register()
    }

    companion object{
        val instance by lazy { DownloadManager(ContextHolder.getContext()?.applicationContext) }
    }
}