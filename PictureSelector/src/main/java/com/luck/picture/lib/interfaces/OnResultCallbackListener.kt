package com.luck.picture.lib.interfaces

import com.luck.picture.lib.config.SelectorConfig
import java.util.ArrayList

/**
 * @author：luck
 * @date：2020-01-14 17:08
 * @describe：onResult Callback Listener
 */
interface OnResultCallbackListener<T> {
    /**
     * return LocalMedia result
     *
     * @param result
     */
    fun onResult(result: ArrayList<T>?)

    /**
     * Cancel
     */
    fun onCancel()
}