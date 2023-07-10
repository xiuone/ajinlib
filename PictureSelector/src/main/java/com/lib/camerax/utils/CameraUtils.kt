package com.lib.camerax.utils

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import com.lib.camerax.utils.CameraUtils
import com.xy.base.utils.exp.getCreateFileName

/**
 * @author：luck
 * @date：2021/11/8 4:27 下午
 * @describe：CameraFileUtils
 */
object CameraUtils {
    const val TYPE_IMAGE = 1
    const val TYPE_VIDEO = 2
    const val CAMERA = "Camera"
    const val MIME_TYPE_PREFIX_IMAGE = "image"
    const val MIME_TYPE_PREFIX_VIDEO = "video"
    const val MIME_TYPE_IMAGE = "image/jpeg"
    const val MIME_TYPE_VIDEO = "video/mp4"
    const val DCIM_CAMERA = "DCIM/Camera"
    const val JPEG = ".jpeg"
    const val MP4 = ".mp4"

    /**
     * 构建图片的ContentValues,用于保存拍照后的照片
     *
     * @param cameraFileName 资源名称
     * @param mimeType       资源类型
     * @return
     */
    @JvmStatic
    fun buildImageContentValues(cameraFileName: String, mimeType: String): ContentValues {
        val time = System.currentTimeMillis().toString()
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        val values = ContentValues(3)
        if (TextUtils.isEmpty(cameraFileName)) {
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_".getCreateFileName())
        } else {
            if (cameraFileName.lastIndexOf(".") == -1) {
                values.put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_".getCreateFileName())
            } else {
                val suffix = cameraFileName.substring(cameraFileName.lastIndexOf("."))
                val fileName = cameraFileName.replace(suffix.toRegex(), "")
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            }
        }
        values.put(
            MediaStore.Images.Media.MIME_TYPE,
            if (TextUtils.isEmpty(mimeType) || mimeType.startsWith(
                    MIME_TYPE_PREFIX_VIDEO
                )
            ) MIME_TYPE_IMAGE else mimeType
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, time)
            values.put(MediaStore.Images.Media.RELATIVE_PATH, DCIM_CAMERA)
        }
        return values
    }

    /**
     * 构建视频的ContentValues,用于保存拍照后的照片
     *
     * @param cameraFileName 资源名称
     * @param mimeType       资源类型
     * @return
     */
    @JvmStatic
    fun buildVideoContentValues(cameraFileName: String, mimeType: String): ContentValues {
        val time = System.currentTimeMillis().toString()
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        val values = ContentValues(3)
        if (TextUtils.isEmpty(cameraFileName)) {
            values.put(MediaStore.Video.Media.DISPLAY_NAME, "VID_".getCreateFileName())
        } else {
            if (cameraFileName.lastIndexOf(".") == -1) {
                values.put(MediaStore.Video.Media.DISPLAY_NAME, "VID_".getCreateFileName())
            } else {
                val suffix = cameraFileName.substring(cameraFileName.lastIndexOf("."))
                val fileName = cameraFileName.replace(suffix.toRegex(), "")
                values.put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
            }
        }
        values.put(
            MediaStore.Video.Media.MIME_TYPE, if (TextUtils.isEmpty(mimeType)
                || mimeType.startsWith(MIME_TYPE_PREFIX_IMAGE)
            ) MIME_TYPE_VIDEO else mimeType
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.DATE_TAKEN, time)
            values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
        }
        return values
    }
}