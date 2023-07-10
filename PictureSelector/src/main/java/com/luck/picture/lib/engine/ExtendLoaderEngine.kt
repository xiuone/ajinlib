package com.luck.picture.lib.engine

import android.content.Context
import com.luck.picture.lib.config.PictureMimeType.isHasHttp
import com.luck.picture.lib.config.SelectorProviders.Companion.instance
import com.luck.picture.lib.config.SelectorProviders.selectorConfig
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaFolder
import com.luck.picture.lib.interfaces.OnQueryAlbumListener
import com.luck.picture.lib.interfaces.OnQueryAllAlbumListener
import com.luck.picture.lib.interfaces.OnQueryDataResultListener

/**
 * @author：luck
 * @date：2021/12/5 7:31 下午
 * @describe：Custom data loader engine
 */
@Deprecated("")
interface ExtendLoaderEngine {
    /**
     * load all album list data
     *
     *
     * Users can implement some interfaces to access their own query data,
     * provided that they comply with the [LocalMediaFolder] standard
     *
     *
     *
     *
     * query.onComplete(List<LocalMediaFolder> result);
    </LocalMediaFolder> *
     *
     * @param context
     * @param query
     */
    fun loadAllAlbumData(context: Context?, query: OnQueryAllAlbumListener<LocalMediaFolder?>?)

    /**
     * load resources in the specified directory
     *
     *
     * Users can implement some interfaces to access their own query data,
     * provided that they comply with the [LocalMediaFolder] standard
     *
     *
     *
     *
     * query.onComplete(LocalMediaFolder result);
     *
     *
     * @param context
     * @param query
     */
    fun loadOnlyInAppDirAllMediaData(
        context: Context?,
        query: OnQueryAlbumListener<LocalMediaFolder?>?
    )

    /**
     * load the first item of data in the album list
     * [SelectorConfig] Valid only in isPageStrategy mode
     *
     *
     * Users can implement some interfaces to access their own query data,
     * provided that they comply with the [LocalMedia] standard
     *
     *
     *
     * query.onComplete(List<LocalMedia> result, int currentPage, boolean isHasMore);
    </LocalMedia> *
     *
     *
     *
     * isHasMore; Whether there is more data needs to be controlled by developers
     *
     *
     * @param context
     * @param bucketId Album ID
     * @param page     first page
     * @param pageSize How many entries per page
     * @param query
     */
    fun loadFirstPageMediaData(
        context: Context?, bucketId: Long, page: Int, pageSize: Int,
        query: OnQueryDataResultListener<LocalMedia?>?
    )

    /**
     * load the first item of data in the album list
     * [SelectorConfig] Valid only in isPageStrategy mode
     *
     *
     *
     *
     * Users can implement some interfaces to access their own query data,
     * provided that they comply with the [LocalMedia] standard
     *
     * query.onComplete(List<LocalMedia> result, int currentPage, boolean isHasMore);
     *
     *
     *
     *
     * currentPage; Represents the current page number
     * isHasMore; Whether there is more data needs to be controlled by developers
     *
     *
     * @param context
     * @param bucketId Album ID
     * @param page     Current page number
     * @param limit    query limit
     * @param pageSize How many entries per page
     * @param query
    </LocalMedia> */
    fun loadMoreMediaData(
        context: Context?, bucketId: Long, page: Int, limit: Int, pageSize: Int,
        query: OnQueryDataResultListener<LocalMedia?>?
    )
}