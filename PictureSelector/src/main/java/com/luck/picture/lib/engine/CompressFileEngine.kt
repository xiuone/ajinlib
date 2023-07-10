package com.luck.picture.lib.engine

import android.content.Context
import android.net.Uri
import com.luck.picture.lib.config.PictureMimeType.isHasHttp
import com.luck.picture.lib.config.SelectorProviders.Companion.instance
import com.luck.picture.lib.config.SelectorProviders.selectorConfig
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import java.util.ArrayList

/**
 * @author：luck
 * @date：2021/5/19 9:36 AM
 * @describe：CompressFileEngine
 */
interface CompressFileEngine {
    /**
     * Custom compression engine
     *
     *
     * Users can implement this interface, and then access their own compression framework to plug
     * the compressed path into the [LocalMedia] object;
     *
     *
     * @param context
     * @param source
     */
    fun onStartCompress(
        context: Context?,
        source: ArrayList<Uri?>?,
        call: OnKeyValueResultCallbackListener?
    )
}