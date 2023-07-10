package com.yalantis.ucrop

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView

/**
 * @author：luck
 * @date：2021/12/1 9:53 下午
 * @describe：UCropImageEngine
 */
open interface UCropImageEngine {
    /**
     * load image source
     *
     * @param context
     * @param url
     * @param imageView
     */
    fun loadImage(context: Context?, url: String?, imageView: ImageView?)

    /**
     * load image source
     *
     * @param context
     * @param url
     * @param maxWidth
     * @param maxHeight
     * @param call
     */
    fun loadImage(
        context: Context?,
        url: Uri?,
        maxWidth: Int,
        maxHeight: Int,
        call: OnCallbackListener<Bitmap?>?
    )

    open interface OnCallbackListener<T> {
        /**
         * @param data
         */
        fun onCall(data: T)
    }
}