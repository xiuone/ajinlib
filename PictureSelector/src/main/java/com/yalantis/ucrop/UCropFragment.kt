package com.yalantis.ucrop

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.*
import com.yalantis.ucrop.model.AspectRatio.aspectRatioX
import com.yalantis.ucrop.model.AspectRatio.aspectRatioY
import com.yalantis.ucrop.statusbar.ImmersiveManager.immersiveAboveAPI23
import com.yalantis.ucrop.util.FileUtils.replaceOutputUri
import com.yalantis.ucrop.util.FileUtils.getMimeTypeFromMediaContentUri
import com.yalantis.ucrop.util.FileUtils.isGif
import com.yalantis.ucrop.util.FileUtils.isWebp
import com.yalantis.ucrop.util.FileUtils.getInputPath
import com.yalantis.ucrop.util.FileUtils.isContent
import com.yalantis.ucrop.util.FileUtils.getPath
import com.yalantis.ucrop.util.FileUtils.isUrlHasVideo
import com.yalantis.ucrop.util.FileUtils.isHasVideo
import com.yalantis.ucrop.util.FileUtils.isHasAudio
import com.yalantis.ucrop.util.FileUtils.isHasHttp
import com.yalantis.ucrop.util.FileUtils.getPostfixDefaultJPEG
import com.yalantis.ucrop.util.FileUtils.getCreateFileName
import com.yalantis.ucrop.util.FileUtils.createFileName
import com.yalantis.ucrop.util.DensityUtil.dip2px
import okhttp3.OkHttpClient
import com.yalantis.ucrop.OkHttpClientStore
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine
import com.yalantis.ucrop.UCropDevelopConfig
import kotlin.jvm.JvmOverloads
import com.yalantis.ucrop.UCropMultipleActivity
import com.yalantis.ucrop.UCropActivity
import com.yalantis.ucrop.UCropFragment
import androidx.annotation.FloatRange
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.IntDef
import com.yalantis.ucrop.view.UCropView
import com.yalantis.ucrop.view.GestureCropImageView
import com.yalantis.ucrop.view.OverlayView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.yalantis.ucrop.view.CropImageView
import androidx.appcompat.content.res.AppCompatResources
import com.yalantis.ucrop.view.TransformImageView.TransformImageListener
import com.yalantis.ucrop.util.SelectedStateListDrawable
import com.yalantis.ucrop.view.widget.AspectRatioTextView
import com.yalantis.ucrop.view.widget.HorizontalProgressWheelView
import com.yalantis.ucrop.view.widget.HorizontalProgressWheelView.ScrollingListener
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.luck.picture.lib.R
import com.yalantis.ucrop.callback.BitmapCropCallback
import com.yalantis.ucrop.UCropFragmentCallback
import com.yalantis.ucrop.UCropFragment.UCropResult
import com.yalantis.ucrop.UCropGalleryAdapter
import com.yalantis.ucrop.model.AspectRatio
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*

class UCropFragment constructor() : Fragment() {
    @IntDef(NONE, SCALE, ROTATE, ALL)
    @Retention(RetentionPolicy.SOURCE)
    annotation class GestureTypes constructor()

    private var callback: UCropFragmentCallback? = null
    private var isUseCustomBitmap: Boolean = false
    private var mActiveControlsWidgetColor: Int = 0

    @ColorInt
    private var mRootViewBackgroundColor: Int = 0
    private var mLogoColor: Int = 0
    private var mShowBottomControls: Boolean = false
    private var mControlsTransition: Transition? = null
    private var mUCropView: UCropView? = null
    private var mGestureCropImageView: GestureCropImageView? = null
    private var mOverlayView: OverlayView? = null
    private var mWrapperStateAspectRatio: ViewGroup? = null
    private var mWrapperStateRotate: ViewGroup? = null
    private var mWrapperStateScale: ViewGroup? = null
    private var mLayoutAspectRatio: ViewGroup? = null
    private var mLayoutRotate: ViewGroup? = null
    private var mLayoutScale: ViewGroup? = null
    private val mCropAspectRatioViews: MutableList<ViewGroup> = ArrayList()
    private var mTextViewRotateAngle: TextView? = null
    private var mTextViewScalePercent: TextView? = null
    private var mBlockingView: View? = null
    private var mCompressFormat: Bitmap.CompressFormat = DEFAULT_COMPRESS_FORMAT
    private var mCompressQuality: Int = DEFAULT_COMPRESS_QUALITY
    private var mAllowedGestures: IntArray = intArrayOf(SCALE, ROTATE, ALL)

    companion object {
        val DEFAULT_COMPRESS_QUALITY: Int = 90
        val DEFAULT_COMPRESS_FORMAT: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
        val NONE: Int = 0
        val SCALE: Int = 1
        val ROTATE: Int = 2
        val ALL: Int = 3
        val TAG: String = UCropFragment::class.java.getSimpleName()
        private val CONTROLS_ANIMATION_DURATION: Long = 50
        private val TABS_COUNT: Int = 3
        private val SCALE_WIDGET_SENSITIVITY_COEFFICIENT: Int = 15000
        private val ROTATE_WIDGET_SENSITIVITY_COEFFICIENT: Int = 42
        fun newInstance(uCrop: Bundle?): UCropFragment {
            val fragment: UCropFragment = UCropFragment()
            fragment.setArguments(uCrop)
            return fragment
        }

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    public override fun onAttach(context: Context) {
        super.onAttach(context)
        if (getParentFragment() is UCropFragmentCallback) callback =
            getParentFragment() as UCropFragmentCallback? else if (context is UCropFragmentCallback) callback =
            context else throw IllegalArgumentException(
            (context.toString()
                    + " must implement UCropFragmentCallback")
        )
    }

    fun setCallback(callback: UCropFragmentCallback?) {
        this.callback = callback
    }

    public override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.ucrop_fragment_photobox, container, false)
        val args: Bundle? = getArguments()
        setupViews(rootView, args)
        setImageData((args)!!)
        setInitialState()
        addBlockingView(rootView)
        return rootView
    }

    /**
     * Fragment重新可见
     */
    fun fragmentReVisible() {
        setImageData((getArguments())!!)
        mUCropView!!.animate().alpha(1f).setDuration(300).setInterpolator(AccelerateInterpolator())
        callback!!.loadingProgress(false)
        var isClickable: Boolean = false
        if (getArguments()!!.getBoolean(
                UCrop.Options.Companion.EXTRA_CROP_FORBID_GIF_WEBP,
                false
            )
        ) {
            val inputUri: Uri? = getArguments()!!.getParcelable(UCrop.Companion.EXTRA_INPUT_URI)
            val mimeType: String? = getMimeTypeFromMediaContentUri(
                (getContext())!!, (inputUri)!!
            )
            isClickable = isGif(mimeType) || isWebp(mimeType)
        }
        mBlockingView!!.setClickable(isClickable)
    }

    fun setupViews(view: View, args: Bundle?) {
        mActiveControlsWidgetColor = args!!.getInt(
            UCrop.Options.Companion.EXTRA_UCROP_COLOR_CONTROLS_WIDGET_ACTIVE,
            ContextCompat.getColor(
                (getContext())!!, R.color.ucrop_color_active_controls_color
            )
        )
        mLogoColor = args.getInt(
            UCrop.Options.Companion.EXTRA_UCROP_LOGO_COLOR, ContextCompat.getColor(
                (getContext())!!, R.color.ucrop_color_default_logo
            )
        )
        mShowBottomControls =
            !args.getBoolean(UCrop.Options.Companion.EXTRA_HIDE_BOTTOM_CONTROLS, false)
        mRootViewBackgroundColor = args.getInt(
            UCrop.Options.Companion.EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR, ContextCompat.getColor(
                (getContext())!!, R.color.ucrop_color_crop_background
            )
        )
        initiateRootViews(view)
        callback!!.loadingProgress(true)
        if (mShowBottomControls) {
            val wrapper: ViewGroup = view.findViewById(R.id.controls_wrapper)
            wrapper.setVisibility(View.VISIBLE)
            LayoutInflater.from(getContext()).inflate(R.layout.ucrop_controls, wrapper, true)
            mControlsTransition = AutoTransition()
            mControlsTransition.setDuration(CONTROLS_ANIMATION_DURATION)
            mWrapperStateAspectRatio = view.findViewById(R.id.state_aspect_ratio)
            mWrapperStateAspectRatio.setOnClickListener(mStateClickListener)
            mWrapperStateRotate = view.findViewById(R.id.state_rotate)
            mWrapperStateRotate.setOnClickListener(mStateClickListener)
            mWrapperStateScale = view.findViewById(R.id.state_scale)
            mWrapperStateScale.setOnClickListener(mStateClickListener)
            mLayoutAspectRatio = view.findViewById(R.id.layout_aspect_ratio)
            mLayoutRotate = view.findViewById(R.id.layout_rotate_wheel)
            mLayoutScale = view.findViewById(R.id.layout_scale_wheel)
            setupAspectRatioWidget((args), view)
            setupRotateWidget(view)
            setupScaleWidget(view)
            setupStatesWrapper(view)
        } else {
            val params: RelativeLayout.LayoutParams = view.findViewById<View>(R.id.ucrop_frame)
                .getLayoutParams() as RelativeLayout.LayoutParams
            params.bottomMargin = 0
            view.findViewById<View>(R.id.ucrop_frame).requestLayout()
        }
    }

    private fun setImageData(bundle: Bundle) {
        val inputUri: Uri? = bundle.getParcelable(UCrop.Companion.EXTRA_INPUT_URI)
        var outputUri: Uri? = bundle.getParcelable(UCrop.Companion.EXTRA_OUTPUT_URI)
        processOptions(bundle)
        if (inputUri != null && outputUri != null) {
            try {
                val isForbidCropGifWebp: Boolean =
                    bundle.getBoolean(UCrop.Options.Companion.EXTRA_CROP_FORBID_GIF_WEBP, false)
                outputUri =
                    replaceOutputUri((getContext())!!, isForbidCropGifWebp, inputUri, outputUri)
                mGestureCropImageView!!.setImageUri(inputUri, outputUri, isUseCustomBitmap)
            } catch (e: Exception) {
                callback!!.onCropFinish(getError(e))
            }
        } else {
            callback!!.onCropFinish(getError(NullPointerException(getString(R.string.ucrop_error_input_data_is_absent))))
        }
    }

    /**
     * This method extracts [#optionsBundle][UCrop.Options] from incoming bundle
     * and setups fragment, [OverlayView] and [CropImageView] properly.
     */
    private fun processOptions(bundle: Bundle) {
        // Bitmap compression options
        val compressionFormatName: String? =
            bundle.getString(UCrop.Options.Companion.EXTRA_COMPRESSION_FORMAT_NAME)
        var compressFormat: Bitmap.CompressFormat? = null
        if (!TextUtils.isEmpty(compressionFormatName)) {
            compressFormat = Bitmap.CompressFormat.valueOf((compressionFormatName)!!)
        }
        mCompressFormat = if ((compressFormat == null)) DEFAULT_COMPRESS_FORMAT else compressFormat
        mCompressQuality = bundle.getInt(
            UCrop.Options.Companion.EXTRA_COMPRESSION_QUALITY,
            UCropActivity.Companion.DEFAULT_COMPRESS_QUALITY
        )
        isUseCustomBitmap =
            bundle.getBoolean(UCrop.Options.Companion.EXTRA_CROP_CUSTOM_LOADER_BITMAP, false)

        // Gestures options
        val allowedGestures: IntArray? =
            bundle.getIntArray(UCrop.Options.Companion.EXTRA_ALLOWED_GESTURES)
        if (allowedGestures != null && allowedGestures.size == TABS_COUNT) {
            mAllowedGestures = allowedGestures
        }

        // Crop image view options
        mGestureCropImageView!!.maxBitmapSize = bundle.getInt(
            UCrop.Options.Companion.EXTRA_MAX_BITMAP_SIZE,
            CropImageView.DEFAULT_MAX_BITMAP_SIZE
        )
        mGestureCropImageView!!.setMaxScaleMultiplier(
            bundle.getFloat(
                UCrop.Options.Companion.EXTRA_MAX_SCALE_MULTIPLIER,
                CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER
            )
        )
        mGestureCropImageView!!.setImageToWrapCropBoundsAnimDuration(
            bundle.getInt(
                UCrop.Options.Companion.EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION,
                CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION
            ).toLong()
        )

        // Overlay view options
        mOverlayView!!.isFreestyleCropEnabled = bundle.getBoolean(
            UCrop.Options.Companion.EXTRA_FREE_STYLE_CROP,
            OverlayView.DEFAULT_FREESTYLE_CROP_MODE != OverlayView.FREESTYLE_CROP_MODE_DISABLE
        )
        mOverlayView!!.setDragSmoothToCenter(
            bundle.getBoolean(
                UCrop.Options.Companion.EXTRA_CROP_DRAG_CENTER,
                false
            )
        )
        mOverlayView!!.setDimmedColor(
            bundle.getInt(
                UCrop.Options.Companion.EXTRA_DIMMED_LAYER_COLOR, getResources().getColor(
                    R.color.ucrop_color_default_dimmed
                )
            )
        )
        mOverlayView!!.setCircleStrokeColor(
            bundle.getInt(
                UCrop.Options.Companion.EXTRA_CIRCLE_STROKE_COLOR, getResources().getColor(
                    R.color.ucrop_color_default_dimmed
                )
            )
        )
        mOverlayView!!.setCircleDimmedLayer(
            bundle.getBoolean(
                UCrop.Options.Companion.EXTRA_CIRCLE_DIMMED_LAYER,
                OverlayView.DEFAULT_CIRCLE_DIMMED_LAYER
            )
        )
        mOverlayView!!.setShowCropFrame(
            bundle.getBoolean(
                UCrop.Options.Companion.EXTRA_SHOW_CROP_FRAME,
                OverlayView.DEFAULT_SHOW_CROP_FRAME
            )
        )
        mOverlayView!!.setCropFrameColor(
            bundle.getInt(
                UCrop.Options.Companion.EXTRA_CROP_FRAME_COLOR, getResources().getColor(
                    R.color.ucrop_color_default_crop_frame
                )
            )
        )
        mOverlayView!!.setCropFrameStrokeWidth(
            bundle.getInt(
                UCrop.Options.Companion.EXTRA_CROP_FRAME_STROKE_WIDTH,
                getResources().getDimensionPixelSize(
                    R.dimen.ucrop_default_crop_frame_stoke_width
                )
            )
        )
        mOverlayView!!.setShowCropGrid(
            bundle.getBoolean(
                UCrop.Options.Companion.EXTRA_SHOW_CROP_GRID,
                OverlayView.DEFAULT_SHOW_CROP_GRID
            )
        )
        mOverlayView!!.setCropGridRowCount(
            bundle.getInt(
                UCrop.Options.Companion.EXTRA_CROP_GRID_ROW_COUNT,
                OverlayView.DEFAULT_CROP_GRID_ROW_COUNT
            )
        )
        mOverlayView!!.setCropGridColumnCount(
            bundle.getInt(
                UCrop.Options.Companion.EXTRA_CROP_GRID_COLUMN_COUNT,
                OverlayView.DEFAULT_CROP_GRID_COLUMN_COUNT
            )
        )
        mOverlayView!!.setCropGridColor(
            bundle.getInt(
                UCrop.Options.Companion.EXTRA_CROP_GRID_COLOR, getResources().getColor(
                    R.color.ucrop_color_default_crop_grid
                )
            )
        )
        mOverlayView!!.setCropGridStrokeWidth(
            bundle.getInt(
                UCrop.Options.Companion.EXTRA_CROP_GRID_STROKE_WIDTH,
                getResources().getDimensionPixelSize(
                    R.dimen.ucrop_default_crop_grid_stoke_width
                )
            )
        )
        mOverlayView!!.setDimmedStrokeWidth(
            bundle.getInt(
                UCrop.Options.Companion.EXTRA_CIRCLE_STROKE_WIDTH_LAYER,
                getResources().getDimensionPixelSize(
                    R.dimen.ucrop_default_crop_grid_stoke_width
                )
            )
        )
        // Aspect ratio options
        val aspectRatioX: Float = bundle.getFloat(UCrop.Companion.EXTRA_ASPECT_RATIO_X, -1f)
        val aspectRatioY: Float = bundle.getFloat(UCrop.Companion.EXTRA_ASPECT_RATIO_Y, -1f)
        val aspectRationSelectedByDefault: Int =
            bundle.getInt(UCrop.Options.Companion.EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, 0)
        val aspectRatioList: ArrayList<AspectRatio>? =
            bundle.getParcelableArrayList(UCrop.Options.Companion.EXTRA_ASPECT_RATIO_OPTIONS)
        if (aspectRatioX >= 0 && aspectRatioY >= 0) {
            if (mWrapperStateAspectRatio != null) {
                mWrapperStateAspectRatio!!.setVisibility(View.GONE)
            }
            val targetAspectRatio: Float = aspectRatioX / aspectRatioY
            mGestureCropImageView!!.targetAspectRatio = if (java.lang.Float.isNaN(targetAspectRatio)) CropImageView.SOURCE_IMAGE_ASPECT_RATIO else targetAspectRatio
        } else if (aspectRatioList != null && aspectRationSelectedByDefault < aspectRatioList.size) {
            val targetAspectRatio: Float =
                aspectRatioList.get(aspectRationSelectedByDefault).aspectRatioX / aspectRatioList.get(
                    aspectRationSelectedByDefault
                ).aspectRatioY
            mGestureCropImageView!!.targetAspectRatio = if (java.lang.Float.isNaN(targetAspectRatio)) CropImageView.SOURCE_IMAGE_ASPECT_RATIO else targetAspectRatio
        } else {
            mGestureCropImageView!!.targetAspectRatio = CropImageView.SOURCE_IMAGE_ASPECT_RATIO
        }

        // Result bitmap max size options
        val maxSizeX: Int = bundle.getInt(UCrop.Companion.EXTRA_MAX_SIZE_X, 0)
        val maxSizeY: Int = bundle.getInt(UCrop.Companion.EXTRA_MAX_SIZE_Y, 0)
        if (maxSizeX > 0 && maxSizeY > 0) {
            mGestureCropImageView!!.setMaxResultImageSizeX(maxSizeX)
            mGestureCropImageView!!.setMaxResultImageSizeY(maxSizeY)
        }
    }

    private fun initiateRootViews(view: View) {
        mUCropView = view.findViewById(R.id.ucrop)
        mGestureCropImageView = mUCropView.cropImageView
        mOverlayView = mUCropView.overlayView
        mGestureCropImageView!!.setTransformImageListener(mImageListener)
        (view.findViewById<View>(R.id.image_view_logo) as ImageView).setColorFilter(
            mLogoColor,
            PorterDuff.Mode.SRC_ATOP
        )
        view.findViewById<View>(R.id.ucrop_frame).setBackgroundColor(mRootViewBackgroundColor)
    }

    private val mImageListener: TransformImageListener = object : TransformImageListener {
        public override fun onRotate(currentAngle: Float) {
            setAngleText(currentAngle)
        }

        public override fun onScale(currentScale: Float) {
            setScaleText(currentScale)
        }

        public override fun onLoadComplete() {
            mUCropView!!.animate().alpha(1f).setDuration(300)
                .setInterpolator(AccelerateInterpolator())
            mBlockingView!!.setClickable(false)
            callback!!.loadingProgress(false)
            if (getArguments()!!.getBoolean(
                    UCrop.Options.Companion.EXTRA_CROP_FORBID_GIF_WEBP,
                    false
                )
            ) {
                val inputUri: Uri? = getArguments()!!.getParcelable(UCrop.Companion.EXTRA_INPUT_URI)
                val mimeType: String? = getMimeTypeFromMediaContentUri(
                    (getContext())!!, (inputUri)!!
                )
                if (isGif(mimeType) || isWebp(mimeType)) {
                    mBlockingView!!.setClickable(true)
                }
            }
        }

        public override fun onLoadFailure(e: Exception) {
            callback!!.onCropFinish(getError(e))
        }
    }

    /**
     * Use [.mActiveControlsWidgetColor] for color filter
     */
    private fun setupStatesWrapper(view: View) {
        val stateScaleImageView: ImageView = view.findViewById(R.id.image_view_state_scale)
        val stateRotateImageView: ImageView = view.findViewById(R.id.image_view_state_rotate)
        val stateAspectRatioImageView: ImageView =
            view.findViewById(R.id.image_view_state_aspect_ratio)
        stateScaleImageView.setImageDrawable(
            SelectedStateListDrawable(
                stateScaleImageView.getDrawable(),
                mActiveControlsWidgetColor
            )
        )
        stateRotateImageView.setImageDrawable(
            SelectedStateListDrawable(
                stateRotateImageView.getDrawable(),
                mActiveControlsWidgetColor
            )
        )
        stateAspectRatioImageView.setImageDrawable(
            SelectedStateListDrawable(
                stateAspectRatioImageView.getDrawable(),
                mActiveControlsWidgetColor
            )
        )
    }

    private fun setupAspectRatioWidget(bundle: Bundle, view: View) {
        var aspectRationSelectedByDefault: Int =
            bundle.getInt(UCrop.Options.Companion.EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, 0)
        var aspectRatioList: ArrayList<AspectRatio?>? =
            bundle.getParcelableArrayList(UCrop.Options.Companion.EXTRA_ASPECT_RATIO_OPTIONS)
        if (aspectRatioList == null || aspectRatioList.isEmpty()) {
            aspectRationSelectedByDefault = 2
            aspectRatioList = ArrayList()
            aspectRatioList.add(AspectRatio(null, 1, 1))
            aspectRatioList.add(AspectRatio(null, 3, 4))
            aspectRatioList.add(
                AspectRatio(
                    getString(R.string.ucrop_label_original).uppercase(Locale.getDefault()),
                    CropImageView.SOURCE_IMAGE_ASPECT_RATIO, CropImageView.SOURCE_IMAGE_ASPECT_RATIO
                )
            )
            aspectRatioList.add(AspectRatio(null, 3, 2))
            aspectRatioList.add(AspectRatio(null, 16, 9))
        }
        val wrapperAspectRatioList: LinearLayout = view.findViewById(R.id.layout_aspect_ratio)
        var wrapperAspectRatio: FrameLayout
        var aspectRatioTextView: AspectRatioTextView
        val lp: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
        lp.weight = 1f
        for (aspectRatio: AspectRatio? in aspectRatioList) {
            wrapperAspectRatio =
                getLayoutInflater().inflate(R.layout.ucrop_aspect_ratio, null) as FrameLayout
            wrapperAspectRatio.setLayoutParams(lp)
            aspectRatioTextView = (wrapperAspectRatio.getChildAt(0) as AspectRatioTextView)
            aspectRatioTextView.setActiveColor(mActiveControlsWidgetColor)
            aspectRatioTextView.setAspectRatio((aspectRatio)!!)
            wrapperAspectRatioList.addView(wrapperAspectRatio)
            mCropAspectRatioViews.add(wrapperAspectRatio)
        }
        mCropAspectRatioViews.get(aspectRationSelectedByDefault).setSelected(true)
        for (cropAspectRatioView: ViewGroup in mCropAspectRatioViews) {
            cropAspectRatioView.setOnClickListener(object : View.OnClickListener {
                public override fun onClick(v: View) {
                    mGestureCropImageView!!.targetAspectRatio = ((v as ViewGroup).getChildAt(0) as AspectRatioTextView).getAspectRatio(v.isSelected())
                    mGestureCropImageView!!.setImageToWrapCropBounds()
                    if (!v.isSelected()) {
                        for (cropAspectRatioView: ViewGroup in mCropAspectRatioViews) {
                            cropAspectRatioView.setSelected(cropAspectRatioView === v)
                        }
                    }
                }
            })
        }
    }

    private fun setupRotateWidget(view: View) {
        mTextViewRotateAngle = view.findViewById(R.id.text_view_rotate)
        (view.findViewById<View>(R.id.rotate_scroll_wheel) as HorizontalProgressWheelView)
            .setScrollingListener(object : ScrollingListener {
                public override fun onScroll(delta: Float, totalDistance: Float) {
                    mGestureCropImageView!!.postRotate(delta / ROTATE_WIDGET_SENSITIVITY_COEFFICIENT)
                }

                public override fun onScrollEnd() {
                    mGestureCropImageView!!.setImageToWrapCropBounds()
                }

                public override fun onScrollStart() {
                    mGestureCropImageView!!.cancelAllAnimations()
                }
            })
        (view.findViewById<View>(R.id.rotate_scroll_wheel) as HorizontalProgressWheelView).setMiddleLineColor(
            mActiveControlsWidgetColor
        )
        view.findViewById<View>(R.id.wrapper_reset_rotate)
            .setOnClickListener(object : View.OnClickListener {
                public override fun onClick(v: View) {
                    resetRotation()
                }
            })
        view.findViewById<View>(R.id.wrapper_rotate_by_angle)
            .setOnClickListener(object : View.OnClickListener {
                public override fun onClick(v: View) {
                    rotateByAngle(90)
                }
            })
        setAngleTextColor(mActiveControlsWidgetColor)
    }

    private fun setupScaleWidget(view: View) {
        mTextViewScalePercent = view.findViewById(R.id.text_view_scale)
        (view.findViewById<View>(R.id.scale_scroll_wheel) as HorizontalProgressWheelView)
            .setScrollingListener(object : ScrollingListener {
                public override fun onScroll(delta: Float, totalDistance: Float) {
                    if (delta > 0) {
                        mGestureCropImageView!!.zoomInImage(
                            (mGestureCropImageView!!.currentScale
                                    + delta * ((mGestureCropImageView!!.maxScale - mGestureCropImageView!!.minScale) / SCALE_WIDGET_SENSITIVITY_COEFFICIENT))
                        )
                    } else {
                        mGestureCropImageView!!.zoomOutImage(
                            (mGestureCropImageView!!.currentScale
                                    + delta * ((mGestureCropImageView!!.maxScale - mGestureCropImageView!!.minScale) / SCALE_WIDGET_SENSITIVITY_COEFFICIENT))
                        )
                    }
                }

                public override fun onScrollEnd() {
                    mGestureCropImageView!!.setImageToWrapCropBounds()
                }

                public override fun onScrollStart() {
                    mGestureCropImageView!!.cancelAllAnimations()
                }
            })
        (view.findViewById<View>(R.id.scale_scroll_wheel) as HorizontalProgressWheelView).setMiddleLineColor(
            mActiveControlsWidgetColor
        )
        setScaleTextColor(mActiveControlsWidgetColor)
    }

    private fun setAngleText(angle: Float) {
        if (mTextViewRotateAngle != null) {
            mTextViewRotateAngle!!.setText(String.format(Locale.getDefault(), "%.1f°", angle))
        }
    }

    private fun setAngleTextColor(textColor: Int) {
        if (mTextViewRotateAngle != null) {
            mTextViewRotateAngle!!.setTextColor(textColor)
        }
    }

    private fun setScaleText(scale: Float) {
        if (mTextViewScalePercent != null) {
            mTextViewScalePercent!!.setText(
                String.format(
                    Locale.getDefault(),
                    "%d%%",
                    (scale * 100).toInt()
                )
            )
        }
    }

    private fun setScaleTextColor(textColor: Int) {
        if (mTextViewScalePercent != null) {
            mTextViewScalePercent!!.setTextColor(textColor)
        }
    }

    private fun resetRotation() {
        mGestureCropImageView!!.postRotate(-mGestureCropImageView!!.currentAngle)
        mGestureCropImageView!!.setImageToWrapCropBounds()
    }

    private fun rotateByAngle(angle: Int) {
        mGestureCropImageView!!.postRotate(angle.toFloat())
        mGestureCropImageView!!.setImageToWrapCropBounds()
    }

    private val mStateClickListener: View.OnClickListener = object : View.OnClickListener {
        public override fun onClick(v: View) {
            if (!v.isSelected()) {
                setWidgetState(v.getId())
            }
        }
    }

    private fun setInitialState() {
        if (mShowBottomControls) {
            if (mWrapperStateAspectRatio!!.getVisibility() == View.VISIBLE) {
                setWidgetState(R.id.state_aspect_ratio)
            } else {
                setWidgetState(R.id.state_scale)
            }
        } else {
            setAllowedGestures(0)
        }
    }

    private fun setWidgetState(@IdRes stateViewId: Int) {
        if (!mShowBottomControls) return
        mWrapperStateAspectRatio!!.setSelected(stateViewId == R.id.state_aspect_ratio)
        mWrapperStateRotate!!.setSelected(stateViewId == R.id.state_rotate)
        mWrapperStateScale!!.setSelected(stateViewId == R.id.state_scale)
        mLayoutAspectRatio!!.setVisibility(if (stateViewId == R.id.state_aspect_ratio) View.VISIBLE else View.GONE)
        mLayoutRotate!!.setVisibility(if (stateViewId == R.id.state_rotate) View.VISIBLE else View.GONE)
        mLayoutScale!!.setVisibility(if (stateViewId == R.id.state_scale) View.VISIBLE else View.GONE)
        changeSelectedTab(stateViewId)
        if (stateViewId == R.id.state_scale) {
            setAllowedGestures(0)
        } else if (stateViewId == R.id.state_rotate) {
            setAllowedGestures(1)
        } else {
            setAllowedGestures(2)
        }
    }

    private fun changeSelectedTab(stateViewId: Int) {
        if (getView() != null) {
            TransitionManager.beginDelayedTransition(
                (getView()!!.findViewById<View>(R.id.ucrop_photobox) as ViewGroup?)!!,
                mControlsTransition
            )
        }
        mWrapperStateScale!!.findViewById<View>(R.id.text_view_scale)
            .setVisibility(if (stateViewId == R.id.state_scale) View.VISIBLE else View.GONE)
        mWrapperStateAspectRatio!!.findViewById<View>(R.id.text_view_crop)
            .setVisibility(if (stateViewId == R.id.state_aspect_ratio) View.VISIBLE else View.GONE)
        mWrapperStateRotate!!.findViewById<View>(R.id.text_view_rotate)
            .setVisibility(if (stateViewId == R.id.state_rotate) View.VISIBLE else View.GONE)
    }

    private fun setAllowedGestures(tab: Int) {
        mGestureCropImageView!!.isScaleEnabled = mAllowedGestures.get(tab) == ALL || mAllowedGestures.get(
            tab
        ) == SCALE
        mGestureCropImageView!!.isRotateEnabled = mAllowedGestures.get(tab) == ALL || mAllowedGestures.get(
            tab
        ) == ROTATE
        mGestureCropImageView!!.isGestureEnabled = getArguments()!!.getBoolean(
            UCrop.Options.Companion.EXTRA_DRAG_IMAGES,
            true
        )
    }

    /**
     * Adds view that covers everything below the Toolbar.
     * When it's clickable - user won't be able to click/touch anything below the Toolbar.
     * Need to block user input while loading and cropping an image.
     */
    private fun addBlockingView(view: View) {
        if (mBlockingView == null) {
            mBlockingView = View(getContext())
            val lp: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            mBlockingView!!.setLayoutParams(lp)
            mBlockingView!!.setClickable(true)
        }
        (view.findViewById<View>(R.id.ucrop_photobox) as RelativeLayout).addView(mBlockingView)
    }

    fun cropAndSaveImage() {
        mBlockingView!!.setClickable(true)
        callback!!.loadingProgress(true)
        mGestureCropImageView!!.cropAndSaveImage(
            mCompressFormat,
            mCompressQuality,
            object : BitmapCropCallback {
                public override fun onBitmapCropped(
                    resultUri: Uri,
                    offsetX: Int,
                    offsetY: Int,
                    imageWidth: Int,
                    imageHeight: Int
                ) {
                    callback!!.onCropFinish(
                        getResult(
                            resultUri,
                            mGestureCropImageView!!.targetAspectRatio,
                            offsetX,
                            offsetY,
                            imageWidth,
                            imageHeight
                        )
                    )
                    callback!!.loadingProgress(false)
                }

                public override fun onCropFailure(t: Throwable) {
                    callback!!.onCropFinish(getError(t))
                }
            })
    }

    protected fun getResult(
        uri: Uri?,
        resultAspectRatio: Float,
        offsetX: Int,
        offsetY: Int,
        imageWidth: Int,
        imageHeight: Int
    ): UCropResult {
        val inputUri: Uri? = getArguments()!!.getParcelable(UCrop.Companion.EXTRA_INPUT_URI)
        return UCropResult(
            Activity.RESULT_OK, Intent()
                .putExtra(UCrop.Companion.EXTRA_OUTPUT_URI, uri)
                .putExtra(UCrop.Companion.EXTRA_OUTPUT_CROP_ASPECT_RATIO, resultAspectRatio)
                .putExtra(UCrop.Companion.EXTRA_OUTPUT_IMAGE_WIDTH, imageWidth)
                .putExtra(UCrop.Companion.EXTRA_OUTPUT_IMAGE_HEIGHT, imageHeight)
                .putExtra(UCrop.Companion.EXTRA_OUTPUT_OFFSET_X, offsetX)
                .putExtra(UCrop.Companion.EXTRA_OUTPUT_OFFSET_Y, offsetY)
                .putExtra(
                    UCrop.Companion.EXTRA_CROP_INPUT_ORIGINAL, getInputPath(
                        (inputUri)!!
                    )
                )
        )
    }

    protected fun getError(throwable: Throwable?): UCropResult {
        return UCropResult(
            UCrop.Companion.RESULT_ERROR, Intent()
                .putExtra(UCrop.Companion.EXTRA_ERROR, throwable)
        )
    }

    class UCropResult constructor(var mResultCode: Int, var mResultData: Intent)
}