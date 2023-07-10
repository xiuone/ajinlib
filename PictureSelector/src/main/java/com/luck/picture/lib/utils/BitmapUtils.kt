package com.luck.picture.lib.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import com.luck.picture.lib.config.PictureMimeType.isContent
import com.luck.picture.lib.basic.PictureContentResolver.openInputStream
import com.luck.picture.lib.basic.PictureContentResolver.openOutputStream
import com.luck.picture.lib.immersive.RomUtils.isSamsung
import com.luck.picture.lib.thread.PictureThreadUtils.executeByIo
import com.luck.picture.lib.config.PictureMimeType.isHasAudio
import com.luck.picture.lib.config.PictureMimeType.isHasVideo
import com.luck.picture.lib.config.PictureMimeType.isHasGif
import com.luck.picture.lib.config.PictureMimeType.isUrlHasGif
import com.luck.picture.lib.config.PictureMimeType.isHasHttp
import com.luck.picture.lib.thread.PictureThreadUtils.cancel
import com.luck.picture.lib.interfaces.OnCallbackListener.onCall
import com.luck.picture.lib.config.PictureMimeType.isHasImage
import com.luck.picture.lib.app.PictureAppMaster.Companion.instance
import com.luck.picture.lib.app.PictureAppMaster.appContext
import com.luck.picture.lib.config.SelectMimeType.ofImage
import com.luck.picture.lib.config.PictureMimeType.getLastSourceSuffix
import com.luck.picture.lib.thread.PictureThreadUtils.isInUiThread
import com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.utils.FileDirMap
import com.luck.picture.lib.config.SelectorConfig
import androidx.core.content.FileProvider
import kotlin.jvm.JvmOverloads
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeCompat
import androidx.exifinterface.media.ExifInterface
import com.luck.picture.lib.config.PictureConfig
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception

/**
 * @author：luck
 * @date：2020-01-15 18:22
 * @describe：BitmapUtils
 */
object BitmapUtils {
    private const val ARGB_8888_MEMORY_BYTE = 4
    private const val MAX_BITMAP_SIZE = 100 * 1024 * 1024 // 100 MB

    /**
     * 判断拍照 图片是否旋转
     *
     * @param context
     * @param path    资源路径
     */
    fun rotateImage(context: Context?, path: String?) {
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        var bitmap: Bitmap? = null
        try {
            val degree = readPictureDegree(context, path)
            if (degree > 0) {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                if (isContent(path!!)) {
                    inputStream = openInputStream(context, Uri.parse(path))
                    BitmapFactory.decodeStream(inputStream, null, options)
                } else {
                    BitmapFactory.decodeFile(path, options)
                }
                options.inSampleSize = computeSize(options.outWidth, options.outHeight)
                options.inJustDecodeBounds = false
                if (isContent(path)) {
                    inputStream = openInputStream(context, Uri.parse(path))
                    bitmap = BitmapFactory.decodeStream(inputStream, null, options)
                } else {
                    bitmap = BitmapFactory.decodeFile(path, options)
                }
                if (bitmap != null) {
                    bitmap = rotatingImage(bitmap, degree)
                    outputStream = if (isContent(path)) {
                        openOutputStream(
                            context!!, Uri.parse(path)
                        ) as FileOutputStream?
                    } else {
                        FileOutputStream(path)
                    }
                    saveBitmapFile(bitmap, outputStream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            PictureFileUtils.close(inputStream)
            PictureFileUtils.close(outputStream)
            if (bitmap != null && !bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
    }

    /**
     * 旋转Bitmap
     *
     * @param bitmap
     * @param angle
     * @return
     */
    fun rotatingImage(bitmap: Bitmap, angle: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * 保存Bitmap至本地
     *
     * @param bitmap
     * @param fos
     */
    private fun saveBitmapFile(bitmap: Bitmap?, fos: FileOutputStream?) {
        var stream: ByteArrayOutputStream? = null
        try {
            stream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 60, fos)
            fos!!.write(stream.toByteArray())
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            PictureFileUtils.close(fos)
            PictureFileUtils.close(stream)
        }
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param context
     * @param filePath 图片绝对路径
     * @return degree旋转的角度
     */
    fun readPictureDegree(context: Context?, filePath: String?): Int {
        val exifInterface: ExifInterface
        var inputStream: InputStream? = null
        return try {
            if (isContent(filePath!!)) {
                inputStream = openInputStream(context, Uri.parse(filePath))
                exifInterface = ExifInterface(inputStream!!)
            } else {
                exifInterface = ExifInterface(filePath)
            }
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        } finally {
            PictureFileUtils.close(inputStream)
        }
    }

    /**
     * 获取图片的缩放比例
     *
     * @param imageWidth  图片原始宽度
     * @param imageHeight 图片原始高度
     * @return
     */
    fun getMaxImageSize(imageWidth: Int, imageHeight: Int): IntArray {
        var maxWidth = PictureConfig.UNSET
        var maxHeight = PictureConfig.UNSET
        if (imageWidth == 0 && imageHeight == 0) {
            return intArrayOf(maxWidth, maxHeight)
        }
        var inSampleSize = computeSize(imageWidth, imageHeight)
        val totalMemory = totalMemory
        var decodeAttemptSuccess = false
        while (!decodeAttemptSuccess) {
            maxWidth = imageWidth / inSampleSize
            maxHeight = imageHeight / inSampleSize
            val bitmapSize = maxWidth * maxHeight * ARGB_8888_MEMORY_BYTE
            if (bitmapSize > totalMemory) {
                inSampleSize *= 2
                continue
            }
            decodeAttemptSuccess = true
        }
        return intArrayOf(maxWidth, maxHeight)
    }

    /**
     * 获取当前应用可用内存
     *
     * @return
     */
    val totalMemory: Long
        get() {
            val totalMemory = Runtime.getRuntime().totalMemory()
            return if (totalMemory > MAX_BITMAP_SIZE) MAX_BITMAP_SIZE.toLong() else totalMemory
        }

    /**
     * 计算图片合适压缩比较
     *
     * @param srcWidth  资源宽度
     * @param srcHeight 资源高度
     * @return
     */
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