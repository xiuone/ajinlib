package com.luck.picture.lib.basic

import com.luck.picture.lib.loader.IBridgeMediaLoader

/**
 * @author：luck
 * @date：2022/6/10 9:37 上午
 * @describe：IBridgeLoaderFactory
 */
interface IBridgeLoaderFactory {
    /**
     * CreateLoader
     */
    fun onCreateLoader(): IBridgeMediaLoader?
}