package com.xy.baselib.utils

import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.xy.baselib.BaseApp
import com.xy.baselib.R
import com.xy.baselib.utils.gilde.ProgressManager
import com.xy.baselib.view.GlideLoadImgView
import com.xy.baselib.view.GlidePhotoImgView

object GlideUtils {

    var placePic:Int = R.color.transparent
    var errPic:Int = R.mipmap.icon_logo

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
            if (imageView is GlideLoadImgView)
                ProgressManager.addListener(`object`,imageView.glideProgressController)
            else if (imageView is GlidePhotoImgView)
                ProgressManager.addListener(`object`,imageView.glideProgressController)
            var requestBuilder = Glide.with(BaseApp.context!!)
                .load(`object`)
            if (requestOptions != null)
                requestBuilder.apply(requestOptions)
            requestBuilder.thumbnail(0.5f)
                .into(imageView)
        }
    }

}