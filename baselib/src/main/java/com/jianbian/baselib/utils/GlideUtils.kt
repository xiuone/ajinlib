package com.jianbian.baselib.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.app.Service
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jianbian.baselib.R

object GlideUtils {

    var placePic: Int = R.mipmap.icon_splash
    var errPic: Int = R.mipmap.icon_splash

    private fun getOption(placePic: Int, errPic: Int): RequestOptions {
        return RequestOptions()
            .centerCrop()
            .dontAnimate()
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(placePic)
            .error(errPic)
    }

    fun show(context: Context?, `object`: Any?, imageView: ImageView?, placeholder: Int, errPic: Int) {
        if (`object` != null && imageView != null) {
            Glide.with(context!!)
                .load(`object`)
                .thumbnail(0.5f)
                .apply(getOption(placeholder, errPic))
                .into(imageView)
        }
    }

    fun show(context: Context?, `object`: Any?, imageView: ImageView?) {
        if (`object` != null && imageView != null) {
            Glide.with(context!!)
                .load(`object`)
                .apply(getOption(placePic, errPic))
                .thumbnail(0.5f)
                .into(imageView)
        }
    }


}