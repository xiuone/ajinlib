package com.xy.base.utils.exp

import android.graphics.Bitmap
import android.graphics.Canvas

/**
 * 设置水印图片在左上角
 *
 * @param context     上下文
 * @param src
 * @param watermark
 * @param paddingLeft
 * @param paddingTop
 * @return
 */
fun Bitmap?.createWaterMaskLeftTop(watermark: Bitmap?, paddingLeft: Int, paddingTop: Int): Bitmap? {
    if (this == null) {
        return null
    }
    if (watermark == null){
        return this
    }
    return createWaterMaskBitmap(watermark, paddingLeft, paddingTop)
}


/**
 * 设置水印图片到右上角
 *
 * @param context
 * @param src
 * @param watermark
 * @param paddingRight
 * @param paddingTop
 * @return
 */
fun Bitmap?.createWaterMaskRightTop(watermark: Bitmap?, paddingRight: Int, paddingTop: Int): Bitmap? {
    if (this == null) {
        return null
    }
    if (watermark == null){
        return this
    }
    val paddingLeft = width - watermark.width - paddingRight
    return createWaterMaskBitmap( watermark, paddingLeft,paddingTop)
}

private fun Bitmap.createWaterMaskBitmap(watermark: Bitmap, paddingLeft: Int, paddingTop: Int, ): Bitmap? {
    val width = width
    val height = height
    val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(newBitmap)
    canvas.drawBitmap(this, 0f, 0f, null)
    canvas.drawBitmap(watermark, paddingLeft.toFloat(), paddingTop.toFloat(), null)
    canvas.save()
    canvas.restore()
    return newBitmap
}
