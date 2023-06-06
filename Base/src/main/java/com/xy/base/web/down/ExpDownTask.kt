package com.xy.base.web.down

import com.arialyy.aria.core.task.DownloadTask

/**
 * 获取下载任务的id
 */
fun DownloadTask?.getDownloadId():Long{
    var did: Long = -1
    if (this != null && this.downloadEntity != null) {
        did = this.downloadEntity.id
    }
    return did
}
