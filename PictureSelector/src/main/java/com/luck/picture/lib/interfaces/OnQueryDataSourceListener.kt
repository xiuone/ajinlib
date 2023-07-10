package com.luck.picture.lib.interfaces

import com.luck.picture.lib.config.SelectorConfig

/**
 * @author：luck
 * @date：2020-04-16 12:42
 * @describe：OnQueryDataSourceListener
 */
interface OnQueryDataSourceListener<T> {
    /**
     * Query data source
     *
     * @param result The data source
     */
    fun onComplete(result: List<T>?)
}