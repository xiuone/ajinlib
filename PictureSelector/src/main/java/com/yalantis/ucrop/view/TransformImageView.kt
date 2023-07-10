package com.yalantis.ucrop.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
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
import androidx.annotation.IntRange
import androidx.appcompat.widget.AppCompatImageView
import com.yalantis.ucrop.view.TransformImageView.TransformImageListener
import com.yalantis.ucrop.model.ExifInfo
import com.yalantis.ucrop.util.BitmapLoadUtils
import com.yalantis.ucrop.util.FastBitmapDrawable
import com.yalantis.ucrop.UCropDevelopConfig
import com.yalantis.ucrop.UCropImageEngine
import com.yalantis.ucrop.callback.BitmapLoadCallback
import java.lang.Exception

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 *
 *
 * This class provides base logic to setup the image, transform it with matrix (move, scale, rotate),
 * and methods to get current matrix state.
 */
open class TransformImageView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(
    context!!, attrs, defStyle
) {
    protected val mCurrentImageCorners = FloatArray(RECT_CORNER_POINTS_COORDS)
    protected val mCurrentImageCenter = FloatArray(RECT_CENTER_POINT_COORDS)
    private val mMatrixValues = FloatArray(MATRIX_VALUES_COUNT)
    protected var mCurrentImageMatrix = Matrix()
    protected var mThisWidth = 0
    protected var mThisHeight = 0
    protected var mTransformImageListener: TransformImageListener? = null
    private var mInitialImageCorners: FloatArray
    private var mInitialImageCenter: FloatArray
    protected var mBitmapDecoded = false
    protected var mBitmapLaidOut = false
    private var mMaxBitmapSize = 0
    var imageInputPath: String? = null
        private set
    var imageOutputPath: String? = null
        private set
    var imageInputUri: Uri? = null
        private set
    var imageOutputUri: Uri? = null
        private set
    var exifInfo: ExifInfo? = null
        private set

    /**
     * Interface for rotation and scale change notifying.
     */
    interface TransformImageListener {
        fun onLoadComplete()
        fun onLoadFailure(e: Exception)
        fun onRotate(currentAngle: Float)
        fun onScale(currentScale: Float)
    }

    fun setTransformImageListener(transformImageListener: TransformImageListener?) {
        mTransformImageListener = transformImageListener
    }

    override fun setScaleType(scaleType: ScaleType) {
        if (scaleType == ScaleType.MATRIX) {
            super.setScaleType(scaleType)
        } else {
            Log.w(TAG, "Invalid ScaleType. Only ScaleType.MATRIX can be used")
        }
    }

    /**
     * Setter for [.mMaxBitmapSize] value.
     * Be sure to call it before [.setImageURI] or other image setters.
     *
     * @param maxBitmapSize - max size for both width and height of bitmap that will be used in the view.
     */
    var maxBitmapSize: Int
        get() {
            if (mMaxBitmapSize <= 0) {
                mMaxBitmapSize = calculateMaxBitmapSize(context)
            }
            return mMaxBitmapSize
        }
        set(maxBitmapSize) {
            mMaxBitmapSize = maxBitmapSize
        }

    override fun setImageBitmap(bitmap: Bitmap) {
        setImageDrawable(FastBitmapDrawable(bitmap))
    }

    /**
     * This method takes an Uri as a parameter, then calls method to decode it into Bitmap with specified size.
     *
     * @param imageUri - image Uri
     */
    fun setImageUri(imageUri: Uri, outputUri: Uri?, isUseCustomBitmap: Boolean) {
        if (UCropDevelopConfig.imageEngine != null && isUseCustomBitmap) {
            useCustomLoaderCrop(imageUri, outputUri)
        } else {
            useDefaultLoaderCrop(imageUri, outputUri)
        }
    }

    /**
     * use uCrop custom loader
     *
     * @param imageUri
     * @param outputUri
     */
    private fun useCustomLoaderCrop(imageUri: Uri, outputUri: Uri?) {
        val maxImageSize = getMaxImageSize(context, imageUri)
        if (maxImageSize[0] > 0 && maxImageSize[1] > 0) {
            UCropDevelopConfig.imageEngine!!.loadImage(
                context,
                imageUri,
                maxImageSize[0],
                maxImageSize[1],
                object : UCropImageEngine.OnCallbackListener<Bitmap?> {
                    override fun onCall(bitmap: Bitmap) {
                        if (bitmap == null) {
                            useDefaultLoaderCrop(imageUri, outputUri)
                        } else {
                            val copyBitmap = bitmap.copy(bitmap.config, true)
                            setBitmapLoadedResult(
                                copyBitmap,
                                ExifInfo(0, 0, 0),
                                imageUri,
                                outputUri
                            )
                        }
                    }
                })
        } else {
            useDefaultLoaderCrop(imageUri, outputUri)
        }
    }

    /**
     * use uCrop default loader
     *
     * @param imageUri
     * @param outputUri
     */
    private fun useDefaultLoaderCrop(imageUri: Uri, outputUri: Uri?) {
        val maxBitmapSize = maxBitmapSize
        decodeBitmapInBackground(
            context, imageUri, outputUri, maxBitmapSize, maxBitmapSize,
            object : BitmapLoadCallback {
                override fun onBitmapLoaded(
                    bitmap: Bitmap,
                    exifInfo: ExifInfo,
                    imageInputUri: Uri,
                    imageOutputUri: Uri?
                ) {
                    setBitmapLoadedResult(bitmap, exifInfo, imageInputUri, imageOutputUri)
                }

                override fun onFailure(bitmapWorkerException: Exception) {
                    Log.e(TAG, "onFailure: setImageUri", bitmapWorkerException)
                    if (mTransformImageListener != null) {
                        mTransformImageListener!!.onLoadFailure(bitmapWorkerException)
                    }
                }
            })
    }

    /**
     * bitmap loader complete
     *
     * @param bitmap
     * @param exifInfo
     * @param imageInputUri
     * @param imageOutputUri
     */
    fun setBitmapLoadedResult(
        bitmap: Bitmap,
        exifInfo: ExifInfo,
        imageInputUri: Uri,
        imageOutputUri: Uri?
    ) {
        this.imageInputUri = imageInputUri
        this.imageOutputUri = imageOutputUri
        imageInputPath =
            if (isContent(imageInputUri.toString())) imageInputUri.toString() else imageInputUri.path
        imageOutputPath =
            if (imageOutputUri != null) if (isContent(imageOutputUri.toString())) imageOutputUri.toString() else imageOutputUri.path else null
        this.exifInfo = exifInfo
        mBitmapDecoded = true
        setImageBitmap(bitmap)
    }

    /**
     * @return - current image scale value.
     * [1.0f - for original image, 2.0f - for 200% scaled image, etc.]
     */
    val currentScale: Float
        get() = getMatrixScale(mCurrentImageMatrix)

    /**
     * This method calculates scale value for given Matrix object.
     */
    fun getMatrixScale(matrix: Matrix): Float {
        return Math.sqrt(
            Math.pow(getMatrixValue(matrix, Matrix.MSCALE_X).toDouble(), 2.0)
                    + Math.pow(getMatrixValue(matrix, Matrix.MSKEW_Y).toDouble(), 2.0)
        ).toFloat()
    }

    /**
     * @return - current image rotation angle.
     */
    val currentAngle: Float
        get() = getMatrixAngle(mCurrentImageMatrix)

    /**
     * This method calculates rotation angle for given Matrix object.
     */
    fun getMatrixAngle(matrix: Matrix): Float {
        return -(Math.atan2(
            getMatrixValue(matrix, Matrix.MSKEW_X).toDouble(),
            getMatrixValue(matrix, Matrix.MSCALE_X).toDouble()
        ) * (180 / Math.PI)).toFloat()
    }

    override fun setImageMatrix(matrix: Matrix) {
        super.setImageMatrix(matrix)
        mCurrentImageMatrix.set(matrix)
        updateCurrentImagePoints()
    }

    val viewBitmap: Bitmap?
        get() = if (drawable == null || drawable !is FastBitmapDrawable) {
            null
        } else {
            (drawable as FastBitmapDrawable).bitmap
        }

    /**
     * This method translates current image.
     *
     * @param deltaX - horizontal shift
     * @param deltaY - vertical shift
     */
    fun postTranslate(deltaX: Float, deltaY: Float) {
        if (deltaX != 0f || deltaY != 0f) {
            mCurrentImageMatrix.postTranslate(deltaX, deltaY)
            imageMatrix = mCurrentImageMatrix
        }
    }

    /**
     * This method scales current image.
     *
     * @param deltaScale - scale value
     * @param px         - scale center X
     * @param py         - scale center Y
     */
    open fun postScale(deltaScale: Float, px: Float, py: Float) {
        if (deltaScale != 0f) {
            mCurrentImageMatrix.postScale(deltaScale, deltaScale, px, py)
            imageMatrix = mCurrentImageMatrix
            if (mTransformImageListener != null) {
                mTransformImageListener!!.onScale(getMatrixScale(mCurrentImageMatrix))
            }
        }
    }

    /**
     * This method rotates current image.
     *
     * @param deltaAngle - rotation angle
     * @param px         - rotation center X
     * @param py         - rotation center Y
     */
    fun postRotate(deltaAngle: Float, px: Float, py: Float) {
        if (deltaAngle != 0f) {
            mCurrentImageMatrix.postRotate(deltaAngle, px, py)
            imageMatrix = mCurrentImageMatrix
            if (mTransformImageListener != null) {
                mTransformImageListener!!.onRotate(getMatrixAngle(mCurrentImageMatrix))
            }
        }
    }

    protected open fun init() {
        scaleType = ScaleType.MATRIX
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var left = left
        var top = top
        var right = right
        var bottom = bottom
        super.onLayout(changed, left, top, right, bottom)
        if (changed || mBitmapDecoded && !mBitmapLaidOut) {
            left = paddingLeft
            top = paddingTop
            right = width - paddingRight
            bottom = height - paddingBottom
            mThisWidth = right - left
            mThisHeight = bottom - top
            onImageLaidOut()
        }
    }

    /**
     * When image is laid out [.mInitialImageCenter] and [.mInitialImageCenter]
     * must be set.
     */
    protected open fun onImageLaidOut() {
        val drawable = drawable ?: return
        val w = drawable.intrinsicWidth.toFloat()
        val h = drawable.intrinsicHeight.toFloat()
        Log.d(TAG, String.format("Image size: [%d:%d]", w.toInt(), h.toInt()))
        val initialImageRect = RectF(0, 0, w, h)
        mInitialImageCorners = getCornersFromRect(initialImageRect)
        mInitialImageCenter = getCenterFromRect(initialImageRect)
        mBitmapLaidOut = true
        if (mTransformImageListener != null) {
            mTransformImageListener!!.onLoadComplete()
        }
    }

    /**
     * This method returns Matrix value for given index.
     *
     * @param matrix     - valid Matrix object
     * @param valueIndex - index of needed value. See [Matrix.MSCALE_X] and others.
     * @return - matrix value for index
     */
    protected fun getMatrixValue(
        matrix: Matrix,
        @IntRange(
            from = 0,
            to = MATRIX_VALUES_COUNT.toLong()
        ) valueIndex: Int
    ): Float {
        matrix.getValues(mMatrixValues)
        return mMatrixValues[valueIndex]
    }

    /**
     * This method logs given matrix X, Y, scale, and angle values.
     * Can be used for debug.
     */
    protected fun printMatrix(logPrefix: String, matrix: Matrix) {
        val x = getMatrixValue(matrix, Matrix.MTRANS_X)
        val y = getMatrixValue(matrix, Matrix.MTRANS_Y)
        val rScale = getMatrixScale(matrix)
        val rAngle = getMatrixAngle(matrix)
        Log.d(TAG, "$logPrefix: matrix: { x: $x, y: $y, scale: $rScale, angle: $rAngle }")
    }

    /**
     * This method updates current image corners and center points that are stored in
     * [.mCurrentImageCorners] and [.mCurrentImageCenter] arrays.
     * Those are used for several calculations.
     */
    private fun updateCurrentImagePoints() {
        mCurrentImageMatrix.mapPoints(mCurrentImageCorners, mInitialImageCorners)
        mCurrentImageMatrix.mapPoints(mCurrentImageCenter, mInitialImageCenter)
    }

    companion object {
        private const val TAG = "TransformImageView"
        private const val RECT_CORNER_POINTS_COORDS = 8
        private const val RECT_CENTER_POINT_COORDS = 2
        private const val MATRIX_VALUES_COUNT = 9
    }

    init {
        init()
    }
}