package com.luck.picture.lib.loader

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.text.TextUtils
import com.luck.picture.lib.config.PictureMimeType
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
import com.luck.picture.lib.interfaces.OnQueryAlbumListener
import com.luck.picture.lib.interfaces.OnQueryAllAlbumListener
import com.luck.picture.lib.interfaces.OnQueryDataResultListener
import java.lang.StringBuilder
import java.util.*

/**
 * @author：luck
 * @date：2021/11/11 12:53 下午
 * @describe：IBridgeMediaLoader
 */
abstract class IBridgeMediaLoader(
    protected val context: Context,
    protected val config: SelectorConfig
) {

    /**
     * query album cover
     *
     * @param bucketId
     */
    abstract fun getAlbumFirstCover(bucketId: Long): String?

    /**
     * query album list
     */
    abstract fun loadAllAlbum(query: OnQueryAllAlbumListener<LocalMediaFolder?>?)

    /**
     * page query specified contents
     *
     * @param bucketId
     * @param page
     * @param pageSize
     */
    abstract fun loadPageMediaData(
        bucketId: Long,
        page: Int,
        pageSize: Int,
        query: OnQueryDataResultListener<LocalMedia?>?
    )

    /**
     * query specified contents
     */
    abstract fun loadOnlyInAppDirAllMedia(query: OnQueryAlbumListener<LocalMediaFolder?>?)

    /**
     * A filter declaring which rows to return,
     * formatted as an SQL WHERE clause (excluding the WHERE itself).
     * Passing null will return all rows for the given URI.
     */
    protected abstract val selection: String?

    /**
     * You may include ?s in selection, which will be replaced by the values from selectionArgs,
     * in the order that they appear in the selection. The values will be bound as Strings.
     */
    protected abstract val selectionArgs: Array<String>?

    /**
     * How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself).
     * Passing null will use the default sort order, which may be unordered.
     */
    protected abstract val sortOrder: String

    /**
     * parse LocalMedia
     *
     * @param data      Cursor
     * @param isUsePool object pool
     */
    protected abstract fun parseLocalMedia(data: Cursor, isUsePool: Boolean): LocalMedia?

    /**
     * Get video (maximum or minimum time)
     *
     * @return
     */
    protected val durationCondition: String
        protected get() {
            val maxS =
                if (config.filterVideoMaxSecond == 0) Long.MAX_VALUE else config.filterVideoMaxSecond.toLong()
            return String.format(
                Locale.CHINA, "%d <%s " + COLUMN_DURATION + " and " + COLUMN_DURATION + " <= %d",
                Math.max(0L, config.filterVideoMinSecond), "=", maxS
            )
        }

    /**
     * Get media size (maxFileSize or miniFileSize)
     *
     * @return
     */
    protected val fileSizeCondition: String
        protected get() {
            val maxS =
                if (config.filterMaxFileSize == 0L) Long.MAX_VALUE else config.filterMaxFileSize
            return String.format(
                Locale.CHINA,
                "%d <%s " + MediaStore.MediaColumns.SIZE + " and " + MediaStore.MediaColumns.SIZE + " <= %d",
                Math.max(0, config.filterMinFileSize),
                "=",
                maxS
            )
        }

    /**
     * getQueryMimeCondition
     *
     * @return
     */
    protected val queryMimeCondition: String
        protected get() {
            val filters = config.queryOnlyList
            val filterSet = HashSet(filters)
            val iterator: Iterator<String> = filterSet.iterator()
            val stringBuilder = StringBuilder()
            var index = -1
            while (iterator.hasNext()) {
                val value = iterator.next()
                if (TextUtils.isEmpty(value)) {
                    continue
                }
                if (config.chooseMode == ofVideo()) {
                    if (value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_IMAGE) || value.startsWith(
                            PictureMimeType.MIME_TYPE_PREFIX_AUDIO
                        )
                    ) {
                        continue
                    }
                } else if (config.chooseMode == ofImage()) {
                    if (value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_AUDIO) || value.startsWith(
                            PictureMimeType.MIME_TYPE_PREFIX_VIDEO
                        )
                    ) {
                        continue
                    }
                } else if (config.chooseMode == ofAudio()) {
                    if (value.startsWith(PictureMimeType.MIME_TYPE_PREFIX_VIDEO) || value.startsWith(
                            PictureMimeType.MIME_TYPE_PREFIX_IMAGE
                        )
                    ) {
                        continue
                    }
                }
                index++
                stringBuilder.append(if (index == 0) " AND " else " OR ")
                    .append(MediaStore.MediaColumns.MIME_TYPE).append("='").append(value)
                    .append("'")
            }
            if (config.chooseMode != ofVideo()) {
                if (!config.isGif && !filterSet.contains(ofGIF())) {
                    stringBuilder.append(NOT_GIF)
                }
            }
            return stringBuilder.toString()
        }

    companion object {
        protected val TAG = IBridgeMediaLoader::class.java.simpleName
        protected val QUERY_URI = MediaStore.Files.getContentUri("external")
        protected const val ORDER_BY = MediaStore.MediaColumns.DATE_MODIFIED + " DESC"
        protected const val NOT_GIF =
            " AND (" + MediaStore.MediaColumns.MIME_TYPE + "!='image/gif')"
        protected const val GROUP_BY_BUCKET_Id = " GROUP BY (bucket_id"
        protected const val DISTINCT_BUCKET_Id = "DISTINCT bucket_id"
        protected const val COLUMN_COUNT = "count"
        protected const val COLUMN_BUCKET_ID = "bucket_id"
        protected const val COLUMN_DURATION = "duration"
        protected const val COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name"
        protected const val COLUMN_ORIENTATION = "orientation"
        protected const val MAX_SORT_SIZE = 60

        /**
         * A list of which columns to return. Passing null will return all columns, which is inefficient.
         */
        protected val PROJECTION = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            COLUMN_DURATION,
            MediaStore.MediaColumns.SIZE,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DISPLAY_NAME,
            COLUMN_BUCKET_ID,
            MediaStore.MediaColumns.DATE_ADDED,
            COLUMN_ORIENTATION
        )

        /**
         * A list of which columns to return. Passing null will return all columns, which is inefficient.
         */
        protected val ALL_PROJECTION = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            COLUMN_DURATION,
            MediaStore.MediaColumns.SIZE,
            COLUMN_BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DISPLAY_NAME,
            COLUMN_BUCKET_ID,
            MediaStore.MediaColumns.DATE_ADDED,
            COLUMN_ORIENTATION,
            "COUNT(*) AS " + COLUMN_COUNT
        )
    }
}