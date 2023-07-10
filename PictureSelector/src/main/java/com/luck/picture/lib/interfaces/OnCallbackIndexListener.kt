package com.luck.picture.lib.interfaces

import com.luck.picture.lib.config.SelectorConfig

/**
 * @author：luck
 * @date：2020/4/24 11:48 AM
 * @describe：OnCallbackIndexListener
 */
interface OnCallbackIndexListener<T> {
    /**
     * @param data
     * @param index
     */
    fun onCall(data: T, index: Int)
}