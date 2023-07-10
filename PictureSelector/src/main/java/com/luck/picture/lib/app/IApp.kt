package com.luck.picture.lib.app

import android.content.Context
import com.luck.picture.lib.engine.PictureSelectorEngine

/**
 * @author：luck
 * @date：2019-12-03 15:14
 * @describe：IApp
 */
interface IApp {
    /**
     * Application
     *
     * @return
     */
    val appContext: Context?

    /**
     * PictureSelectorEngine
     *
     * @return
     */
    val pictureSelectorEngine: PictureSelectorEngine?
}