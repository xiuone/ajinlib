package com.luck.picture.lib.interfaces

import android.content.Context
import com.luck.picture.lib.config.SelectorConfig

/**
 * @author：luck
 * @date：2022/4/3 5:37 下午
 * @describe：OnVideoThumbnailEventListener
 */
interface OnVideoThumbnailEventListener {
    /**
     * video thumbnail
     *
     * @param context
     * @param videoPath
     */
    fun onVideoThumbnail(
        context: Context?,
        videoPath: String?,
        call: OnKeyValueResultCallbackListener?
    )
}