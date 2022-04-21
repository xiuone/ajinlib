package com.xy.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.StringRes
import java.io.File
import java.io.FileOutputStream

fun Bitmap.saveBitmap(context: Context): String? {
    val path = context.filesDir.toString() + File.separator +"temp_image"+ System.currentTimeMillis() + ".png"
    path.createDirs()
    return saveBitmap(path)
}

fun Bitmap.saveBitmap(path: String?): String? {
    path?.deleteFile()
    var fos: FileOutputStream? = null
    try {
        val file = File(path)
        file.createNewFile()
        fos = FileOutputStream(file)
        compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        return path
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        fos?.closeRe()
    }
    return null
}

/**
 * 保存图片到指定路径
 * @param context
 * @param *bitmap   要保存的图片
 */
fun Bitmap.saveImageToGallery(context: Context,@StringRes strRes:Int) {
    val fileName = "IMG_" + System.currentTimeMillis() + ".PNG"
    // 保存图片至指定路径
    val storePath = getSdImagePath(context,strRes, fileName)
    storePath.createDirs()
    val file = File(storePath)
    val saveImgOut = FileOutputStream(file)
    // compress - 压缩的意思
    compress(Bitmap.CompressFormat.PNG, 100, saveImgOut)
    //存储完成后需要清除相关的进程
    saveImgOut.flush()
    saveImgOut.closeRe()
    context.insertImage(storePath)
}


/**
 * 将颜色变成bitmap并设置大小
 */
fun getBitmapFromColor(colorRes: Int,width:Int,height:Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.eraseColor(colorRes) //填充颜色
    return bitmap
}