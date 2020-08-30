package com.jianbian.baselib.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import java.io.*


object FileUtils {

    /***
     * 保存照片到本地--注意相关权限
     */
    fun savePhoto(context: Context, file: File): String? {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        val tmpUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        try {
            val fileInputStream = file.inputStream()
            val bitmap = BitmapFactory.decodeStream(fileInputStream)
            //获取刚插入的数据的Uri对应的输出流
            val outputStream: OutputStream? = contentResolver.openOutputStream(tmpUri!!)
            //将bitmap图片保存到Uri对应的数据节点中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream?.close()
            fileInputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tmpUri?.path
    }

    /***
     * 保存照片到本地--注意相关权限
     */
    fun savePhoto(context: Context, bitmap: Bitmap?): String? {
        val contentResolver = context.contentResolver
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        val tmpUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        try {
            //获取刚插入的数据的Uri对应的输出流
            val outputStream: OutputStream? = contentResolver.openOutputStream(tmpUri!!)
            //将bitmap图片保存到Uri对应的数据节点中
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tmpUri?.path
    }

    /***
     * bitmap转换为byte[]
     */
    fun bitmapToBytes(bitmap: Bitmap?): ByteArray? {
        if (bitmap == null) return null
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}