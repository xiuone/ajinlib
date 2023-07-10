package com.luck.picture.lib.interfaces

import com.luck.picture.lib.config.SelectorConfig

/**
 * @author：luck
 * @date：2021/12/5 9:56 下午
 * @describe：OnExternalQueryAlbumListener
 */
interface OnQueryAlbumListener<T> {
    fun onComplete(result: T)
}