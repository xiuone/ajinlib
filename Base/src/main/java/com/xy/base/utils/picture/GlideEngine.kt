package com.xy.base.utils.picture

import android.R
import android.content.Context
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.utils.ActivityCompatHelper
import com.xy.base.utils.exp.getResColor


/**
 * @author：luck
 * @date：2019-11-13 17:02
 * @describe：Glide加载引擎
 */
class GlideEngine: ImageEngine {
    /**
     * 加载图片
     *
     * @param context   上下文
     * @param url       资源url
     * @param imageView 图片承载控件
     */
    override fun loadImage(context: Context?, url: String?, imageView: ImageView?) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        if (context == null || imageView == null)return
        Glide.with(context)
            .load(url)
            .into(imageView)
    }

    override fun loadImage(context: Context?, imageView: ImageView?, url: String?, maxWidth: Int, maxHeight: Int, ) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        if (context == null || imageView == null)return
        Glide.with(context)
            .load(url)
            .override(maxWidth, maxHeight)
            .into(imageView)
    }

    /**
     * 加载相册目录封面
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadAlbumCover(context: Context?, url: String?, imageView: ImageView?) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        if (context == null || imageView == null)return
        Glide.with(context)
            .asBitmap()
            .load(url)
            .override(180, 180)
            .sizeMultiplier(0.5f)
            .transform(CenterCrop(), RoundedCorners(8))
            .placeholder(context.getResColor(R.color.darker_gray).toDrawable())
            .into(imageView)
    }

    /**
     * 加载图片列表图片
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadGridImage(context: Context?, url: String?, imageView: ImageView?) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        if (context == null || imageView == null)return
        Glide.with(context)
            .load(url)
            .override(200, 200)
            .centerCrop()
            .placeholder(context.getResColor(R.color.darker_gray).toDrawable())
            .into(imageView)
    }

    override fun pauseRequests(context: Context?) {
        if (context == null)return
        Glide.with(context).pauseRequests()
    }

    override fun resumeRequests(context: Context?) {
        if (context == null)return
        Glide.with(context).resumeRequests()
    }
}