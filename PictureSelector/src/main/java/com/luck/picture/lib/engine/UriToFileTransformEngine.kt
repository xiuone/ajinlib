package com.luck.picture.lib.engine

import android.content.Context
import com.luck.picture.lib.config.PictureMimeType.isHasHttp
import com.luck.picture.lib.config.SelectorProviders.Companion.instance
import com.luck.picture.lib.config.SelectorProviders.selectorConfig
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener

/**
 * @author：luck
 * @date：2021/11/23 8:23 下午
 * @describe：UriToFileTransformEngine
 */
interface UriToFileTransformEngine {
    /**
     * Custom Sandbox File engine
     *
     *
     * Users can implement this interface, and then access their own sandbox framework to plug
     * the sandbox path into the [LocalMedia] object;
     *
     *
     *
     * This is an asynchronous thread callback
     *
     *
     * @param context  context
     * @param srcPath
     * @param mineType
     */
    fun onUriToFileAsyncTransform(
        context: Context?,
        srcPath: String?,
        mineType: String?,
        call: OnKeyValueResultCallbackListener?
    )
}