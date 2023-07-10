package xy.xy.base.assembly.picture.select.crop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import xy.xy.base.R
import picture.luck.picture.lib.engine.CropFileEngine
import picture.luck.picture.lib.style.PictureSelectorStyle
import picture.luck.picture.lib.utils.StyleUtils
import xy.xy.base.utils.ContextHolder
import xy.xy.base.assembly.picture.select.ImageLoaderUtils
import ucrop.yalantis.ucrop.UCrop
import ucrop.yalantis.ucrop.UCropImageEngine
import java.io.File


abstract class ImageCropEngineBase :
    CropFileEngine {
    private val context by lazy { ContextHolder.getContext() }
    private val selectorStyle by lazy { onCreateSelectStyle() }



    override fun onStartCrop(fragment: Fragment, srcUri: Uri?, destinationUri: Uri?,
                             dataSource: ArrayList<String>?, requestCode: Int) {
        val context = this.context?:return
        if (srcUri == null || destinationUri == null)return
        val options = buildOptions(context)
        val uCrop = UCrop.of(srcUri, destinationUri, dataSource)
        uCrop.withOptions(options)
        uCrop.setImageEngine(object : UCropImageEngine {
            override fun loadImage(context: Context?, url: String?, imageView: ImageView?) {
                if (context == null || url == null || imageView == null)return
                if (!ImageLoaderUtils.assertValidRequest(context))return
                Glide.with(context).load(url).override(180, 180).into(imageView)
            }

            override fun loadImage(context: Context?, url: Uri?,
                maxWidth: Int, maxHeight: Int, call: UCropImageEngine.OnCallbackListener<Bitmap?>?) {
                if (context == null){
                    call?.onCall(null)
                    return
                }
                Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight)
                    .into(object : CustomTarget<Bitmap?>() {

                        override fun onLoadCleared(placeholder: Drawable?) {
                            call?.onCall(null)
                        }

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                            call?.onCall(resource)
                        }
                    })
            }
        })
        uCrop.start(fragment.requireActivity(), fragment, requestCode)
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
        val selectorStyle = this.selectorStyle
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

    open fun onCreateSelectStyle(): PictureSelectorStyle?= null
}