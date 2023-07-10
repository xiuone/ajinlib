package com.luck.picture.lib.loader

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.text.TextUtils
import com.luck.picture.lib.R
import com.luck.picture.lib.config.*
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
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaFolder
import com.luck.picture.lib.interfaces.OnQueryAlbumListener
import com.luck.picture.lib.interfaces.OnQueryAllAlbumListener
import com.luck.picture.lib.interfaces.OnQueryDataResultListener
import com.luck.picture.lib.thread.PictureThreadUtils
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.SdkVersionUtils
import com.luck.picture.lib.utils.SortUtils
import java.lang.Exception
import java.util.ArrayList

/**
 * @author：luck
 * @data：2016/12/31 19:12
 * @describe: Local media database query class
 */
class LocalMediaLoader(context: Context, config: SelectorConfig) :
    IBridgeMediaLoader(context, config) {
    override fun loadAllAlbum(query: OnQueryAllAlbumListener<LocalMediaFolder?>?) {
        PictureThreadUtils.executeByIo<List<LocalMediaFolder>>(object :
            PictureThreadUtils.SimpleTask<List<LocalMediaFolder?>?>() {
            override fun doInBackground(): List<LocalMediaFolder> {
                val imageFolders: MutableList<LocalMediaFolder> = ArrayList()
                val data = context.contentResolver.query(
                    IBridgeMediaLoader.Companion.QUERY_URI, IBridgeMediaLoader.Companion.PROJECTION,
                    selection, selectionArgs, sortOrder
                )
                try {
                    if (data != null) {
                        val allImageFolder = LocalMediaFolder()
                        val latelyImages = ArrayList<LocalMedia>()
                        val count = data.count
                        if (count > 0) {
                            data.moveToFirst()
                            do {
                                val media = parseLocalMedia(data, false) ?: continue
                                val folder = getImageFolder(
                                    media.path,
                                    media.mimeType, media.parentFolderName, imageFolders
                                )
                                folder.bucketId = media.bucketId
                                folder.data.add(media)
                                folder.folderTotalNum = folder.folderTotalNum + 1
                                folder.bucketId = media.bucketId
                                latelyImages.add(media)
                                val imageNum = allImageFolder.folderTotalNum
                                allImageFolder.folderTotalNum = imageNum + 1
                            } while (data.moveToNext())
                            val selfFolder = SandboxFileLoader.loadInAppSandboxFolderFile(
                                context, config.sandboxDir
                            )
                            if (selfFolder != null) {
                                imageFolders.add(selfFolder)
                                allImageFolder.folderTotalNum =
                                    allImageFolder.folderTotalNum + selfFolder.folderTotalNum
                                allImageFolder.data = selfFolder.data
                                latelyImages.addAll(0, selfFolder.data)
                                if (IBridgeMediaLoader.Companion.MAX_SORT_SIZE > selfFolder.folderTotalNum) {
                                    if (latelyImages.size > IBridgeMediaLoader.Companion.MAX_SORT_SIZE) {
                                        SortUtils.sortLocalMediaAddedTime(
                                            latelyImages.subList(
                                                0,
                                                IBridgeMediaLoader.Companion.MAX_SORT_SIZE
                                            )
                                        )
                                    } else {
                                        SortUtils.sortLocalMediaAddedTime(latelyImages)
                                    }
                                }
                            }
                            if (latelyImages.size > 0) {
                                SortUtils.sortFolder(imageFolders)
                                imageFolders.add(0, allImageFolder)
                                allImageFolder.firstImagePath = latelyImages[0].path
                                allImageFolder.firstMimeType = latelyImages[0].mimeType
                                val folderName: String?
                                folderName = if (TextUtils.isEmpty(config.defaultAlbumName)) {
                                    if (config.chooseMode == ofAudio()) context.getString(R.string.ps_all_audio) else context.getString(
                                        R.string.ps_camera_roll
                                    )
                                } else {
                                    config.defaultAlbumName
                                }
                                allImageFolder.folderName = folderName
                                allImageFolder.bucketId = PictureConfig.ALL.toLong()
                                allImageFolder.data = latelyImages
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (data != null && !data.isClosed) {
                        data.close()
                    }
                }
                return imageFolders
            }

            override fun onSuccess(result: List<LocalMediaFolder?>) {
                PictureThreadUtils.cancel(this)
                query?.onComplete(result)
            }
        })
    }

    override fun loadOnlyInAppDirAllMedia(listener: OnQueryAlbumListener<LocalMediaFolder?>?) {
        PictureThreadUtils.executeByIo(object : PictureThreadUtils.SimpleTask<LocalMediaFolder?>() {
            override fun doInBackground(): LocalMediaFolder {
                return SandboxFileLoader.loadInAppSandboxFolderFile(
                    context, config.sandboxDir
                )!!
            }

            override fun onSuccess(result: LocalMediaFolder) {
                PictureThreadUtils.cancel(this)
                listener?.onComplete(result)
            }
        })
    }

    override fun loadPageMediaData(
        bucketId: Long,
        page: Int,
        pageSize: Int,
        query: OnQueryDataResultListener<LocalMedia?>?
    ) {
    }

    override fun getAlbumFirstCover(bucketId: Long): String? {
        return null
    }// Access to the audio// Access to video// Gets the image

    // Get all, not including audio
    protected override val selection: String?
        protected get() {
            val durationCondition = durationCondition
            val fileSizeCondition = fileSizeCondition
            val queryMimeCondition = queryMimeCondition
            when (config.chooseMode) {
                SelectMimeType.TYPE_ALL ->                 // Get all, not including audio
                    return getSelectionArgsForAllMediaCondition(
                        durationCondition,
                        fileSizeCondition,
                        queryMimeCondition
                    )
                SelectMimeType.TYPE_IMAGE ->                 // Gets the image
                    return getSelectionArgsForImageMediaCondition(
                        fileSizeCondition,
                        queryMimeCondition
                    )
                SelectMimeType.TYPE_VIDEO ->                 // Access to video
                    return getSelectionArgsForVideoMediaCondition(
                        durationCondition,
                        queryMimeCondition
                    )
                SelectMimeType.TYPE_AUDIO ->                 // Access to the audio
                    return getSelectionArgsForAudioMediaCondition(
                        durationCondition,
                        queryMimeCondition
                    )
            }
            return null
        }// Get audio// Get video// Get photo

    // Get all
    protected override val selectionArgs: Array<String>?
        protected get() {
            when (config.chooseMode) {
                SelectMimeType.TYPE_ALL ->                 // Get all
                    return arrayOf(
                        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
                        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
                    )
                SelectMimeType.TYPE_IMAGE ->                 // Get photo
                    return arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
                SelectMimeType.TYPE_VIDEO ->                 // Get video
                    return arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
                SelectMimeType.TYPE_AUDIO ->                 // Get audio
                    return arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO.toString())
            }
            return null
        }
    protected override val sortOrder: String
        protected get() = if (TextUtils.isEmpty(config.sortOrder)) IBridgeMediaLoader.Companion.ORDER_BY else config.sortOrder

    override fun parseLocalMedia(data: Cursor, isUsePool: Boolean): LocalMedia? {
        val idColumn = data.getColumnIndexOrThrow(IBridgeMediaLoader.Companion.PROJECTION.get(0))
        val dataColumn = data.getColumnIndexOrThrow(IBridgeMediaLoader.Companion.PROJECTION.get(1))
        val mimeTypeColumn =
            data.getColumnIndexOrThrow(IBridgeMediaLoader.Companion.PROJECTION.get(2))
        val widthColumn = data.getColumnIndexOrThrow(IBridgeMediaLoader.Companion.PROJECTION.get(3))
        val heightColumn =
            data.getColumnIndexOrThrow(IBridgeMediaLoader.Companion.PROJECTION.get(4))
        val durationColumn =
            data.getColumnIndexOrThrow(IBridgeMediaLoader.Companion.PROJECTION.get(5))
        val sizeColumn = data.getColumnIndexOrThrow(IBridgeMediaLoader.Companion.PROJECTION.get(6))
        val folderNameColumn =
            data.getColumnIndexOrThrow(IBridgeMediaLoader.Companion.PROJECTION.get(7))
        val fileNameColumn =
            data.getColumnIndexOrThrow(IBridgeMediaLoader.Companion.PROJECTION.get(8))
        val bucketIdColumn =
            data.getColumnIndexOrThrow(IBridgeMediaLoader.Companion.PROJECTION.get(9))
        val dateAddedColumn =
            data.getColumnIndexOrThrow(IBridgeMediaLoader.Companion.PROJECTION.get(10))
        val orientationColumn =
            data.getColumnIndexOrThrow(IBridgeMediaLoader.Companion.PROJECTION.get(11))
        val id = data.getLong(idColumn)
        val dateAdded = data.getLong(dateAddedColumn)
        var mimeType = data.getString(mimeTypeColumn)
        val absolutePath = data.getString(dataColumn)
        val url =
            if (SdkVersionUtils.isQ()) MediaUtils.getRealPathUri(id, mimeType) else absolutePath
        mimeType = if (TextUtils.isEmpty(mimeType)) ofJPEG() else mimeType
        // Here, it is solved that some models obtain mimeType and return the format of image / *,
        // which makes it impossible to distinguish the specific type, such as mi 8,9,10 and other models
        if (mimeType.endsWith("image/*")) {
            mimeType = MediaUtils.getMimeTypeFromMediaUrl(absolutePath)
            if (!config.isGif) {
                if (isHasGif(mimeType)) {
                    return null
                }
            }
        }
        if (mimeType.endsWith("image/*")) {
            return null
        }
        if (!config.isWebp) {
            if (mimeType.startsWith(ofWEBP())) {
                return null
            }
        }
        if (!config.isBmp) {
            if (isHasBmp(mimeType)) {
                return null
            }
        }
        var width = data.getInt(widthColumn)
        var height = data.getInt(heightColumn)
        val orientation = data.getInt(orientationColumn)
        if (orientation == 90 || orientation == 270) {
            width = data.getInt(heightColumn)
            height = data.getInt(widthColumn)
        }
        val duration = data.getLong(durationColumn)
        val size = data.getLong(sizeColumn)
        val folderName = data.getString(folderNameColumn)
        var fileName = data.getString(fileNameColumn)
        val bucketId = data.getLong(bucketIdColumn)
        if (TextUtils.isEmpty(fileName)) {
            fileName = getUrlToFileName(absolutePath)
        }
        if (config.isFilterSizeDuration && size > 0 && size < FileSizeUnit.KB) {
            // Filter out files less than 1KB
            return null
        }
        if (isHasVideo(mimeType) || isHasAudio(mimeType)) {
            if (config.filterVideoMinSecond > 0 && duration < config.filterVideoMinSecond) {
                // If you set the minimum number of seconds of video to display
                return null
            }
            if (config.filterVideoMaxSecond > 0 && duration > config.filterVideoMaxSecond) {
                // If you set the maximum number of seconds of video to display
                return null
            }
            if (config.isFilterSizeDuration && duration <= 0) {
                //If the length is 0, the corrupted video is processed and filtered out
                return null
            }
        }
        val media = LocalMedia.create()
        media.id = id
        media.bucketId = bucketId
        media.path = url
        media.realPath = absolutePath
        media.fileName = fileName
        media.parentFolderName = folderName
        media.duration = duration
        media.chooseModel = config.chooseMode
        media.mimeType = mimeType
        media.width = width
        media.height = height
        media.size = size
        media.dateAddedTime = dateAdded
        if (mConfig.onQueryFilterListener != null) {
            if (mConfig.onQueryFilterListener.onFilter(media)) {
                return null
            }
        }
        return media
    }

    /**
     * Create folder
     *
     * @param firstPath
     * @param firstMimeType
     * @param imageFolders
     * @param folderName
     * @return
     */
    private fun getImageFolder(
        firstPath: String,
        firstMimeType: String,
        folderName: String,
        imageFolders: MutableList<LocalMediaFolder>
    ): LocalMediaFolder {
        for (folder in imageFolders) {
            // Under the same folder, return yourself, otherwise create a new folder
            val name = folder.folderName
            if (TextUtils.isEmpty(name)) {
                continue
            }
            if (TextUtils.equals(name, folderName)) {
                return folder
            }
        }
        val newFolder = LocalMediaFolder()
        newFolder.folderName = folderName
        newFolder.firstImagePath = firstPath
        newFolder.firstMimeType = firstMimeType
        imageFolders.add(newFolder)
        return newFolder
    }

    companion object {
        /**
         * Video mode conditions
         *
         * @param durationCondition
         * @param queryMimeCondition
         * @return
         */
        private fun getSelectionArgsForVideoMediaCondition(
            durationCondition: String?,
            queryMimeCondition: String?
        ): String {
            return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + queryMimeCondition + " AND " + durationCondition
        }

        /**
         * Audio mode conditions
         *
         * @param durationCondition
         * @param queryMimeCondition
         * @return
         */
        private fun getSelectionArgsForAudioMediaCondition(
            durationCondition: String?,
            queryMimeCondition: String?
        ): String {
            return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + queryMimeCondition + " AND " + durationCondition
        }

        /**
         * Query conditions in all modes
         *
         * @param timeCondition
         * @param sizeCondition
         * @param queryMimeCondition
         * @return
         */
        private fun getSelectionArgsForAllMediaCondition(
            timeCondition: String?,
            sizeCondition: String?,
            queryMimeCondition: String?
        ): String {
            return "(" +
                    MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" +
                    queryMimeCondition + " OR " +
                    MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " +
                    timeCondition + ") AND " +
                    sizeCondition
        }

        /**
         * Query conditions in image modes
         *
         * @param fileSizeCondition
         * @param queryMimeCondition
         * @return
         */
        private fun getSelectionArgsForImageMediaCondition(
            fileSizeCondition: String?,
            queryMimeCondition: String?
        ): String {
            return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + queryMimeCondition + " AND " + fileSizeCondition
        }
    }
}