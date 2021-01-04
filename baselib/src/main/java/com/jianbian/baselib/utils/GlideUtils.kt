package com.jianbian.baselib.utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.jianbian.baselib.BaseApp
import com.jianbian.baselib.R

object GlideUtils {

    var placePic:Int = R.color.gray_9999
    var errPic:Int = R.color.gray_9999

    fun getOption(transformation: Transformation<Bitmap>, placePic: Int = GlideUtils.placePic, errPic: Int = GlideUtils.errPic): RequestOptions {
        return  RequestOptions.bitmapTransform(transformation)
            .dontAnimate()
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(placePic)
            .error(errPic)
    }


    fun getCenterCropOption(placePic: Int = GlideUtils.placePic, errPic: Int = GlideUtils.errPic): RequestOptions {
        return  RequestOptions()
            .centerCrop()
            .dontAnimate()
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(placePic)
            .error(errPic)
    }

    fun getOption(placePic: Int = GlideUtils.placePic, errPic: Int = GlideUtils.errPic):RequestOptions{
        return  RequestOptions()
            .dontAnimate()
            .format(DecodeFormat.PREFER_RGB_565)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(placePic)
            .error(errPic)
    }



    fun show(`object`: Any?, imageView: ImageView?
             ,requestOptions :RequestOptions?) {
        if (`object` != null && imageView != null && BaseApp.context != null) {
            var requestBuilder = Glide.with(BaseApp.context!!)
                .load(`object`)
            if (requestOptions != null)
                requestBuilder.apply(requestOptions)
            requestBuilder.thumbnail(0.5f)
                .into(imageView)
        }
    }

    fun pauseRequests(){
        if (BaseApp.context != null)
            Glide.with(BaseApp.context!!).pauseRequests();
    }
}