package com.luck.picture.lib.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
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
import com.luck.picture.lib.config.FileSizeUnit
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectMimeType
import java.io.*
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.nio.channels.FileChannel
import java.util.*

/**
 * @author：luck
 * @date：2017-5-30 19:30
 * @describe：PictureFileUtils
 */
object PictureFileUtils {
    private const val BYTE_SIZE = 1024
    private const val POSTFIX_JPG = ".jpg"
    private const val POSTFIX_MP4 = ".mp4"
    private const val POSTFIX_AMR = ".amr"

    /**
     * @param context
     * @param chooseMode
     * @param format
     * @param outCameraDirectory
     * @return
     */
    fun createCameraFile(
        context: Context,
        chooseMode: Int,
        fileName: String?,
        format: String?,
        outCameraDirectory: String?
    ): File {
        return createMediaFile(context, chooseMode, fileName, format, outCameraDirectory)
    }

    /**
     * 创建文件
     *
     * @param context
     * @param chooseMode
     * @param fileName
     * @param format
     * @param outCameraDirectory
     * @return
     */
    private fun createMediaFile(
        context: Context,
        chooseMode: Int,
        fileName: String?,
        format: String?,
        outCameraDirectory: String?
    ): File {
        return createOutFile(context, chooseMode, fileName, format, outCameraDirectory)
    }

    /**
     * 创建文件
     *
     * @param ctx                上下文
     * @param chooseMode         选择模式
     * @param fileName           文件名
     * @param format             文件格式
     * @param outCameraDirectory 输出目录
     * @return
     */
    private fun createOutFile(
        ctx: Context,
        chooseMode: Int,
        fileName: String?,
        format: String?,
        outCameraDirectory: String?
    ): File {
        val context = ctx.applicationContext
        val folderDir: File
        if (TextUtils.isEmpty(outCameraDirectory)) {
            // 外部没有自定义拍照存储路径使用默认
            val rootDir: File
            if (TextUtils.equals(
                    Environment.MEDIA_MOUNTED,
                    Environment.getExternalStorageState()
                )
            ) {
                rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                folderDir =
                    File(rootDir.absolutePath + File.separator + PictureMimeType.CAMERA + File.separator)
            } else {
                rootDir = getRootDirFile(context, chooseMode)
                folderDir = File(rootDir.absolutePath + File.separator)
            }
            if (!rootDir.exists()) {
                rootDir.mkdirs()
            }
        } else {
            // 自定义存储路径
            folderDir = File(outCameraDirectory)
            if (!Objects.requireNonNull(folderDir.parentFile).exists()) {
                folderDir.parentFile.mkdirs()
            }
        }
        if (!folderDir.exists()) {
            folderDir.mkdirs()
        }
        val isOutFileNameEmpty = TextUtils.isEmpty(fileName)
        return when (chooseMode) {
            SelectMimeType.TYPE_VIDEO -> {
                val newFileVideoName =
                    if (isOutFileNameEmpty) DateUtils.getCreateFileName("VID_") + POSTFIX_MP4 else fileName!!
                File(folderDir, newFileVideoName)
            }
            SelectMimeType.TYPE_AUDIO -> {
                val newFileAudioName =
                    if (isOutFileNameEmpty) DateUtils.getCreateFileName("AUD_") + POSTFIX_AMR else fileName!!
                File(folderDir, newFileAudioName)
            }
            else -> {
                val suffix =
                    if (TextUtils.isEmpty(format)) POSTFIX_JPG else format!!
                val newFileImageName =
                    if (isOutFileNameEmpty) DateUtils.getCreateFileName("IMG_") + suffix else fileName!!
                File(folderDir, newFileImageName)
            }
        }
    }

    /**
     * 文件根目录
     *
     * @param context
     * @param type
     * @return
     */
    private fun getRootDirFile(context: Context, type: Int): File {
        val fileDirPath = FileDirMap.getFileDirPath(context, type)
        return File(fileDirPath)
    }

    /**
     * TAG for log messages.
     */
    const val TAG = "PictureFileUtils"

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (ex: IllegalArgumentException) {
            Log.i(
                TAG,
                String.format(Locale.getDefault(), "getDataColumn: _data - [%s]", ex.message)
            )
        } finally {
            cursor?.close()
        }
        return ""
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br></br>
     * <br></br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @kotlin.jvm.JvmStatic
    @SuppressLint("NewApi")
    fun getPath(ctx: Context, uri: Uri): String? {
        val context = ctx.applicationContext
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return if (SdkVersionUtils.isQ()) {
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            .toString() + "/" + split[1]
                    } else {
                        Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), toLong(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) {
                uri.lastPathSegment
            } else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return ""
    }

    /**
     * Copies one file into the other with the given paths.
     * In the event that the paths are the same, trying to copy one file to the other
     * will cause both files to become null.
     * Simply skipping this step if the paths are identical.
     */
    fun copyFile(pathFrom: String, pathTo: String) {
        if (pathFrom.equals(pathTo, ignoreCase = true)) {
            return
        }
        var outputChannel: FileChannel? = null
        var inputChannel: FileChannel? = null
        try {
            inputChannel = FileInputStream(pathFrom).channel
            outputChannel = FileOutputStream(pathTo).channel
            inputChannel.transferTo(0, inputChannel.size(), outputChannel)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close(inputChannel)
            close(outputChannel)
        }
    }

    /**
     * 复制文件
     *
     * @param is 文件输入流
     * @param os 文件输出流
     * @return
     */
    fun writeFileFromIS(`is`: InputStream?, os: OutputStream?): Boolean {
        var osBuffer: OutputStream? = null
        var isBuffer: BufferedInputStream? = null
        return try {
            isBuffer = BufferedInputStream(`is`)
            osBuffer = BufferedOutputStream(os)
            val data = ByteArray(BYTE_SIZE)
            var len: Int
            while (isBuffer.read(data).also { len = it } != -1) {
                os!!.write(data, 0, len)
            }
            os!!.flush()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            close(isBuffer)
            close(osBuffer)
        }
    }

    /**
     * 创建视频缩略图地址
     *
     * @return
     */
    fun getVideoThumbnailDir(context: Context): String {
        val externalFilesDir = context.getExternalFilesDir("")
        val customFile = File(externalFilesDir!!.absolutePath, "VideoThumbnail")
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile.absolutePath + File.separator
    }

    /**
     * set empty PictureSelector Cache
     * Use [PictureCacheManager]
     *
     * @param mContext
     * @param type     image or video ...
     */
    @Deprecated("")
    fun deleteCacheDirFile(mContext: Context, type: Int) {
        val cutDir =
            mContext.getExternalFilesDir(if (type == ofImage()) Environment.DIRECTORY_PICTURES else Environment.DIRECTORY_MOVIES)
        if (cutDir != null) {
            val files = cutDir.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        file.delete()
                    }
                }
            }
        }
    }

    /**
     * set empty PictureSelector Cache
     * Use [PictureCacheManager]
     *
     * @param context
     * @param type    image、video、audio ...
     */
    @Deprecated("")
    fun deleteAllCacheDirFile(context: Context) {
        val dirPictures = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (dirPictures != null) {
            val files = dirPictures.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        file.delete()
                    }
                }
            }
        }
        val dirMovies = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        if (dirMovies != null) {
            val files = dirMovies.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        file.delete()
                    }
                }
            }
        }
        val dirMusic = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        if (dirMusic != null) {
            val files = dirMusic.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        file.delete()
                    }
                }
            }
        }
    }

    /**
     * 生成uri
     *
     * @param context
     * @param cameraFile
     * @return
     */
    fun parUri(context: Context, cameraFile: File?): Uri {
        val imageUri: Uri
        val authority = context.packageName + ".luckProvider"
        imageUri = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            //通过FileProvider创建一个content类型的Uri
            FileProvider.getUriForFile(context, authority, cameraFile!!)
        } else {
            Uri.fromFile(cameraFile)
        }
        return imageUri
    }

    /**
     * 根据类型创建文件名
     *
     * @param context
     * @param mineType
     * @param customFileName
     * @return
     */
    fun createFilePath(context: Context, mineType: String?, customFileName: String?): String {
        val filesDir: File
        val prefixTAG: String
        val suffix = getLastSourceSuffix(
            mineType!!
        )
        if (isHasVideo(mineType)) {
            // 视频
            prefixTAG = "VID_"
            filesDir = getRootDirFile(context, SelectMimeType.TYPE_VIDEO)
        } else if (isHasAudio(mineType)) {
            // 音频
            prefixTAG = "AUD_"
            filesDir = getRootDirFile(context, SelectMimeType.TYPE_AUDIO)
        } else {
            // 图片
            prefixTAG = "IMG_"
            filesDir = getRootDirFile(context, SelectMimeType.TYPE_IMAGE)
        }
        return filesDir.path + File.separator + if (TextUtils.isEmpty(customFileName)) DateUtils.getCreateFileName(
            prefixTAG
        ) + suffix else customFileName
    }

    /**
     * 判断文件是否存在
     *
     * @param path
     * @return
     */
    fun isImageFileExists(path: String?): Boolean {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inSampleSize = 2
        BitmapFactory.decodeFile(path, options)
        return options.outWidth > 0 && options.outHeight > 0
    }

    /**
     * 判断文件是否存在
     *
     * @param path
     * @return
     */
    fun isFileExists(path: String?): Boolean {
        return !TextUtils.isEmpty(path) && File(path).exists()
    }

    /**
     * Size of byte to fit size of memory.
     *
     * to three decimal places
     *
     * @param byteSize  Size of byte.
     * @param precision The precision
     * @return fit size of memory
     */
    @SuppressLint("DefaultLocale")
    fun formatFileSize(byteSize: Long): String {
        return if (byteSize < 0) {
            throw IllegalArgumentException("byteSize shouldn't be less than zero!")
        } else if (byteSize < FileSizeUnit.KB) {
            val format = String.format("%." + 2 + "f", byteSize.toDouble())
            val num: Double = toDouble(format)
            val round = Math.round(num)
            (if (round - num == 0.0) round else format).toString() + "B"
        } else if (byteSize < FileSizeUnit.MB) {
            val format = String.format(
                "%." + 2 + "f",
                byteSize.toDouble() / FileSizeUnit.KB
            )
            val num: Double = toDouble(format)
            val round = Math.round(num)
            (if (round - num == 0.0) round else format).toString() + "KB"
        } else if (byteSize < FileSizeUnit.GB) {
            val format = String.format(
                "%." + 2 + "f",
                byteSize.toDouble() / FileSizeUnit.MB
            )
            val num: Double = toDouble(format)
            val round = Math.round(num)
            (if (round - num == 0.0) round else format).toString() + "MB"
        } else {
            val format = String.format(
                "%." + 2 + "f",
                byteSize.toDouble() / FileSizeUnit.GB
            )
            val num: Double = toDouble(format)
            val round = Math.round(num)
            (if (round - num == 0.0) round else format).toString() + "GB"
        }
    }

    /**
     * Size of byte to fit size of memory.
     *
     * to three decimal places
     *
     * @param byteSize  Size of byte.
     * @param precision The precision
     * @return fit size of memory
     */
    @kotlin.jvm.JvmStatic
    @SuppressLint("DefaultLocale")
    fun formatAccurateUnitFileSize(byteSize: Long): String {
        var unit = ""
        val newByteSize: Double
        require(byteSize >= 0) { "byteSize shouldn't be less than zero!" }
        if (byteSize < FileSizeUnit.ACCURATE_KB) {
            newByteSize = byteSize.toDouble()
        } else if (byteSize < FileSizeUnit.ACCURATE_MB) {
            unit = "KB"
            newByteSize = byteSize.toDouble() / FileSizeUnit.ACCURATE_KB
        } else if (byteSize < FileSizeUnit.ACCURATE_GB) {
            unit = "MB"
            newByteSize = byteSize.toDouble() / FileSizeUnit.ACCURATE_MB
        } else {
            unit = "GB"
            newByteSize = byteSize.toDouble() / FileSizeUnit.ACCURATE_GB
        }
        val format = String.format(Locale("zh"), "%." + 2 + "f", newByteSize)
        return (if (Math.round(toDouble(format)) - toDouble(format) == 0.0) Math.round(
            toDouble(
                format
            )
        ) else format).toString() + unit
    }

    fun close(c: Closeable?) {
        // java.lang.IncompatibleClassChangeError: interface not implemented
        if (c is Closeable) {
            try {
                c.close()
            } catch (e: Exception) {
                // silence
            }
        }
    }
}