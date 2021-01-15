package com.xy.baselib.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.*


object FileUtils {

    /***
     * 保存照片到本地--注意相关权限
     */
    fun savePhoto(context: Context?, file: File): String? {
        val uri = Uri.fromFile(file)
        context?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
        return file?.path
    }

    fun delFile(path:String?){
        if (path == null)return
        delFile(File((path)))
    }
    fun delFile(file:File?){
        if (file == null)return
        if (file.exists())
            file.delete()
    }

    fun delFileList(paths :List<String?>?){
        if (paths == null)return
        for (path in paths)
            delFile(path)
    }
}