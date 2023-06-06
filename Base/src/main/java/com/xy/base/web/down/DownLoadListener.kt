package com.xy.base.web.down

import com.arialyy.aria.core.task.DownloadTask

interface DownLoadListener {
    fun running(task: DownloadTask)
    fun taskComplete(task: DownloadTask)
    fun taskFail(task: DownloadTask)
    fun taskStop(task: DownloadTask){}
    fun taskCancel(task: DownloadTask){}
}