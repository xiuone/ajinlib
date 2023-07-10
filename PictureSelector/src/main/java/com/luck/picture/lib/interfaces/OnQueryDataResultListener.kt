package com.luck.picture.lib.interfaces

import com.luck.picture.lib.config.SelectorConfig
import java.util.ArrayList

/**
 * @author：luck
 * @date：2020-04-16 12:42
 * @describe：OnQueryMediaResultListener
 */
open class OnQueryDataResultListener<T> {
    /**
     * Query to complete The callback listener
     *
     * @param result        The data source
     * @param isHasMore   Is there more
     */
    open fun onComplete(result: ArrayList<T>?, isHasMore: Boolean) {}
}