package com.lib.camerax.utils

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * @author：luck
 * @date：2022/6/19 11:56 上午
 * @describe：BitmapUtils
 */
object BitmapUtils {
    /**
     * 水平镜像
     *
     * @param bmp
     * @return
     */
    fun toHorizontalMirror(bmp: Bitmap): Bitmap {
        val w = bmp.width
        val h = bmp.height
        val matrix = Matrix()
        matrix.postScale(-1f, 1f)
        matrix.postRotate(if (w > h) 90F else 0F)
        return Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true)
    }

    fun computeSize(srcWidth: Int, srcHeight: Int): Int {
        var srcWidth = srcWidth
        var srcHeight = srcHeight
        srcWidth = if (srcWidth % 2 == 1) srcWidth + 1 else srcWidth
        srcHeight = if (srcHeight % 2 == 1) srcHeight + 1 else srcHeight
        val longSide = Math.max(srcWidth, srcHeight)
        val shortSide = Math.min(srcWidth, srcHeight)
        val scale = shortSide.toFloat() / longSide
        return if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                1
            } else if (longSide < 4990) {
                2
            } else if (longSide > 4990 && longSide < 10240) {
                4
            } else {
                longSide / 1280
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            if (longSide / 1280 == 0) 1 else longSide / 1280
        } else {
            Math.ceil(longSide / (1280.0 / scale)).toInt()
        }
    }
}