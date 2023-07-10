package com.luck.picture.lib.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.webkit.MimeTypeMap
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
import com.luck.picture.lib.entity.MediaExtraInfo
import com.luck.picture.lib.interfaces.OnCallbackListener
import com.luck.picture.lib.thread.PictureThreadUtils
import java.io.*
import java.lang.Exception
import java.net.URLConnection
import java.util.*

/**
 * @author：luck
 * @date：2019-10-21 17:10
 * @describe：资源处理工具类
 */
object MediaUtils {
    /**
     * get uri
     *
     * @param id
     * @return
     */
    fun getRealPathUri(id: Long, mimeType: String?): String {
        val contentUri: Uri
        contentUri = if (isHasImage(mimeType)) {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else if (isHasVideo(mimeType)) {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else if (isHasAudio(mimeType)) {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Files.getContentUri("external")
        }
        return ContentUris.withAppendedId(contentUri, id).toString()
    }

    /**
     * 获取mimeType
     *
     * @param path
     * @return
     */
    @kotlin.jvm.JvmStatic
    fun getMimeTypeFromMediaUrl(path: String?): String {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(path)
        var mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            fileExtension.lowercase(Locale.getDefault())
        )
        if (TextUtils.isEmpty(mimeType)) {
            mimeType = getMimeType(File(path))
        }
        return if (TextUtils.isEmpty(mimeType)) PictureMimeType.MIME_TYPE_JPEG else mimeType!!
    }

    /**
     * 获取mimeType
     *
     * @param url
     * @return
     */
    @kotlin.jvm.JvmStatic
    fun getMimeTypeFromMediaHttpUrl(url: String): String? {
        if (TextUtils.isEmpty(url)) {
            return null
        }
        if (url.lowercase(Locale.getDefault())
                .endsWith(".jpg") || url.lowercase(Locale.getDefault()).endsWith(".jpeg")
        ) {
            return "image/jpeg"
        } else if (url.lowercase(Locale.getDefault()).endsWith(".png")) {
            return "image/png"
        } else if (url.lowercase(Locale.getDefault()).endsWith(".gif")) {
            return "image/gif"
        } else if (url.lowercase(Locale.getDefault()).endsWith(".webp")) {
            return "image/webp"
        } else if (url.lowercase(Locale.getDefault()).endsWith(".bmp")) {
            return "image/bmp"
        } else if (url.lowercase(Locale.getDefault()).endsWith(".mp4")) {
            return "video/mp4"
        } else if (url.lowercase(Locale.getDefault()).endsWith(".avi")) {
            return "video/avi"
        } else if (url.lowercase(Locale.getDefault()).endsWith(".mp3")) {
            return "audio/mpeg"
        } else if (url.lowercase(Locale.getDefault()).endsWith(".amr")) {
            return "audio/amr"
        } else if (url.lowercase(Locale.getDefault()).endsWith(".m4a")) {
            return "audio/mpeg"
        }
        return null
    }

    /**
     * 获取mimeType
     *
     * @param file
     * @return
     */
    private fun getMimeType(file: File): String {
        val fileNameMap = URLConnection.getFileNameMap()
        return fileNameMap.getContentTypeFor(file.name)
    }

    /**
     * 是否是长图
     *
     * @param width  图片宽度
     * @param height 图片高度
     * @return
     */
    fun isLongImage(width: Int, height: Int): Boolean {
        return if (width <= 0 || height <= 0) {
            false
        } else height > width * 3
    }

    /**
     * 创建目录名
     *
     * @param absolutePath 资源路径
     * @return
     */
    @kotlin.jvm.JvmStatic
    fun generateCameraFolderName(absolutePath: String?): String {
        val folderName: String
        val cameraFile = File(absolutePath)
        folderName = if (cameraFile.parentFile != null) {
            cameraFile.parentFile.name
        } else {
            PictureMimeType.CAMERA
        }
        return folderName
    }

    /**
     * get Local image width or height
     *
     *
     * Use []
     *
     * @param url
     * @return
     */
    @Deprecated("")
    fun getImageSize(url: String?): MediaExtraInfo {
        val mediaExtraInfo = MediaExtraInfo()
        var inputStream: InputStream? = null
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            inputStream = if (isContent(url!!)) {
                openInputStream(instance!!.appContext, Uri.parse(url))
            } else {
                FileInputStream(url)
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            mediaExtraInfo.width = options.outWidth
            mediaExtraInfo.height = options.outHeight
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            PictureFileUtils.close(inputStream)
        }
        return mediaExtraInfo
    }

    /**
     * get Local image width or height
     *
     * @param url
     * @return
     */
    fun getImageSize(context: Context?, url: String?): MediaExtraInfo {
        val mediaExtraInfo = MediaExtraInfo()
        if (isHasHttp(url!!)) {
            return mediaExtraInfo
        }
        var inputStream: InputStream? = null
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            inputStream = if (isContent(url)) {
                openInputStream(context, Uri.parse(url))
            } else {
                FileInputStream(url)
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            mediaExtraInfo.width = options.outWidth
            mediaExtraInfo.height = options.outHeight
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            PictureFileUtils.close(inputStream)
        }
        return mediaExtraInfo
    }

    /**
     * get Local image width or height
     *
     * @param context
     * @param url
     * @param call
     */
    fun getImageSize(context: Context?, url: String?, call: OnCallbackListener<MediaExtraInfo?>?) {
        executeByIo<MediaExtraInfo>(
            object : PictureThreadUtils.SimpleTask<MediaExtraInfo?>() {
                override fun doInBackground(): MediaExtraInfo {
                    return getImageSize(context, url)
                }

                override fun onSuccess(result: MediaExtraInfo) {
                    cancel(this)
                    call?.onCall(result)
                }
            }
        )
    }

    /**
     * get Local video width or height
     *
     * @param context
     * @param url
     * @return
     */
    fun getVideoSize(context: Context?, url: String?, call: OnCallbackListener<MediaExtraInfo?>?) {
        executeByIo<MediaExtraInfo>(
            object : PictureThreadUtils.SimpleTask<MediaExtraInfo?>() {
                override fun doInBackground(): MediaExtraInfo {
                    return getVideoSize(context, url)
                }

                override fun onSuccess(result: MediaExtraInfo) {
                    cancel(this)
                    call?.onCall(result)
                }
            }
        )
    }

    /**
     * get Local video width or height
     *
     * @param context
     * @param url
     * @return
     */
    @kotlin.jvm.JvmStatic
    fun getVideoSize(context: Context?, url: String?): MediaExtraInfo {
        val mediaExtraInfo = MediaExtraInfo()
        if (isHasHttp(url!!)) {
            return mediaExtraInfo
        }
        val retriever = MediaMetadataRetriever()
        try {
            if (isContent(url)) {
                retriever.setDataSource(context, Uri.parse(url))
            } else {
                retriever.setDataSource(url)
            }
            val orientation =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
            val width: Int
            val height: Int
            if (TextUtils.equals("90", orientation) || TextUtils.equals("270", orientation)) {
                height =
                    toInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
                width =
                    toInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
            } else {
                width =
                    toInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
                height =
                    toInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
            }
            mediaExtraInfo.width = width
            mediaExtraInfo.height = height
            mediaExtraInfo.orientation = orientation
            mediaExtraInfo.duration =
                toLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return mediaExtraInfo
    }

    /**
     * get Local video width or height
     *
     * @param context
     * @param url
     * @return
     */
    @kotlin.jvm.JvmStatic
    fun getAudioSize(context: Context?, url: String?): MediaExtraInfo {
        val mediaExtraInfo = MediaExtraInfo()
        if (isHasHttp(url!!)) {
            return mediaExtraInfo
        }
        val retriever = MediaMetadataRetriever()
        try {
            if (isContent(url)) {
                retriever.setDataSource(context, Uri.parse(url))
            } else {
                retriever.setDataSource(url)
            }
            mediaExtraInfo.duration =
                toLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return mediaExtraInfo
    }

    /**
     * 删除部分手机 拍照在DCIM也生成一张的问题
     *
     * @param id
     */
    fun removeMedia(context: Context, id: Int) {
        try {
            val cr = context.applicationContext.contentResolver
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val selection = MediaStore.Images.Media._ID + "=?"
            cr.delete(uri, selection, arrayOf(java.lang.Long.toString(id.toLong())))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取DCIM文件下最新一条拍照记录
     *
     * @return
     */
    fun getDCIMLastImageId(context: Context, absoluteDir: String): Int {
        var data: Cursor? = null
        return try {
            //selection: 指定查询条件
            val selection = MediaStore.Images.Media.DATA + " like ?"
            //定义selectionArgs：
            val selectionArgs = arrayOf("%$absoluteDir%")
            data = if (SdkVersionUtils.isR()) {
                val queryArgs = createQueryArgsBundle(
                    selection,
                    selectionArgs,
                    1,
                    0,
                    MediaStore.Files.FileColumns._ID + " DESC"
                )
                context.applicationContext.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    queryArgs,
                    null
                )
            } else {
                val orderBy = MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0"
                context.applicationContext.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    selection,
                    selectionArgs,
                    orderBy
                )
            }
            if (data != null && data.count > 0 && data.moveToFirst()) {
                val id = data.getInt(data.getColumnIndex(MediaStore.Images.Media._ID))
                val date = data.getLong(data.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
                val duration = DateUtils.dateDiffer(date)
                // 最近时间1s以内的图片，可以判定是最新生成的重复照片
                if (duration <= 1) id else -1
            } else {
                -1
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        } finally {
            data?.close()
        }
    }

    /**
     * getPathMediaBucketId
     *
     * @return
     */
    @kotlin.jvm.JvmStatic
    fun getPathMediaBucketId(context: Context, absolutePath: String): Array<Long> {
        val mediaBucketId = arrayOf(0L, 0L)
        var data: Cursor? = null
        try {
            //selection: 指定查询条件
            val selection = MediaStore.Files.FileColumns.DATA + " like ?"
            //定义selectionArgs：
            val selectionArgs = arrayOf("%$absolutePath%")
            data = if (SdkVersionUtils.isR()) {
                val queryArgs = createQueryArgsBundle(
                    selection,
                    selectionArgs,
                    1,
                    0,
                    MediaStore.Files.FileColumns._ID + " DESC"
                )
                context.contentResolver.query(
                    MediaStore.Files.getContentUri("external"),
                    null,
                    queryArgs,
                    null
                )
            } else {
                val orderBy = MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0"
                context.contentResolver.query(
                    MediaStore.Files.getContentUri("external"),
                    null,
                    selection,
                    selectionArgs,
                    orderBy
                )
            }
            if (data != null && data.count > 0 && data.moveToFirst()) {
                mediaBucketId[0] =
                    data.getLong(data.getColumnIndex(MediaStore.Files.FileColumns._ID))
                mediaBucketId[1] = data.getLong(data.getColumnIndex("bucket_id"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            data?.close()
        }
        return mediaBucketId
    }

    /**
     * Key for an SQL style `LIMIT` string that may be present in the
     * query Bundle argument passed to
     * [ContentProvider.query].
     *
     *
     * **Apps targeting [android.os.Build.VERSION_CODES.O] or higher are strongly
     * encourage to use structured query arguments in lieu of opaque SQL query clauses.**
     *
     * @see .QUERY_ARG_LIMIT
     *
     * @see .QUERY_ARG_OFFSET
     */
    const val QUERY_ARG_SQL_LIMIT = "android:query-arg-sql-limit"

    /**
     * R  createQueryArgsBundle
     *
     * @param selection
     * @param selectionArgs
     * @param limitCount
     * @param offset
     * @return
     */
    fun createQueryArgsBundle(
        selection: String?,
        selectionArgs: Array<String>?,
        limitCount: Int,
        offset: Int,
        orderBy: String?
    ): Bundle {
        val queryArgs = Bundle()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, orderBy)
            if (SdkVersionUtils.isR()) {
                queryArgs.putString(
                    ContentResolver.QUERY_ARG_SQL_LIMIT,
                    "$limitCount offset $offset"
                )
            }
        }
        return queryArgs
    }

    /**
     * 异步获取视频缩略图地址
     *
     * @param context
     * @param url
     * @param call
     * @return
     */
    fun getAsyncVideoThumbnail(
        context: Context,
        url: String?,
        call: OnCallbackListener<MediaExtraInfo?>?
    ) {
        executeByIo<MediaExtraInfo>(
            object : PictureThreadUtils.SimpleTask<MediaExtraInfo?>() {
                override fun doInBackground(): MediaExtraInfo {
                    return getVideoThumbnail(context, url)
                }

                override fun onSuccess(result: MediaExtraInfo) {
                    cancel(this)
                    call?.onCall(result)
                }
            }
        )
    }

    /**
     * 获取视频缩略图地址
     *
     * @param context
     * @param url
     * @return
     */
    fun getVideoThumbnail(context: Context, url: String?): MediaExtraInfo {
        var bitmap: Bitmap? = null
        var stream: ByteArrayOutputStream? = null
        var fos: FileOutputStream? = null
        val extraInfo = MediaExtraInfo()
        try {
            val mmr = MediaMetadataRetriever()
            if (isContent(url!!)) {
                mmr.setDataSource(context, Uri.parse(url))
            } else {
                mmr.setDataSource(url)
            }
            bitmap = mmr.frameAtTime
            if (bitmap != null && !bitmap.isRecycled) {
                stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                val videoThumbnailDir = PictureFileUtils.getVideoThumbnailDir(context)
                val targetFile =
                    File(videoThumbnailDir, DateUtils.getCreateFileName("vid_") + "_thumb.jpg")
                fos = FileOutputStream(targetFile)
                fos.write(stream.toByteArray())
                fos.flush()
                extraInfo.videoThumbnail = targetFile.absolutePath
                extraInfo.width = bitmap.width
                extraInfo.height = bitmap.height
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            PictureFileUtils.close(stream)
            PictureFileUtils.close(fos)
            if (bitmap != null && !bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
        return extraInfo
    }

    /**
     * delete camera PATH
     *
     * @param context Context
     * @param path    path
     */
    fun deleteUri(context: Context, path: String?) {
        try {
            if (!TextUtils.isEmpty(path) && isContent(
                    path!!
                )
            ) {
                context.contentResolver.delete(Uri.parse(path), null, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}