package com.yalantis.ucrop.view

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.yalantis.ucrop.model.AspectRatio.aspectRatioTitle
import com.yalantis.ucrop.model.AspectRatio.aspectRatioX
import com.yalantis.ucrop.model.AspectRatio.aspectRatioY
import com.yalantis.ucrop.util.RectUtils.trapToRect
import com.yalantis.ucrop.model.CropParameters.contentImageInputUri
import com.yalantis.ucrop.model.CropParameters.contentImageOutputUri
import com.yalantis.ucrop.callback.CropBoundsChangeListener.onCropAspectRatioChanged
import com.yalantis.ucrop.util.RectUtils.getRectSidesFromCorners
import com.yalantis.ucrop.util.RectUtils.getCornersFromRect
import com.yalantis.ucrop.util.CubicEasing.easeOut
import com.yalantis.ucrop.util.CubicEasing.easeInOut
import com.yalantis.ucrop.util.RotationGestureDetector.onTouchEvent
import com.yalantis.ucrop.util.RotationGestureDetector.angle
import com.yalantis.ucrop.callback.OverlayViewChangeListener.onCropRectUpdated
import com.yalantis.ucrop.util.RectUtils.getCenterFromRect
import com.yalantis.ucrop.util.DensityUtil.dip2px
import com.yalantis.ucrop.callback.OverlayViewChangeListener.postTranslate
import com.yalantis.ucrop.util.BitmapLoadUtils.calculateMaxBitmapSize
import com.yalantis.ucrop.util.BitmapLoadUtils.getMaxImageSize
import com.yalantis.ucrop.UCropImageEngine.loadImage
import com.yalantis.ucrop.util.BitmapLoadUtils.decodeBitmapInBackground
import com.yalantis.ucrop.util.FileUtils.isContent
import com.yalantis.ucrop.util.FastBitmapDrawable.bitmap
import androidx.appcompat.widget.AppCompatTextView
import kotlin.jvm.JvmOverloads
import androidx.annotation.ColorInt
import com.yalantis.ucrop.view.CropImageView
import androidx.core.content.ContextCompat
import com.yalantis.ucrop.view.widget.HorizontalProgressWheelView.ScrollingListener
import com.yalantis.ucrop.view.TransformImageView
import com.yalantis.ucrop.callback.CropBoundsChangeListener
import com.yalantis.ucrop.callback.BitmapCropCallback
import com.yalantis.ucrop.model.ImageState
import com.yalantis.ucrop.util.RectUtils
import com.yalantis.ucrop.model.CropParameters
import com.yalantis.ucrop.task.BitmapCropTask
import com.yalantis.ucrop.view.CropImageView.WrapCropBoundsRunnable
import com.yalantis.ucrop.view.CropImageView.ZoomImageToPosition
import com.yalantis.ucrop.util.CubicEasing
import com.yalantis.ucrop.util.RotationGestureDetector
import com.yalantis.ucrop.view.GestureCropImageView.GestureListener
import com.yalantis.ucrop.view.GestureCropImageView.ScaleListener
import com.yalantis.ucrop.view.GestureCropImageView.RotateListener
import com.yalantis.ucrop.view.GestureCropImageView
import com.yalantis.ucrop.util.RotationGestureDetector.SimpleOnRotationGestureListener
import com.yalantis.ucrop.view.OverlayView.FreestyleMode
import com.yalantis.ucrop.view.OverlayView
import com.yalantis.ucrop.callback.OverlayViewChangeListener
import androidx.annotation.IntDef
import androidx.appcompat.widget.AppCompatImageView
import com.luck.picture.lib.R
import com.yalantis.ucrop.view.TransformImageView.TransformImageListener
import com.yalantis.ucrop.model.ExifInfo
import com.yalantis.ucrop.util.BitmapLoadUtils
import com.yalantis.ucrop.util.FastBitmapDrawable
import com.yalantis.ucrop.UCropDevelopConfig
import com.yalantis.ucrop.callback.BitmapLoadCallback

class UCropView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    var cropImageView: GestureCropImageView
        private set
    val overlayView: OverlayView
    private fun setListenersToViews() {
        cropImageView.cropBoundsChangeListener = object : CropBoundsChangeListener {
            override fun onCropAspectRatioChanged(cropRatio: Float) {
                overlayView.setTargetAspectRatio(cropRatio)
            }
        }
        overlayView.overlayViewChangeListener = object : OverlayViewChangeListener {
            override fun onCropRectUpdated(cropRect: RectF?) {
                cropImageView.setCropRect(cropRect!!)
            }

            override fun postTranslate(deltaX: Float, deltaY: Float) {
                cropImageView.postTranslate(deltaX, deltaY)
            }
        }
    }

    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }

    /**
     * Method for reset state for UCropImageView such as rotation, scale, translation.
     * Be careful: this method recreate UCropImageView instance and reattach it to layout.
     */
    fun resetCropImageView() {
        removeView(cropImageView)
        cropImageView = GestureCropImageView(context)
        setListenersToViews()
        cropImageView.setCropRect(overlayView.cropViewRect)
        addView(cropImageView, 0)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.ucrop_view, this, true)
        cropImageView = findViewById(R.id.image_view_crop)
        overlayView = findViewById(R.id.view_overlay)
        val a = context.obtainStyledAttributes(attrs, R.styleable.ucrop_UCropView)
        overlayView.processStyledAttributes(a)
        cropImageView.processStyledAttributes(a)
        a.recycle()
        setListenersToViews()
    }
}