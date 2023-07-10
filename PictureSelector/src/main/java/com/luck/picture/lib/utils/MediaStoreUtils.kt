package com.luck.picture.lib.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
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
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType

/**
 * @author：luck
 * @date：2021/11/8 4:27 下午
 * @describe：CameraFileUtils
 */
object MediaStoreUtils {
    /**
     * 拍摄图片地址
     *
     * @param context 上下文
     * @param config  PictureSelector配制类
     * @return
     */
    fun createCameraOutImageUri(context: Context, config: SelectorConfig): Uri? {
        val imageUri: Uri?
        val cameraFileName: String?
        cameraFileName = if (TextUtils.isEmpty(config.outPutCameraImageFileName)) {
            ""
        } else {
            if (config.isOnlyCamera) config.outPutCameraImageFileName else System.currentTimeMillis()
                .toString() + "_" + config.outPutCameraImageFileName
        }
        if (SdkVersionUtils.isQ() && TextUtils.isEmpty(config.outPutCameraDir)) {
            imageUri = createImageUri(context, cameraFileName, config.cameraImageFormatForQ)
            config.cameraPath = imageUri?.toString() ?: ""
        } else {
            val cameraFile = PictureFileUtils.createCameraFile(
                context, SelectMimeType.TYPE_IMAGE,
                cameraFileName, config.cameraImageFormat, config.outPutCameraDir
            )
            config.cameraPath = cameraFile!!.absolutePath
            imageUri = PictureFileUtils.parUri(context, cameraFile)
        }
        return imageUri
    }

    /**
     * 拍摄视频地址
     *
     * @param context 上下文
     * @param config  PictureSelector配制类
     * @return
     */
    fun createCameraOutVideoUri(context: Context, config: SelectorConfig): Uri? {
        val videoUri: Uri?
        val cameraFileName: String?
        cameraFileName = if (TextUtils.isEmpty(config.outPutCameraVideoFileName)) {
            ""
        } else {
            if (config.isOnlyCamera) config.outPutCameraVideoFileName else System.currentTimeMillis()
                .toString() + "_" + config.outPutCameraVideoFileName
        }
        if (SdkVersionUtils.isQ() && TextUtils.isEmpty(config.outPutCameraDir)) {
            videoUri = createVideoUri(context, cameraFileName, config.cameraVideoFormatForQ)
            config.cameraPath = videoUri?.toString() ?: ""
        } else {
            val cameraFile = PictureFileUtils.createCameraFile(
                context, SelectMimeType.TYPE_VIDEO,
                cameraFileName, config.cameraVideoFormat, config.outPutCameraDir
            )
            config.cameraPath = cameraFile!!.absolutePath
            videoUri = PictureFileUtils.parUri(context, cameraFile)
        }
        return videoUri
    }

    /**
     * 创建一条图片地址uri,用于保存拍照后的照片
     *
     * @param ctx            上下文
     * @param cameraFileName 资源名称
     * @param mimeType       资源类型
     * @return
     */
    fun createImageUri(ctx: Context, cameraFileName: String?, mimeType: String?): Uri? {
        val context = ctx.applicationContext
        val imageFilePath = arrayOf<Uri?>(null)
        val status = Environment.getExternalStorageState()
        val contentValues = buildImageContentValues(cameraFileName, mimeType)
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status == Environment.MEDIA_MOUNTED) {
            imageFilePath[0] = context.contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            imageFilePath[0] = context.contentResolver
                .insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, contentValues)
        }
        return imageFilePath[0]
    }

    /**
     * 构建图片的ContentValues,用于保存拍照后的照片
     *
     * @param customFileName 资源名称
     * @param mimeType       资源类型
     * @return
     */
    fun buildImageContentValues(customFileName: String?, mimeType: String?): ContentValues {
        val time = ValueOf.toString(System.currentTimeMillis())
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        val values = ContentValues(3)
        if (TextUtils.isEmpty(customFileName)) {
            values.put(MediaStore.Images.Media.DISPLAY_NAME, DateUtils.getCreateFileName("IMG_"))
        } else {
            if (customFileName!!.lastIndexOf(".") == -1) {
                values.put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    DateUtils.getCreateFileName("IMG_")
                )
            } else {
                val suffix = customFileName.substring(customFileName.lastIndexOf("."))
                val fileName = customFileName.replace(suffix.toRegex(), "")
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            }
        }
        values.put(
            MediaStore.Images.Media.MIME_TYPE,
            if (TextUtils.isEmpty(mimeType) || mimeType!!.startsWith(
                    PictureMimeType.MIME_TYPE_PREFIX_VIDEO
                )
            ) PictureMimeType.MIME_TYPE_IMAGE else mimeType
        )
        if (SdkVersionUtils.isQ()) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, time)
            values.put(MediaStore.Images.Media.RELATIVE_PATH, PictureMimeType.DCIM)
        }
        return values
    }

    /**
     * 创建一条视频地址uri,用于保存录制的视频
     *
     * @param ctx            上下文
     * @param cameraFileName 资源名称
     * @param mimeType       资源类型
     * @return
     */
    fun createVideoUri(ctx: Context, cameraFileName: String?, mimeType: String?): Uri? {
        val context = ctx.applicationContext
        val imageFilePath = arrayOf<Uri?>(null)
        val status = Environment.getExternalStorageState()
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        val contentValues = buildVideoContentValues(cameraFileName, mimeType)
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status == Environment.MEDIA_MOUNTED) {
            imageFilePath[0] = context.contentResolver
                .insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            imageFilePath[0] = context.contentResolver
                .insert(MediaStore.Video.Media.INTERNAL_CONTENT_URI, contentValues)
        }
        return imageFilePath[0]
    }

    /**
     * 构建视频的ContentValues,用于保存拍照后的照片
     *
     * @param customFileName 资源名称
     * @param mimeType       资源类型
     * @return
     */
    fun buildVideoContentValues(customFileName: String?, mimeType: String?): ContentValues {
        val time = ValueOf.toString(System.currentTimeMillis())
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        val values = ContentValues(3)
        if (TextUtils.isEmpty(customFileName)) {
            values.put(MediaStore.Video.Media.DISPLAY_NAME, DateUtils.getCreateFileName("VID_"))
        } else {
            if (customFileName!!.lastIndexOf(".") == -1) {
                values.put(MediaStore.Video.Media.DISPLAY_NAME, DateUtils.getCreateFileName("VID_"))
            } else {
                val suffix = customFileName.substring(customFileName.lastIndexOf("."))
                val fileName = customFileName.replace(suffix.toRegex(), "")
                values.put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
            }
        }
        values.put(
            MediaStore.Video.Media.MIME_TYPE,
            if (TextUtils.isEmpty(mimeType) || mimeType!!.startsWith(
                    PictureMimeType.MIME_TYPE_PREFIX_IMAGE
                )
            ) PictureMimeType.MIME_TYPE_VIDEO else mimeType
        )
        if (SdkVersionUtils.isQ()) {
            values.put(MediaStore.Video.Media.DATE_TAKEN, time)
            values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
        }
        return values
    }
}