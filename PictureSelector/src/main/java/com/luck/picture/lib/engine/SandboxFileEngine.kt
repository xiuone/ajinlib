package com.luck.picture.lib.engine

import android.content.Context
import com.luck.picture.lib.config.PictureMimeType.isHasHttp
import com.luck.picture.lib.config.SelectorProviders.Companion.instance
import com.luck.picture.lib.config.SelectorProviders.selectorConfig
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnCallbackIndexListener

/**
 * @author：luck
 * @date：2021/11/23 8:23 下午
 * @describe：SandboxFileEngine Use [UriToFileTransformEngine]
 */
@Deprecated("")
interface SandboxFileEngine {
    /**
     * Custom Sandbox File engine
     *
     *
     * Users can implement this interface, and then access their own sandbox framework to plug
     * the sandbox path into the [LocalMedia] object;
     *
     *
     *
     *
     *
     * 1、LocalMedia media = new LocalMedia();
     * media.setSandboxPath("Your sandbox path");
     *
     *
     *
     * 2、listener.onCall( "you result" );
     *
     *
     * @param context              context
     * @param isOriginalImage The original drawing needs to be processed
     * @param index                The location of the resource in the result queue
     * @param media                LocalMedia
     * @param listener
     */
    fun onStartSandboxFileTransform(
        context: Context?, isOriginalImage: Boolean,
        index: Int, media: LocalMedia?,
        listener: OnCallbackIndexListener<LocalMedia?>?
    )
}