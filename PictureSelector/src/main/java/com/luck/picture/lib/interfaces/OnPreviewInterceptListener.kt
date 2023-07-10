package com.luck.picture.lib.interfaces

import android.content.Context
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia
import java.util.ArrayList

/**
 * @author：luck
 * @date：2021/12/24 9:43 上午
 * @describe：OnPreviewInterceptListener
 */
interface OnPreviewInterceptListener {
    /**
     * Custom preview event
     *
     * @param context
     * @param position         preview current position
     * @param totalNum         source total num
     * @param page             page
     * @param currentBucketId  current source bucket id
     * @param currentAlbumName current album name
     * @param isShowCamera     current album show camera
     * @param data             preview source
     * @param isBottomPreview  from bottomNavBar preview mode
     */
    fun onPreview(
        context: Context?, position: Int, totalNum: Int, page: Int,
        currentBucketId: Long, currentAlbumName: String?, isShowCamera: Boolean,
        data: ArrayList<LocalMedia?>?, isBottomPreview: Boolean
    )
}