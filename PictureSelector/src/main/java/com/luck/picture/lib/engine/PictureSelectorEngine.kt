package com.luck.picture.lib.engine

import com.luck.picture.lib.basic.IBridgeLoaderFactory
import com.luck.picture.lib.config.PictureMimeType.isHasHttp
import com.luck.picture.lib.config.SelectorProviders.Companion.instance
import com.luck.picture.lib.config.SelectorProviders.selectorConfig
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnInjectLayoutResourceListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener

/**
 * @author：luck
 * @date：2020/4/22 11:36 AM
 * @describe：PictureSelectorEngine
 */
interface PictureSelectorEngine {
    /**
     * Create ImageLoad Engine
     *
     * @return
     */
    fun createImageLoaderEngine(): ImageEngine?

    /**
     * Create compress Engine
     *
     * @return
     */
    fun createCompressEngine(): CompressEngine?

    /**
     * Create compress Engine
     *
     * @return
     */
    fun createCompressFileEngine(): CompressFileEngine?

    /**
     * Create loader data Engine
     *
     * @return
     */
    fun createLoaderDataEngine(): ExtendLoaderEngine?

    /**
     * Create video player  Engine
     *
     * @return
     */
    fun createVideoPlayerEngine(): VideoPlayerEngine<*>?

    /**
     * Create loader data Engine
     *
     * @return
     */
    fun onCreateLoader(): IBridgeLoaderFactory?

    /**
     * Create SandboxFileEngine  Engine
     *
     * @return
     */
    fun createSandboxFileEngine(): SandboxFileEngine?

    /**
     * Create UriToFileTransformEngine  Engine
     *
     * @return
     */
    fun createUriToFileTransformEngine(): UriToFileTransformEngine?

    /**
     * Create LayoutResource  Listener
     *
     * @return
     */
    fun createLayoutResourceListener(): OnInjectLayoutResourceListener?

    /**
     * Create Result Listener
     *
     * @return
     */
    val resultCallbackListener: OnResultCallbackListener<LocalMedia?>?
}