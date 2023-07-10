package com.luck.picture.lib.interfaces

import com.luck.picture.lib.config.SelectorConfig

/**
 * @author：luck
 * @date：2020/4/24 11:48 AM
 * @describe：OnKeyValueResultCallbackListener
 */
interface OnKeyValueResultCallbackListener {
    /**
     * @param srcPath
     * @param resultPath
     */
    fun onCallback(srcPath: String?, resultPath: String?)
}