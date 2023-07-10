package com.luck.picture.lib.loader

import android.content.Context
import android.text.TextUtils
import com.luck.picture.lib.config.SelectMimeType.ofVideo
import com.luck.picture.lib.config.SelectMimeType.ofImage
import com.luck.picture.lib.config.SelectMimeType.ofAudio
import com.luck.picture.lib.config.PictureMimeType.ofGIF
import com.luck.picture.lib.interfaces.OnQueryAllAlbumListener.onComplete
import com.luck.picture.lib.interfaces.OnQueryAlbumListener.onComplete
import com.luck.picture.lib.config.PictureMimeType.ofJPEG
import com.luck.picture.lib.config.PictureMimeType.isHasGif
import com.luck.picture.lib.config.PictureMimeType.ofWEBP
import com.luck.picture.lib.config.PictureMimeType.isHasBmp
import com.luck.picture.lib.config.PictureMimeType.getUrlToFileName
import com.luck.picture.lib.config.PictureMimeType.isHasVideo
import com.luck.picture.lib.config.PictureMimeType.isHasAudio
import com.luck.picture.lib.interfaces.OnQueryFilterListener.onFilter
import com.luck.picture.lib.interfaces.OnQueryDataResultListener.onComplete
import com.luck.picture.lib.config.PictureMimeType.isContent
import com.luck.picture.lib.config.SelectMimeType.ofAll
import com.luck.picture.lib.config.PictureMimeType.isHasImage
import com.luck.picture.lib.config.SelectorProviders.Companion.instance
import com.luck.picture.lib.config.SelectorProviders.selectorConfig
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaFolder
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.SdkVersionUtils
import com.luck.picture.lib.utils.SortUtils
import com.luck.picture.lib.utils.ValueOf
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.ArrayList

/**
 * @author：luck
 * @date：2021/11/10 5:40 下午
 * @describe：SandboxFileLoader
 */
object SandboxFileLoader {
    /**
     * 查询应用内部目录的图片
     *
     * @param context    上下文
     * @param sandboxDir 资源目标路径
     */
    fun loadInAppSandboxFolderFile(context: Context?, sandboxDir: String?): LocalMediaFolder? {
        val list = loadInAppSandboxFile(context, sandboxDir)
        var folder: LocalMediaFolder? = null
        if (list != null && list.size > 0) {
            SortUtils.sortLocalMediaAddedTime(list)
            val firstMedia = list[0]
            folder = LocalMediaFolder()
            folder.folderName = firstMedia.parentFolderName
            folder.firstImagePath = firstMedia.path
            folder.firstMimeType = firstMedia.mimeType
            folder.bucketId = firstMedia.bucketId
            folder.folderTotalNum = list.size
            folder.data = list
        }
        return folder
    }

    /**
     * 查询应用内部目录的图片
     *
     * @param context    上下文
     * @param sandboxDir 资源目标路径
     */
    fun loadInAppSandboxFile(context: Context?, sandboxDir: String?): ArrayList<LocalMedia>? {
        if (TextUtils.isEmpty(sandboxDir)) {
            return null
        }
        val list = ArrayList<LocalMedia>()
        val sandboxFile = File(sandboxDir)
        if (sandboxFile.exists()) {
            val files = sandboxFile.listFiles { file -> !file.isDirectory } ?: return list
            val config = instance!!.selectorConfig
            var md: MessageDigest? = null
            try {
                md = MessageDigest.getInstance("MD5")
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            for (f in files) {
                val mimeType = MediaUtils.getMimeTypeFromMediaUrl(f.absolutePath)
                if (config.chooseMode == ofImage()) {
                    if (!isHasImage(mimeType)) {
                        continue
                    }
                } else if (config.chooseMode == ofVideo()) {
                    if (!isHasVideo(mimeType)) {
                        continue
                    }
                } else if (config.chooseMode == ofAudio()) {
                    if (!isHasAudio(mimeType)) {
                        continue
                    }
                }
                if (config.queryOnlyList != null && config.queryOnlyList!!.size > 0 && !config.queryOnlyList!!.contains(
                        mimeType
                    )
                ) {
                    continue
                }
                if (!config.isGif) {
                    if (isHasGif(mimeType)) {
                        continue
                    }
                }
                val absolutePath = f.absolutePath
                val size = f.length()
                if (size <= 0) {
                    continue
                }
                var id: Long
                id = if (md != null) {
                    md.update(absolutePath.toByteArray())
                    BigInteger(1, md.digest()).toLong()
                } else {
                    f.lastModified() / 1000
                }
                val bucketId = ValueOf.toLong(sandboxFile.name.hashCode())
                val dateAdded = f.lastModified() / 1000
                var duration: Long
                var width: Int
                var height: Int
                if (isHasVideo(mimeType)) {
                    val videoSize = MediaUtils.getVideoSize(context, absolutePath)
                    width = videoSize.width
                    height = videoSize.height
                    duration = videoSize.duration
                } else if (isHasAudio(mimeType)) {
                    val audioSize = MediaUtils.getAudioSize(context, absolutePath)
                    width = audioSize.width
                    height = audioSize.height
                    duration = audioSize.duration
                } else {
                    val imageSize = MediaUtils.getImageSize(context, absolutePath)
                    width = imageSize.width
                    height = imageSize.height
                    duration = 0L
                }
                if (isHasVideo(mimeType) || isHasAudio(mimeType)) {
                    if (config.filterVideoMinSecond > 0 && duration < config.filterVideoMinSecond) {
                        // If you set the minimum number of seconds of video to display
                        continue
                    }
                    if (config.filterVideoMaxSecond > 0 && duration > config.filterVideoMaxSecond) {
                        // If you set the maximum number of seconds of video to display
                        continue
                    }
                    if (duration == 0L) {
                        //If the length is 0, the corrupted video is processed and filtered out
                        continue
                    }
                }
                val media = LocalMedia.create()
                media.id = id
                media.path = absolutePath
                media.realPath = absolutePath
                media.fileName = f.name
                media.parentFolderName = sandboxFile.name
                media.duration = duration
                media.chooseModel = config.chooseMode
                media.mimeType = mimeType
                media.width = width
                media.height = height
                media.size = size
                media.bucketId = bucketId
                media.dateAddedTime = dateAdded
                if (config.onQueryFilterListener != null) {
                    if (config.onQueryFilterListener!!.onFilter(media)) {
                        continue
                    }
                }
                media.sandboxPath = if (SdkVersionUtils.isQ()) absolutePath else null
                list.add(media)
            }
        }
        return list
    }
}