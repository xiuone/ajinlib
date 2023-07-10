package com.luck.picture.lib.interfaces

import com.luck.picture.lib.PictureSelectorPreviewFragment
import com.luck.picture.lib.config.SelectorConfig

/**
 * @author：luck
 * @date：2022/6/26 8:21 上午
 * @describe：OnInjectActivityPreviewListener
 */
interface OnInjectActivityPreviewListener {
    /**
     * onInjectPreviewFragment
     */
    fun onInjectPreviewFragment(): PictureSelectorPreviewFragment?
}