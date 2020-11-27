package com.jianbian.baselib.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
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
}