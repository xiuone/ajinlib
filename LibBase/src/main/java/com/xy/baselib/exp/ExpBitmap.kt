package com.xy.baselib.exp

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import java.io.File
import java.io.FileOutputStream

fun Bitmap.saveBitmap(context: Context): String? {
    val path = context.filesDir.toString() + File.separator +"temp_image"+ System.currentTimeMillis() + ".png"
    path.createDirs()
    return saveBitmap(path)
}

fun Bitmap.saveBitmap(path: String?): String? {
    path?.deleteFile()?:return null
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

/**
 * 获取bitmap某一点的颜色
 */
fun Bitmap.getBitmapColor(context: Context,x:Int,y:Int,@ColorRes defaultColor: Int): Int {
    if (this.width<=0 || this.height<=0)return context.getResColor(defaultColor)
    val currentX = if (x < 0) 0 else if (x > width) width else x
    val currentY = if (y < 0) 0 else if (y > height) width else y
    return getPixel(currentX,currentY)
}

fun Drawable.resizeDrawable(width: Int, height: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val cas = Canvas(bitmap)
    setBounds(0, 0, width, height)
    draw(cas)
    return bitmap
}

/**
 * @param bitmap
 * @param pixels
 * @return
 */
fun Bitmap.toRoundCorner(pixels: Int): Bitmap {
    val output = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val color = -0xbdbdbe
    val paint = Paint()
    val rect = Rect(0, 0, width, height)
    val rectF = RectF(rect)
    val roundPx = pixels.toFloat()
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, rect, rect, paint)
    return output
}