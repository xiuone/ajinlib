package com.luck.picture.lib.app

import android.content.Context
import com.luck.picture.lib.engine.PictureSelectorEngine

/**
 * @author：luck
 * @date：2019-12-03 15:12
 * @describe：PictureAppMaster
 */
class PictureAppMaster private constructor() : IApp {
    override val appContext: Context?
        get() = if (app == null) {
            null
        } else app.getAppContext()
    override val pictureSelectorEngine: PictureSelectorEngine?
        get() = if (app == null) {
            null
        } else app.getPictureSelectorEngine()
    var app: IApp? = null

    companion object {
        private var mInstance: PictureAppMaster? = null
        @JvmStatic
        val instance: PictureAppMaster?
            get() {
                if (mInstance == null) {
                    synchronized(PictureAppMaster::class.java) {
                        if (mInstance == null) {
                            mInstance = PictureAppMaster()
                        }
                    }
                }
                return mInstance
            }
    }
}