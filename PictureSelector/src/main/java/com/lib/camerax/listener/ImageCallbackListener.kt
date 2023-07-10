package com.lib.camerax.listener

import android.widget.ImageView
import com.lib.camerax.listener.CameraXOrientationEventListener.OnOrientationChangedListener
import com.lib.camerax.listener.CameraXPreviewViewTouchListener.CustomTouchListener

/**
 * @author：luck
 * @date：2020-01-04 15:55
 * @describe：图片加载
 */
interface ImageCallbackListener {
    /**
     * 加载图片回调
     *
     * @param url       资源url
     * @param imageView 图片渲染控件
     */
    fun onLoadImage(url: String?, imageView: ImageView?)
}