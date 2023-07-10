package com.yalantis.ucrop.view

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
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
import com.yalantis.ucrop.view.TransformImageView.TransformImageListener
import com.yalantis.ucrop.model.ExifInfo
import com.yalantis.ucrop.util.BitmapLoadUtils
import com.yalantis.ucrop.util.FastBitmapDrawable
import com.yalantis.ucrop.UCropDevelopConfig
import com.yalantis.ucrop.callback.BitmapLoadCallback

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 */
class GestureCropImageView : CropImageView {
    private var mScaleDetector: ScaleGestureDetector? = null
    private var mRotateDetector: RotationGestureDetector? = null
    private var mGestureDetector: GestureDetector? = null
    private var mMidPntX = 0f
    private var mMidPntY = 0f
    var isRotateEnabled = true
    var isScaleEnabled = true
    var isGestureEnabled = true
    var doubleTapScaleSteps = 5

    constructor(context: Context?) : super(context) {}

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int = 0) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    /**
     * If it's ACTION_DOWN event - user touches the screen and all current animation must be canceled.
     * If it's ACTION_UP event - user removed all fingers from the screen and current image position must be corrected.
     * If there are more than 2 fingers - update focal point coordinates.
     * Pass the event to the gesture detectors if those are enabled.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_DOWN) {
            cancelAllAnimations()
        }
        if (event.pointerCount > 1) {
            mMidPntX = (event.getX(0) + event.getX(1)) / 2
            mMidPntY = (event.getY(0) + event.getY(1)) / 2
        }
        if (isGestureEnabled) {
            mGestureDetector!!.onTouchEvent(event)
        }
        if (isScaleEnabled) {
            mScaleDetector!!.onTouchEvent(event)
        }
        if (isRotateEnabled) {
            mRotateDetector!!.onTouchEvent(event)
        }
        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
            setImageToWrapCropBounds()
        }
        return true
    }

    override fun init() {
        super.init()
        setupGestureListeners()
    }

    /**
     * This method calculates target scale value for double tap gesture.
     * User is able to zoom the image from min scale value
     * to the max scale value with [.mDoubleTapScaleSteps] double taps.
     */
    protected val doubleTapTargetScale: Float
        protected get() = currentScale * Math.pow(
            (maxScale / minScale).toDouble(),
            (1.0f / doubleTapScaleSteps).toDouble()
        ).toFloat()

    private fun setupGestureListeners() {
        mGestureDetector = GestureDetector(context, GestureListener(), null, true)
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mRotateDetector = RotationGestureDetector(RotateListener())
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            postScale(detector.scaleFactor, mMidPntX, mMidPntY)
            return true
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            zoomImageToPosition(doubleTapTargetScale, e.x, e.y, DOUBLE_TAP_ZOOM_DURATION.toLong())
            return super.onDoubleTap(e)
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            postTranslate(-distanceX, -distanceY)
            return true
        }
    }

    private inner class RotateListener : SimpleOnRotationGestureListener() {
        override fun onRotation(rotationDetector: RotationGestureDetector?): Boolean {
            postRotate(rotationDetector!!.angle, mMidPntX, mMidPntY)
            return true
        }
    }

    companion object {
        private const val DOUBLE_TAP_ZOOM_DURATION = 200
    }
}