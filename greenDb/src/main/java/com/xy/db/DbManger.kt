package com.xy.db

import android.content.Context
import com.xy.db.down.DownloadDaoManager

object DbManger {
    private var downManager: DownloadDaoManager ?= null

    @Synchronized
    fun getDownManager(context: Context): DownloadDaoManager? {
        if (downManager == null)
            downManager = DownloadDaoManager(context)
        return downManager as DownloadDaoManager
    }
}