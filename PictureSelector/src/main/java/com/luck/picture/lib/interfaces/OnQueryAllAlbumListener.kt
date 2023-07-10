package com.luck.picture.lib.interfaces

import com.luck.picture.lib.config.SelectorConfig

/**
 * @author：luck
 * @date：2021/12/5 9:41 下午
 * @describe：OnExternalQueryAllAlbumListener
 */
interface OnQueryAllAlbumListener<T> {
    fun onComplete(result: List<T>?)
}