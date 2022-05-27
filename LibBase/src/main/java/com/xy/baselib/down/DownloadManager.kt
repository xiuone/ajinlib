package com.xy.baselib.down

import android.content.Context
import com.arialyy.annotations.Download
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.task.DownloadTask
import com.xy.baselib.exp.insertImage
import com.xy.baselib.exp.installAPK
import com.xy.baselib.exp.Logger

class DownloadManager(val context: Context) {

    init {
        Aria.init(context)
        Aria.download(this).register()
    }
    /**
     * 下载中
     * @param task
     */
    @Download.onTaskRunning
    fun running(task: DownloadTask) {
        Logger.d("DownLoadManager-----------running------id:${task?.getDownloadId()}  downUrl:${task?.downloadUrl}  proportion:${task?.getDownProportion()}")
    }

    /**
     * 下载成功
     * @param task
     */
    @Download.onTaskComplete
    fun taskComplete(task: DownloadTask?) {
        Logger.d("DownLoadManager-----------taskComplete------id:${task?.getDownloadId()}  downUrl:${task?.downloadUrl}  proportion:${task?.getDownProportion()}")
        when{
            task?.filePath?.endsWith(".apk") == true->{
                context.installAPK(task.filePath)
            }
            task?.filePath?.endsWith(".png") == true ->{
                context.insertImage(task.filePath)
            } 
            task?.filePath?.endsWith(".jpg") == true->{
                context.insertImage(task.filePath)
            }
            task?.filePath?.endsWith(".jpeg") == true->{
                context.insertImage(task.filePath)
            }
        }
    }

    /**
     * 下载失败
     * @param task
     */
    @Download.onTaskFail
    fun taskFail(task: DownloadTask?, e: Exception?) {
        Logger.d("DownLoadManager-----------taskFail------id:${task?.getDownloadId()}  downUrl:${task?.downloadUrl}  proportion:${task?.getDownProportion()}   error:${e?.message}")
    }

    /**
     * 暂停下载
     * @param task
     */
    @Download.onTaskStop
    fun taskStop(task: DownloadTask?) {
        Logger.d("DownLoadManager-----------taskStop------id:${task?.getDownloadId()}  downUrl:${task?.downloadUrl}  proportion:${task?.getDownProportion()}")
    }

    /**
     * 取消下载
     * @param task
     */
    @Download.onTaskCancel
    fun taskCancel(task: DownloadTask?) {
        Logger.d("DownLoadManager-----------taskCancel------id:${task?.getDownloadId()}  downUrl:${task?.downloadUrl}  proportion:${task?.getDownProportion()}")
    }
}