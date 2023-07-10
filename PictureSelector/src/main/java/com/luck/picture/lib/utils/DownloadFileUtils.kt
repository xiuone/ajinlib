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
import com.luck.picture.lib.interfaces.OnCallbackListener
import com.luck.picture.lib.thread.PictureThreadUtils
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.Exception
import java.net.URL

/**
 * @author：luck
 * @date：2021/11/25 2:02 下午
 * @describe：DownloadFileUtils
 */
object DownloadFileUtils {
    /**
     * 保存文件
     *
     * @param context  上下文
     * @param path     文件路径
     * @param mimeType 文件类型
     * @param listener 结果回调监听
     */
    fun saveLocalFile(
        context: Context, path: String?, mimeType: String,
        listener: OnCallbackListener<String?>?
    ) {
        executeByIo<String>(object : PictureThreadUtils.SimpleTask<String?>() {
            override fun doInBackground(): String {
                try {
                    val uri: Uri?
                    val contentValues = ContentValues()
                    val time = ValueOf.toString(System.currentTimeMillis())
                    uri = if (isHasAudio(mimeType)) {
                        contentValues.put(
                            MediaStore.Audio.Media.DISPLAY_NAME,
                            DateUtils.getCreateFileName("AUD_")
                        )
                        contentValues.put(
                            MediaStore.Audio.Media.MIME_TYPE, if (TextUtils.isEmpty(mimeType)
                                || mimeType.startsWith(PictureMimeType.MIME_TYPE_PREFIX_VIDEO)
                                || mimeType.startsWith(PictureMimeType.MIME_TYPE_PREFIX_IMAGE)
                            ) PictureMimeType.MIME_TYPE_AUDIO else mimeType
                        )
                        if (SdkVersionUtils.isQ()) {
                            contentValues.put(MediaStore.Audio.Media.DATE_TAKEN, time)
                            contentValues.put(
                                MediaStore.Audio.Media.RELATIVE_PATH,
                                Environment.DIRECTORY_MUSIC
                            )
                        } else {
                            val dir = if (TextUtils.equals(
                                    Environment.getExternalStorageState(),
                                    Environment.MEDIA_MOUNTED
                                )
                            ) Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_MUSIC
                            ) else File(
                                FileDirMap.getFileDirPath(
                                    context,
                                    SelectMimeType.TYPE_AUDIO
                                )
                            )
                            contentValues.put(
                                MediaStore.MediaColumns.DATA, dir.absolutePath + File.separator
                                        + DateUtils.getCreateFileName("AUD_") + PictureMimeType.AMR
                            )
                        }
                        context.contentResolver.insert(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        )
                    } else if (isHasVideo(mimeType)) {
                        contentValues.put(
                            MediaStore.Video.Media.DISPLAY_NAME,
                            DateUtils.getCreateFileName("VID_")
                        )
                        contentValues.put(
                            MediaStore.Video.Media.MIME_TYPE, if (TextUtils.isEmpty(mimeType)
                                || mimeType.startsWith(PictureMimeType.MIME_TYPE_PREFIX_AUDIO)
                                || mimeType.startsWith(PictureMimeType.MIME_TYPE_PREFIX_IMAGE)
                            ) PictureMimeType.MIME_TYPE_VIDEO else mimeType
                        )
                        if (SdkVersionUtils.isQ()) {
                            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, time)
                            contentValues.put(
                                MediaStore.Video.Media.RELATIVE_PATH,
                                Environment.DIRECTORY_MOVIES
                            )
                        } else {
                            val dir = if (TextUtils.equals(
                                    Environment.getExternalStorageState(),
                                    Environment.MEDIA_MOUNTED
                                )
                            ) Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_MOVIES
                            ) else File(
                                FileDirMap.getFileDirPath(
                                    context,
                                    SelectMimeType.TYPE_VIDEO
                                )
                            )
                            contentValues.put(
                                MediaStore.MediaColumns.DATA, dir.absolutePath + File.separator
                                        + DateUtils.getCreateFileName("VID_") + PictureMimeType.MP4
                            )
                        }
                        context.contentResolver.insert(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        )
                    } else {
                        contentValues.put(
                            MediaStore.Images.Media.DISPLAY_NAME,
                            DateUtils.getCreateFileName("IMG_")
                        )
                        contentValues.put(
                            MediaStore.Images.Media.MIME_TYPE, if (TextUtils.isEmpty(mimeType)
                                || mimeType.startsWith(PictureMimeType.MIME_TYPE_PREFIX_AUDIO)
                                || mimeType.startsWith(PictureMimeType.MIME_TYPE_PREFIX_VIDEO)
                            ) PictureMimeType.MIME_TYPE_IMAGE else mimeType
                        )
                        if (SdkVersionUtils.isQ()) {
                            contentValues.put(MediaStore.Images.Media.DATE_TAKEN, time)
                            contentValues.put(
                                MediaStore.Images.Media.RELATIVE_PATH,
                                PictureMimeType.DCIM
                            )
                        } else {
                            if (isHasGif(mimeType) || isUrlHasGif(
                                    path!!
                                )
                            ) {
                                val dir = if (TextUtils.equals(
                                        Environment.getExternalStorageState(),
                                        Environment.MEDIA_MOUNTED
                                    )
                                ) Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES
                                ) else File(
                                    FileDirMap.getFileDirPath(
                                        context,
                                        SelectMimeType.TYPE_IMAGE
                                    )
                                )
                                contentValues.put(
                                    MediaStore.MediaColumns.DATA, dir.absolutePath + File.separator
                                            + DateUtils.getCreateFileName("IMG_") + PictureMimeType.GIF
                                )
                            }
                        }
                        context.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        )
                    }
                    if (uri != null) {
                        val inputStream: InputStream?
                        inputStream = if (isHasHttp(path!!)) {
                            URL(path).openStream()
                        } else {
                            if (isContent(path)) {
                                openInputStream(context, Uri.parse(path))
                            } else {
                                FileInputStream(path)
                            }
                        }
                        val outputStream = openOutputStream(context, uri)
                        if (PictureFileUtils.writeFileFromIS(inputStream, outputStream)) {
                            return PictureFileUtils.getPath(context, uri)!!
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            }

            override fun onSuccess(result: String) {
                cancel(this)
                listener?.onCall(result)
            }
        })
    }
}