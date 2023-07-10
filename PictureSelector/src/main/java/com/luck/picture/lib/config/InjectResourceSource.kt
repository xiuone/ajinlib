package com.luck.picture.lib.config

import android.content.Context
import com.luck.picture.lib.config.SelectorConfig
import kotlin.jvm.Synchronized
import com.luck.picture.lib.utils.FileDirMap
import kotlin.jvm.Volatile
import com.luck.picture.lib.config.SelectorProviders

/**
 * @author：luck
 * @date：2021/12/23 10:50 上午
 * @describe：InjectResourceSource
 */
object InjectResourceSource {
    /**
     * default layout
     */
    const val DEFAULT_LAYOUT_RESOURCE = 0

    /**
     * [PictureSelectorFragment]  layout
     * [R.layout.ps_fragment_selector]
     */
    const val MAIN_SELECTOR_LAYOUT_RESOURCE = 1

    /**
     * [PictureSelectorPreviewFragment] preview layout
     * [R.layout.ps_fragment_preview]
     */
    const val PREVIEW_LAYOUT_RESOURCE = 2

    /**
     * [PictureImageGridAdapter]  image adapter item layout
     * [R.layout.ps_item_grid_image]
     */
    const val MAIN_ITEM_IMAGE_LAYOUT_RESOURCE = 3

    /**
     * [PictureImageGridAdapter]  video adapter item layout
     * [R.layout.ps_item_grid_video]
     */
    const val MAIN_ITEM_VIDEO_LAYOUT_RESOURCE = 4

    /**
     * [PictureImageGridAdapter]  audio adapter item layout
     * [R.layout.ps_item_grid_audio]
     */
    const val MAIN_ITEM_AUDIO_LAYOUT_RESOURCE = 5

    /**
     * [PictureAlbumAdapter] adapter item layout
     * [R.layout.ps_album_folder_item]
     */
    const val ALBUM_ITEM_LAYOUT_RESOURCE = 6

    /**
     * [PicturePreviewAdapter] preview adapter item layout
     * [R.layout.ps_preview_image]
     */
    const val PREVIEW_ITEM_IMAGE_LAYOUT_RESOURCE = 7

    /**
     * [PicturePreviewAdapter] preview adapter item layout
     * [R.layout.ps_preview_video]
     */
    const val PREVIEW_ITEM_VIDEO_LAYOUT_RESOURCE = 8

    /**
     * [PreviewGalleryAdapter] preview gallery adapter item layout
     * [R.layout.ps_preview_gallery_item]
     */
    const val PREVIEW_GALLERY_ITEM_LAYOUT_RESOURCE = 9

    /**
     * [PicturePreviewAdapter] preview adapter item layout
     * [R.layout.ps_preview_audio]
     */
    const val PREVIEW_ITEM_AUDIO_LAYOUT_RESOURCE = 10

    /**
     * getLayoutResource
     *
     * @param context
     * @param resourceSource [InjectResourceSource]
     * @return
     */
    fun getLayoutResource(
        context: Context?,
        resourceSource: Int,
        selectorConfig: SelectorConfig?
    ): Int {
        return if (selectorConfig != null && selectorConfig.onLayoutResourceListener != null) {
            selectorConfig.onLayoutResourceListener!!.getLayoutResourceId(
                context,
                resourceSource
            )
        } else DEFAULT_LAYOUT_RESOURCE
    }
}