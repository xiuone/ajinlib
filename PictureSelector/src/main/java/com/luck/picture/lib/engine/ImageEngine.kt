package com.luck.picture.lib.engine

import android.content.Context
import android.widget.ImageView
import com.luck.picture.lib.config.PictureMimeType.isHasHttp
import com.luck.picture.lib.config.SelectorProviders.Companion.instance
import com.luck.picture.lib.config.SelectorProviders.selectorConfig
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders

/**
 * @author：luck
 * @date：2019-11-13 16:59
 * @describe：ImageEngine
 */
interface ImageEngine {
    /**
     * load image
     *
     * @param context
     * @param url
     * @param imageView
     */
    fun loadImage(context: Context?, url: String?, imageView: ImageView?)

    /**
     * load image
     *
     * @param context
     * @param imageView
     * @param url
     * @param maxWidth
     * @param maxHeight
     */
    fun loadImage(
        context: Context?,
        imageView: ImageView?,
        url: String?,
        maxWidth: Int,
        maxHeight: Int
    )

    /**
     * load album cover
     *
     * @param context
     * @param url
     * @param imageView
     */
    fun loadAlbumCover(context: Context?, url: String?, imageView: ImageView?)

    /**
     * load picture list picture
     *
     * @param context
     * @param url
     * @param imageView
     */
    fun loadGridImage(context: Context?, url: String?, imageView: ImageView?)

    /**
     * When the recyclerview slides quickly, the callback can be used to pause the loading of resources
     *
     * @param context
     */
    fun pauseRequests(context: Context?)

    /**
     * When the recyclerview is slow or stops sliding, the callback can do some operations to restore resource loading
     *
     * @param context
     */
    fun resumeRequests(context: Context?)
}