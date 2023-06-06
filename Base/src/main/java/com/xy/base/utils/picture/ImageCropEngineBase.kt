package com.xy.base.utils.picture

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.PictureSelectionConfig.selectorStyle
import com.luck.picture.lib.engine.CropEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.DateUtils
import com.luck.picture.lib.utils.StyleUtils
import com.xy.base.R
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine
import java.io.File


abstract class ImageCropEngineBase : CropEngine {


    override fun onStartCrop(fragment: Fragment, currentLocalMedia: LocalMedia, dataSource: ArrayList<LocalMedia>, requestCode: Int, ) {
        fragment.context?.run {
            val currentCropPath = currentLocalMedia.availablePath
            val inputUri: Uri = if (PictureMimeType.isContent(currentCropPath) || PictureMimeType.isHasHttp(currentCropPath)) {
                Uri.parse(currentCropPath)
            } else {
                Uri.fromFile(File(currentCropPath))
            }
            val fileName: String = DateUtils.getCreateFileName("CROP_").toString() + ".jpg"
            val destinationUri = Uri.fromFile(File(getSandboxPath(this), fileName))
            val options: UCrop.Options = buildOptions(this)
            val dataCropSource: ArrayList<String> = ArrayList()
            for (i in 0 until dataSource.size) {
                val media = dataSource[i]
                dataCropSource.add(media.availablePath)
            }
            val uCrop = UCrop.of(inputUri, destinationUri, dataCropSource)
            //options.setMultipleCropAspectRatio(buildAspectRatios(dataSource.size()));
            uCrop.withOptions(options)
            uCrop.setImageEngine(object : UCropImageEngine {
                override fun loadImage(context: Context, url: String, imageView: ImageView) {
                    if (!ImageLoaderUtils.assertValidRequest(context)) {
                        return
                    }
                    Glide.with(context).load(url).override(180, 180).into(imageView)
                }

                override fun loadImage(
                    context: Context,
                    url: Uri,
                    maxWidth: Int,
                    maxHeight: Int,
                    call: UCropImageEngine.OnCallbackListener<Bitmap>,
                ) {
                }
            })
            uCrop.start(this, fragment, requestCode)
        }

    }

    private fun getSandboxPath(context: Context): String {
        val externalFilesDir: File = context.filesDir
        val customFile = File(externalFilesDir.absolutePath, "Sandbox")
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile.absolutePath + File.separator
    }

    private fun buildOptions(context: Context): UCrop.Options {
        val options = UCrop.Options()
        options.setHideBottomControls(false)
        options.setFreeStyleCropEnabled(false)
        options.setShowCropFrame(true)
        options.setShowCropGrid(false)
        options.setCircleDimmedLayer(isCircle())
        options.withAspectRatio(withAspectRatioX(), withAspectRatioY())
        options.setCropOutputPathDir(getSandboxPath(context))
        options.isCropDragSmoothToCenter(false)
        options.isForbidCropGifWebp(true)
        options.isForbidSkipMultipleCrop(false)
        options.setMaxScaleMultiplier(100f)
        if (selectorStyle != null && selectorStyle.selectMainStyle.statusBarColor != 0) {
            val mainStyle = selectorStyle.selectMainStyle
            val isDarkStatusBarBlack = mainStyle.isDarkStatusBarBlack
            val statusBarColor = mainStyle.statusBarColor
            options.isDarkStatusBarBlack(isDarkStatusBarBlack)
            if (StyleUtils.checkStyleValidity(statusBarColor)) {
                options.setStatusBarColor(statusBarColor)
                options.setToolbarColor(statusBarColor)
            } else {
                options.setStatusBarColor(ContextCompat.getColor(context, R.color.ps_color_black))
                options.setToolbarColor(ContextCompat.getColor(context, R.color.ps_color_grey))
            }
            val titleBarStyle = selectorStyle.titleBarStyle
            if (StyleUtils.checkStyleValidity(titleBarStyle.titleTextColor)) {
                options.setToolbarWidgetColor(titleBarStyle.titleTextColor)
            } else {
                options.setToolbarWidgetColor(ContextCompat.getColor(context,
                    R.color.ps_color_white))
            }
        } else {
            options.setStatusBarColor(ContextCompat.getColor(context, R.color.ps_color_grey))
            options.setToolbarColor(ContextCompat.getColor(context, R.color.ps_color_grey))
            options.setToolbarWidgetColor(ContextCompat.getColor(context,
                R.color.ps_color_white))
        }
        return options
    }

    open fun withAspectRatioX():Float = 1F
    open fun withAspectRatioY():Float = 1F
    open fun isCircle():Boolean = false
}