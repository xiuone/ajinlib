package com.xy.base.utils.exp

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import java.io.File
import java.io.FileOutputStream





fun Context.getSizeLocalPath(path:String?):IntArray?{
    if (path == null) {
        return intArrayOf(0, 0)
    }else if (!path.isFileExist()) {
        return intArrayOf(0, 0)
    }
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    options.inPreferredConfig = Bitmap.Config.ARGB_8888
    val inSampleSize = calculateInSampleSize(options,
            resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels)
    options.inSampleSize = inSampleSize
    options.inJustDecodeBounds = false
    val bitmap = BitmapFactory.decodeFile(path, options)
    return intArrayOf(bitmap.width,bitmap.height)
}

private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}







fun Bitmap.saveBitmap(context: Context): String? {
    val path = context.filesDir.toString() + File.separator +"temp_image"+ System.currentTimeMillis() + ".png"
    path.createDirs()
    return saveBitmap(path)
}

fun Bitmap.saveBitmap(path: String?): String? {
    path?.createDirs()
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
fun Bitmap.saveImageToGallery(context: Context, strRes:String) :String{
    val fileName = "IMG_" + System.currentTimeMillis() + ".PNG"
    // 保存图片至指定路径
    val storePath = context.getSdImagePath(strRes, fileName)
    storePath.createDirs()
    val file = File(storePath)
    val saveImgOut = FileOutputStream(file)
    // compress - 压缩的意思
    compress(Bitmap.CompressFormat.PNG, 100, saveImgOut)
    //存储完成后需要清除相关的进程
    saveImgOut.flush()
    saveImgOut.closeRe()
    context.insertImage(storePath)
    return storePath
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


fun Bitmap.cropBitmap(startX: Int, startY: Int, width: Int, height: Int): Bitmap {
    var startX = startX
    var startY = startY
    var width = width
    var height = height
    val w = this.width // 得到图片的宽，高
    val h = this.height
    if (startX < 0) startX = 0
    if (startX + width > w) {
        startX = 0
    }
    if (startX + width > w) {
        width = w
    }
    if (startY < 0) startY = 0
    if (startY + height > h) {
        startY = 0
    }
    if (startX + height > h) {
        height = h
    }
    return Bitmap.createBitmap(this, startX, startY, width, height, null, false)
}


fun Bitmap.blur( radius: Int, canReuseInBitmap: Boolean): Bitmap? {
    val bitmap: Bitmap = if (canReuseInBitmap) { this } else { copy(config, true) }
    if (radius < 1) {
        return null
    }
    val w = bitmap.width
    val h = bitmap.height
    val pix = IntArray(w * h)
    bitmap.getPixels(pix, 0, w, 0, 0, w, h)
    val wm = w - 1
    val hm = h - 1
    val wh = w * h
    val div = radius + radius + 1
    val r = IntArray(wh)
    val g = IntArray(wh)
    val b = IntArray(wh)
    var rsum: Int
    var gsum: Int
    var bsum: Int
    var x: Int
    var y: Int
    var i: Int
    var p: Int
    var yp: Int
    var yi: Int
    var yw: Int
    val vmin = IntArray(w.coerceAtLeast(h))
    var divsum = div + 1 shr 1
    divsum *= divsum
    val dv = IntArray(256 * divsum)
    i = 0
    while (i < 256 * divsum) {
        dv[i] = i / divsum
        i++
    }
    yi = 0
    yw = yi
    val stack = Array(div) { IntArray(3) }
    var stackpointer: Int
    var stackstart: Int
    var sir: IntArray
    var rbs: Int
    val r1 = radius + 1
    var routsum: Int
    var goutsum: Int
    var boutsum: Int
    var rinsum: Int
    var ginsum: Int
    var binsum: Int
    y = 0
    while (y < h) {
        bsum = 0
        gsum = bsum
        rsum = gsum
        boutsum = rsum
        goutsum = boutsum
        routsum = goutsum
        binsum = routsum
        ginsum = binsum
        rinsum = ginsum
        i = -radius
        while (i <= radius) {
            p = pix[yi + Math.min(wm, Math.max(i, 0))]
            sir = stack[i + radius]
            sir[0] = p and 0xff0000 shr 16
            sir[1] = p and 0x00ff00 shr 8
            sir[2] = p and 0x0000ff
            rbs = r1 - Math.abs(i)
            rsum += sir[0] * rbs
            gsum += sir[1] * rbs
            bsum += sir[2] * rbs
            if (i > 0) {
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
            } else {
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
            }
            i++
        }
        stackpointer = radius
        x = 0
        while (x < w) {
            r[yi] = dv[rsum]
            g[yi] = dv[gsum]
            b[yi] = dv[bsum]
            rsum -= routsum
            gsum -= goutsum
            bsum -= boutsum
            stackstart = stackpointer - radius + div
            sir = stack[stackstart % div]
            routsum -= sir[0]
            goutsum -= sir[1]
            boutsum -= sir[2]
            if (y == 0) {
                vmin[x] = Math.min(x + radius + 1, wm)
            }
            p = pix[yw + vmin[x]]
            sir[0] = p and 0xff0000 shr 16
            sir[1] = p and 0x00ff00 shr 8
            sir[2] = p and 0x0000ff
            rinsum += sir[0]
            ginsum += sir[1]
            binsum += sir[2]
            rsum += rinsum
            gsum += ginsum
            bsum += binsum
            stackpointer = (stackpointer + 1) % div
            sir = stack[stackpointer % div]
            routsum += sir[0]
            goutsum += sir[1]
            boutsum += sir[2]
            rinsum -= sir[0]
            ginsum -= sir[1]
            binsum -= sir[2]
            yi++
            x++
        }
        yw += w
        y++
    }
    x = 0
    while (x < w) {
        bsum = 0
        gsum = bsum
        rsum = gsum
        boutsum = rsum
        goutsum = boutsum
        routsum = goutsum
        binsum = routsum
        ginsum = binsum
        rinsum = ginsum
        yp = -radius * w
        i = -radius
        while (i <= radius) {
            yi = Math.max(0, yp) + x
            sir = stack[i + radius]
            sir[0] = r[yi]
            sir[1] = g[yi]
            sir[2] = b[yi]
            rbs = r1 - Math.abs(i)
            rsum += r[yi] * rbs
            gsum += g[yi] * rbs
            bsum += b[yi] * rbs
            if (i > 0) {
                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]
            } else {
                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]
            }
            if (i < hm) {
                yp += w
            }
            i++
        }
        yi = x
        stackpointer = radius
        y = 0
        while (y < h) {

            // Preserve alpha channel: ( 0xff000000 & pix[yi] )
            pix[yi] =
                -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]
            rsum -= routsum
            gsum -= goutsum
            bsum -= boutsum
            stackstart = stackpointer - radius + div
            sir = stack[stackstart % div]
            routsum -= sir[0]
            goutsum -= sir[1]
            boutsum -= sir[2]
            if (x == 0) {
                vmin[y] = Math.min(y + r1, hm) * w
            }
            p = x + vmin[y]
            sir[0] = r[p]
            sir[1] = g[p]
            sir[2] = b[p]
            rinsum += sir[0]
            ginsum += sir[1]
            binsum += sir[2]
            rsum += rinsum
            gsum += ginsum
            bsum += binsum
            stackpointer = (stackpointer + 1) % div
            sir = stack[stackpointer]
            routsum += sir[0]
            goutsum += sir[1]
            boutsum += sir[2]
            rinsum -= sir[0]
            ginsum -= sir[1]
            binsum -= sir[2]
            yi += w
            y++
        }
        x++
    }
    bitmap.setPixels(pix, 0, w, 0, 0, w, h)
    return bitmap
}