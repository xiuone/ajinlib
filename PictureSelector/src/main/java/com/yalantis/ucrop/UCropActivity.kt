package com.yalantis.ucrop

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
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
import androidx.appcompat.widget.Toolbar
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
import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 */
class UCropActivity() : AppCompatActivity() {
    @IntDef(NONE, SCALE, ROTATE, ALL)
    @Retention(RetentionPolicy.SOURCE)
    annotation class GestureTypes()

    private var mToolbarTitle: String? = null
    private var mToolbarTitleSize = 0
    private var isUseCustomBitmap = false

    // Enables dynamic coloring
    private var mToolbarColor = 0
    private var mStatusBarColor = 0
    private var mActiveControlsWidgetColor = 0
    private var mToolbarWidgetColor = 0

    @ColorInt
    private var mRootViewBackgroundColor = 0

    @DrawableRes
    private var mToolbarCancelDrawable = 0

    @DrawableRes
    private var mToolbarCropDrawable = 0
    private var mLogoColor = 0
    private var mShowBottomControls = false
    private var mShowLoader = true
    private var isForbidCropGifWebp = false
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
    private var mControlsTransition: Transition? = null
    private var mCompressFormat = DEFAULT_COMPRESS_FORMAT
    private var mCompressQuality = DEFAULT_COMPRESS_QUALITY
    private var mAllowedGestures = intArrayOf(SCALE, ROTATE, ALL)

    companion object {
        val DEFAULT_COMPRESS_QUALITY = 90
        val DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG
        val NONE = 0
        val SCALE = 1
        val ROTATE = 2
        val ALL = 3
        private val TAG = "UCropActivity"
        private val CONTROLS_ANIMATION_DURATION: Long = 50
        private val TABS_COUNT = 3
        private val SCALE_WIDGET_SENSITIVITY_COEFFICIENT = 15000
        private val ROTATE_WIDGET_SENSITIVITY_COEFFICIENT = 42

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersive()
        setContentView(R.layout.ucrop_activity_photobox)
        val intent = intent
        setupViews(intent)
        setImageData(intent)
        setInitialState()
        addBlockingView()
    }

    private fun immersive() {
        val intent = intent
        val isDarkStatusBarBlack =
            intent.getBooleanExtra(UCrop.Options.Companion.EXTRA_DARK_STATUS_BAR_BLACK, false)
        mStatusBarColor = intent.getIntExtra(
            UCrop.Options.Companion.EXTRA_STATUS_BAR_COLOR,
            ContextCompat.getColor(this, R.color.ucrop_color_statusbar)
        )
        immersiveAboveAPI23(this, mStatusBarColor, mStatusBarColor, isDarkStatusBarBlack)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.ucrop_menu_activity, menu)

        // Change crop & loader menu icons color to match the rest of the UI colors
        val menuItemLoader = menu.findItem(R.id.menu_loader)
        val menuItemLoaderIcon = menuItemLoader.icon
        if (menuItemLoaderIcon != null) {
            try {
                menuItemLoaderIcon.mutate()
                val colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    mToolbarWidgetColor,
                    BlendModeCompat.SRC_ATOP
                )
                menuItemLoaderIcon.colorFilter = colorFilter
                menuItemLoader.icon = menuItemLoaderIcon
            } catch (e: IllegalStateException) {
                Log.i(
                    TAG,
                    String.format(
                        "%s - %s",
                        e.message,
                        getString(R.string.ucrop_mutate_exception_hint)
                    )
                )
            }
            (menuItemLoader.icon as Animatable?)!!.start()
        }
        val menuItemCrop = menu.findItem(R.id.menu_crop)
        val menuItemCropIcon = ContextCompat.getDrawable(this, mToolbarCropDrawable)
        if (menuItemCropIcon != null) {
            menuItemCropIcon.mutate()
            val colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                mToolbarWidgetColor,
                BlendModeCompat.SRC_ATOP
            )
            menuItemCropIcon.colorFilter = colorFilter
            menuItemCrop.icon = menuItemCropIcon
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.menu_crop).isVisible = !mShowLoader
        menu.findItem(R.id.menu_loader).isVisible = mShowLoader
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_crop) {
            cropAndSaveImage()
            return true
        } else if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        if (mGestureCropImageView != null) {
            mGestureCropImageView!!.cancelAllAnimations()
        }
    }

    /**
     * This method extracts all data from the incoming intent and setups views properly.
     */
    private fun setImageData(intent: Intent) {
        val inputUri = intent.getParcelableExtra<Uri>(UCrop.Companion.EXTRA_INPUT_URI)
        var outputUri = intent.getParcelableExtra<Uri>(UCrop.Companion.EXTRA_OUTPUT_URI)
        processOptions(intent)
        if (inputUri != null && outputUri != null) {
            try {
                outputUri =
                    replaceOutputUri(this@UCropActivity, isForbidCropGifWebp, inputUri, outputUri)
                mGestureCropImageView!!.setImageUri(inputUri, outputUri, isUseCustomBitmap)
            } catch (e: Exception) {
                setResultError(e)
                finish()
            }
        } else {
            setResultError(NullPointerException(getString(R.string.ucrop_error_input_data_is_absent)))
            finish()
        }
    }

    /**
     * This method extracts [#optionsBundle][UCrop.Options] from incoming intent
     * and setups Activity, [OverlayView] and [CropImageView] properly.
     */
    private fun processOptions(intent: Intent) {
        // Bitmap compression options
        val compressionFormatName =
            intent.getStringExtra(UCrop.Options.Companion.EXTRA_COMPRESSION_FORMAT_NAME)
        var compressFormat: Bitmap.CompressFormat? = null
        if (!TextUtils.isEmpty(compressionFormatName)) {
            compressFormat = Bitmap.CompressFormat.valueOf((compressionFormatName)!!)
        }
        mCompressFormat = compressFormat ?: DEFAULT_COMPRESS_FORMAT
        mCompressQuality = intent.getIntExtra(
            UCrop.Options.Companion.EXTRA_COMPRESSION_QUALITY,
            DEFAULT_COMPRESS_QUALITY
        )

        // Gestures options
        val allowedGestures =
            intent.getIntArrayExtra(UCrop.Options.Companion.EXTRA_ALLOWED_GESTURES)
        if (allowedGestures != null && allowedGestures.size == TABS_COUNT) {
            mAllowedGestures = allowedGestures
        }
        isUseCustomBitmap =
            intent.getBooleanExtra(UCrop.Options.Companion.EXTRA_CROP_CUSTOM_LOADER_BITMAP, false)

        // Crop image view options
        mGestureCropImageView!!.maxBitmapSize = intent.getIntExtra(
            UCrop.Options.Companion.EXTRA_MAX_BITMAP_SIZE,
            CropImageView.DEFAULT_MAX_BITMAP_SIZE
        )
        mGestureCropImageView!!.setMaxScaleMultiplier(
            intent.getFloatExtra(
                UCrop.Options.Companion.EXTRA_MAX_SCALE_MULTIPLIER,
                CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER
            )
        )
        mGestureCropImageView!!.setImageToWrapCropBoundsAnimDuration(
            intent.getIntExtra(
                UCrop.Options.Companion.EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION,
                CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION
            ).toLong()
        )

        // Overlay view options
        mOverlayView!!.isFreestyleCropEnabled = intent.getBooleanExtra(
            UCrop.Options.Companion.EXTRA_FREE_STYLE_CROP,
            OverlayView.DEFAULT_FREESTYLE_CROP_MODE != OverlayView.FREESTYLE_CROP_MODE_DISABLE
        )
        mOverlayView!!.setDragSmoothToCenter(
            intent.getBooleanExtra(
                UCrop.Options.Companion.EXTRA_CROP_DRAG_CENTER,
                false
            )
        )
        mOverlayView!!.setDimmedColor(
            intent.getIntExtra(
                UCrop.Options.Companion.EXTRA_DIMMED_LAYER_COLOR, resources.getColor(
                    R.color.ucrop_color_default_dimmed
                )
            )
        )
        mOverlayView!!.setCircleStrokeColor(
            intent.getIntExtra(
                UCrop.Options.Companion.EXTRA_CIRCLE_STROKE_COLOR, resources.getColor(
                    R.color.ucrop_color_default_dimmed
                )
            )
        )
        mOverlayView!!.setCircleDimmedLayer(
            intent.getBooleanExtra(
                UCrop.Options.Companion.EXTRA_CIRCLE_DIMMED_LAYER,
                OverlayView.DEFAULT_CIRCLE_DIMMED_LAYER
            )
        )
        mOverlayView!!.setShowCropFrame(
            intent.getBooleanExtra(
                UCrop.Options.Companion.EXTRA_SHOW_CROP_FRAME,
                OverlayView.DEFAULT_SHOW_CROP_FRAME
            )
        )
        mOverlayView!!.setCropFrameColor(
            intent.getIntExtra(
                UCrop.Options.Companion.EXTRA_CROP_FRAME_COLOR, resources.getColor(
                    R.color.ucrop_color_default_crop_frame
                )
            )
        )
        mOverlayView!!.setCropFrameStrokeWidth(
            intent.getIntExtra(
                UCrop.Options.Companion.EXTRA_CROP_FRAME_STROKE_WIDTH,
                resources.getDimensionPixelSize(
                    R.dimen.ucrop_default_crop_frame_stoke_width
                )
            )
        )
        mOverlayView!!.setShowCropGrid(
            intent.getBooleanExtra(
                UCrop.Options.Companion.EXTRA_SHOW_CROP_GRID,
                OverlayView.DEFAULT_SHOW_CROP_GRID
            )
        )
        mOverlayView!!.setCropGridRowCount(
            intent.getIntExtra(
                UCrop.Options.Companion.EXTRA_CROP_GRID_ROW_COUNT,
                OverlayView.DEFAULT_CROP_GRID_ROW_COUNT
            )
        )
        mOverlayView!!.setCropGridColumnCount(
            intent.getIntExtra(
                UCrop.Options.Companion.EXTRA_CROP_GRID_COLUMN_COUNT,
                OverlayView.DEFAULT_CROP_GRID_COLUMN_COUNT
            )
        )
        mOverlayView!!.setCropGridColor(
            intent.getIntExtra(
                UCrop.Options.Companion.EXTRA_CROP_GRID_COLOR, resources.getColor(
                    R.color.ucrop_color_default_crop_grid
                )
            )
        )
        mOverlayView!!.setCropGridStrokeWidth(
            intent.getIntExtra(
                UCrop.Options.Companion.EXTRA_CROP_GRID_STROKE_WIDTH,
                resources.getDimensionPixelSize(
                    R.dimen.ucrop_default_crop_grid_stoke_width
                )
            )
        )
        mOverlayView!!.setDimmedStrokeWidth(
            intent.getIntExtra(
                UCrop.Options.Companion.EXTRA_CIRCLE_STROKE_WIDTH_LAYER,
                resources.getDimensionPixelSize(
                    R.dimen.ucrop_default_crop_grid_stoke_width
                )
            )
        )
        // Aspect ratio options
        val aspectRatioX = intent.getFloatExtra(UCrop.Companion.EXTRA_ASPECT_RATIO_X, -1f)
        val aspectRatioY = intent.getFloatExtra(UCrop.Companion.EXTRA_ASPECT_RATIO_Y, -1f)
        val aspectRationSelectedByDefault =
            intent.getIntExtra(UCrop.Options.Companion.EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, 0)
        val aspectRatioList =
            intent.getParcelableArrayListExtra<AspectRatio>(UCrop.Options.Companion.EXTRA_ASPECT_RATIO_OPTIONS)
        if (aspectRatioX >= 0 && aspectRatioY >= 0) {
            if (mWrapperStateAspectRatio != null) {
                mWrapperStateAspectRatio!!.visibility = View.GONE
            }
            val targetAspectRatio = aspectRatioX / aspectRatioY
            mGestureCropImageView!!.targetAspectRatio =
                if (java.lang.Float.isNaN(targetAspectRatio)) CropImageView.SOURCE_IMAGE_ASPECT_RATIO else targetAspectRatio
        } else if (aspectRatioList != null && aspectRationSelectedByDefault < aspectRatioList.size) {
            val targetAspectRatio =
                aspectRatioList[aspectRationSelectedByDefault].aspectRatioX / aspectRatioList[aspectRationSelectedByDefault].aspectRatioY
            mGestureCropImageView!!.targetAspectRatio =
                if (java.lang.Float.isNaN(targetAspectRatio)) CropImageView.SOURCE_IMAGE_ASPECT_RATIO else targetAspectRatio
        } else {
            mGestureCropImageView!!.targetAspectRatio = CropImageView.SOURCE_IMAGE_ASPECT_RATIO
        }

        // Result bitmap max size options
        val maxSizeX = intent.getIntExtra(UCrop.Companion.EXTRA_MAX_SIZE_X, 0)
        val maxSizeY = intent.getIntExtra(UCrop.Companion.EXTRA_MAX_SIZE_Y, 0)
        if (maxSizeX > 0 && maxSizeY > 0) {
            mGestureCropImageView!!.setMaxResultImageSizeX(maxSizeX)
            mGestureCropImageView!!.setMaxResultImageSizeY(maxSizeY)
        }
    }

    private fun setupViews(intent: Intent) {
        isForbidCropGifWebp =
            intent.getBooleanExtra(UCrop.Options.Companion.EXTRA_CROP_FORBID_GIF_WEBP, false)
        mStatusBarColor = intent.getIntExtra(
            UCrop.Options.Companion.EXTRA_STATUS_BAR_COLOR,
            ContextCompat.getColor(this, R.color.ucrop_color_statusbar)
        )
        mToolbarColor = intent.getIntExtra(
            UCrop.Options.Companion.EXTRA_TOOL_BAR_COLOR,
            ContextCompat.getColor(this, R.color.ucrop_color_toolbar)
        )
        mActiveControlsWidgetColor = intent.getIntExtra(
            UCrop.Options.Companion.EXTRA_UCROP_COLOR_CONTROLS_WIDGET_ACTIVE,
            ContextCompat.getColor(this, R.color.ucrop_color_active_controls_color)
        )
        mToolbarWidgetColor = intent.getIntExtra(
            UCrop.Options.Companion.EXTRA_UCROP_WIDGET_COLOR_TOOLBAR,
            ContextCompat.getColor(this, R.color.ucrop_color_toolbar_widget)
        )
        mToolbarCancelDrawable = intent.getIntExtra(
            UCrop.Options.Companion.EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE,
            R.drawable.ucrop_ic_cross
        )
        mToolbarCropDrawable = intent.getIntExtra(
            UCrop.Options.Companion.EXTRA_UCROP_WIDGET_CROP_DRAWABLE,
            R.drawable.ucrop_ic_done
        )
        mToolbarTitle =
            intent.getStringExtra(UCrop.Options.Companion.EXTRA_UCROP_TITLE_TEXT_TOOLBAR)
        mToolbarTitleSize =
            intent.getIntExtra(UCrop.Options.Companion.EXTRA_UCROP_TITLE_TEXT_SIZE_TOOLBAR, 18)
        mToolbarTitle =
            if (mToolbarTitle != null) mToolbarTitle else resources.getString(R.string.ucrop_label_edit_photo)
        mLogoColor = intent.getIntExtra(
            UCrop.Options.Companion.EXTRA_UCROP_LOGO_COLOR,
            ContextCompat.getColor(this, R.color.ucrop_color_default_logo)
        )
        mShowBottomControls =
            !intent.getBooleanExtra(UCrop.Options.Companion.EXTRA_HIDE_BOTTOM_CONTROLS, false)
        mRootViewBackgroundColor = intent.getIntExtra(
            UCrop.Options.Companion.EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR,
            ContextCompat.getColor(this, R.color.ucrop_color_crop_background)
        )
        setupAppBar()
        initiateRootViews()
        if (mShowBottomControls) {
            val viewGroup = findViewById<ViewGroup>(R.id.ucrop_photobox)
            val wrapper = viewGroup.findViewById<ViewGroup>(R.id.controls_wrapper)
            wrapper.visibility = View.VISIBLE
            LayoutInflater.from(this).inflate(R.layout.ucrop_controls, wrapper, true)
            mControlsTransition = AutoTransition()
            mControlsTransition.setDuration(CONTROLS_ANIMATION_DURATION)
            mWrapperStateAspectRatio = findViewById(R.id.state_aspect_ratio)
            mWrapperStateAspectRatio.setOnClickListener(mStateClickListener)
            mWrapperStateRotate = findViewById(R.id.state_rotate)
            mWrapperStateRotate.setOnClickListener(mStateClickListener)
            mWrapperStateScale = findViewById(R.id.state_scale)
            mWrapperStateScale.setOnClickListener(mStateClickListener)
            mLayoutAspectRatio = findViewById(R.id.layout_aspect_ratio)
            mLayoutRotate = findViewById(R.id.layout_rotate_wheel)
            mLayoutScale = findViewById(R.id.layout_scale_wheel)
            setupAspectRatioWidget(intent)
            setupRotateWidget()
            setupScaleWidget()
            setupStatesWrapper()
        }
    }

    /**
     * Configures and styles both status bar and toolbar.
     */
    private fun setupAppBar() {
        setStatusBarColor(mStatusBarColor)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        // Set all of the Toolbar coloring
        toolbar.setBackgroundColor(mToolbarColor)
        toolbar.setTitleTextColor(mToolbarWidgetColor)
        val toolbarTitle = toolbar.findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.setTextColor(mToolbarWidgetColor)
        toolbarTitle.text = mToolbarTitle
        toolbarTitle.textSize = mToolbarTitleSize.toFloat()

        // Color buttons inside the Toolbar
        val stateButtonDrawable = AppCompatResources.getDrawable(this, mToolbarCancelDrawable)!!
            .mutate()
        val colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            mToolbarWidgetColor,
            BlendModeCompat.SRC_ATOP
        )
        stateButtonDrawable.colorFilter = colorFilter
        toolbar.navigationIcon = stateButtonDrawable
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initiateRootViews() {
        mUCropView = findViewById(R.id.ucrop)
        mGestureCropImageView = mUCropView.cropImageView
        mOverlayView = mUCropView.overlayView
        mGestureCropImageView!!.setTransformImageListener(mImageListener)
        (findViewById<View>(R.id.image_view_logo) as ImageView).setColorFilter(
            mLogoColor,
            PorterDuff.Mode.SRC_ATOP
        )
        findViewById<View>(R.id.ucrop_frame).setBackgroundColor(mRootViewBackgroundColor)
        if (!mShowBottomControls) {
            val params =
                findViewById<View>(R.id.ucrop_frame).layoutParams as RelativeLayout.LayoutParams
            params.bottomMargin = 0
            findViewById<View>(R.id.ucrop_frame).requestLayout()
        }
    }

    private val mImageListener: TransformImageListener = object : TransformImageListener {
        override fun onRotate(currentAngle: Float) {
            setAngleText(currentAngle)
        }

        override fun onScale(currentScale: Float) {
            setScaleText(currentScale)
        }

        override fun onLoadComplete() {
            mUCropView!!.animate().alpha(1f).setDuration(300).interpolator =
                AccelerateInterpolator()
            mBlockingView!!.isClickable = false
            if (intent.getBooleanExtra(UCrop.Options.Companion.EXTRA_CROP_FORBID_GIF_WEBP, false)) {
                val inputUri = intent.getParcelableExtra<Uri>(UCrop.Companion.EXTRA_INPUT_URI)
                val mimeType = getMimeTypeFromMediaContentUri(this@UCropActivity, (inputUri)!!)
                if (isGif(mimeType) || isWebp(mimeType)) {
                    mBlockingView!!.isClickable = true
                }
            }
            mShowLoader = false
            supportInvalidateOptionsMenu()
        }

        override fun onLoadFailure(e: Exception) {
            setResultError(e)
            finish()
        }
    }

    /**
     * Use [.mActiveControlsWidgetColor] for color filter
     */
    private fun setupStatesWrapper() {
        val stateScaleImageView = findViewById<ImageView>(R.id.image_view_state_scale)
        val stateRotateImageView = findViewById<ImageView>(R.id.image_view_state_rotate)
        val stateAspectRatioImageView = findViewById<ImageView>(R.id.image_view_state_aspect_ratio)
        stateScaleImageView.setImageDrawable(
            SelectedStateListDrawable(
                stateScaleImageView.drawable,
                mActiveControlsWidgetColor
            )
        )
        stateRotateImageView.setImageDrawable(
            SelectedStateListDrawable(
                stateRotateImageView.drawable,
                mActiveControlsWidgetColor
            )
        )
        stateAspectRatioImageView.setImageDrawable(
            SelectedStateListDrawable(
                stateAspectRatioImageView.drawable,
                mActiveControlsWidgetColor
            )
        )
    }

    /**
     * Sets status-bar color for L devices.
     *
     * @param color - status-bar color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setStatusBarColor(@ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = color
            }
        }
    }

    private fun setupAspectRatioWidget(intent: Intent) {
        var aspectRationSelectedByDefault =
            intent.getIntExtra(UCrop.Options.Companion.EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, 0)
        var aspectRatioList =
            intent.getParcelableArrayListExtra<AspectRatio?>(UCrop.Options.Companion.EXTRA_ASPECT_RATIO_OPTIONS)
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
        val wrapperAspectRatioList = findViewById<LinearLayout>(R.id.layout_aspect_ratio)
        var wrapperAspectRatio: FrameLayout
        var aspectRatioTextView: AspectRatioTextView
        val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
        lp.weight = 1f
        for (aspectRatio: AspectRatio? in aspectRatioList) {
            wrapperAspectRatio =
                layoutInflater.inflate(R.layout.ucrop_aspect_ratio, null) as FrameLayout
            wrapperAspectRatio.layoutParams = lp
            aspectRatioTextView = (wrapperAspectRatio.getChildAt(0) as AspectRatioTextView)
            aspectRatioTextView.setActiveColor(mActiveControlsWidgetColor)
            aspectRatioTextView.setAspectRatio((aspectRatio)!!)
            wrapperAspectRatioList.addView(wrapperAspectRatio)
            mCropAspectRatioViews.add(wrapperAspectRatio)
        }
        mCropAspectRatioViews.get(aspectRationSelectedByDefault).isSelected = true
        for (cropAspectRatioView: ViewGroup in mCropAspectRatioViews) {
            cropAspectRatioView.setOnClickListener { v ->
                mGestureCropImageView!!.targetAspectRatio =
                    ((v as ViewGroup).getChildAt(0) as AspectRatioTextView).getAspectRatio(v.isSelected())
                mGestureCropImageView!!.setImageToWrapCropBounds()
                if (!v.isSelected()) {
                    for (cropAspectRatioView: ViewGroup in mCropAspectRatioViews) {
                        cropAspectRatioView.isSelected = cropAspectRatioView === v
                    }
                }
            }
        }
    }

    private fun setupRotateWidget() {
        mTextViewRotateAngle = findViewById(R.id.text_view_rotate)
        (findViewById<View>(R.id.rotate_scroll_wheel) as HorizontalProgressWheelView)
            .setScrollingListener(object : ScrollingListener {
                override fun onScroll(delta: Float, totalDistance: Float) {
                    mGestureCropImageView!!.postRotate(delta / ROTATE_WIDGET_SENSITIVITY_COEFFICIENT)
                }

                override fun onScrollEnd() {
                    mGestureCropImageView!!.setImageToWrapCropBounds()
                }

                override fun onScrollStart() {
                    mGestureCropImageView!!.cancelAllAnimations()
                }
            })
        (findViewById<View>(R.id.rotate_scroll_wheel) as HorizontalProgressWheelView).setMiddleLineColor(
            mActiveControlsWidgetColor
        )
        findViewById<View>(R.id.wrapper_reset_rotate).setOnClickListener(
            View.OnClickListener { resetRotation() })
        findViewById<View>(R.id.wrapper_rotate_by_angle).setOnClickListener(object :
            View.OnClickListener {
            override fun onClick(v: View) {
                rotateByAngle(90)
            }
        })
        setAngleTextColor(mActiveControlsWidgetColor)
    }

    private fun setupScaleWidget() {
        mTextViewScalePercent = findViewById(R.id.text_view_scale)
        (findViewById<View>(R.id.scale_scroll_wheel) as HorizontalProgressWheelView)
            .setScrollingListener(object : ScrollingListener {
                override fun onScroll(delta: Float, totalDistance: Float) {
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

                override fun onScrollEnd() {
                    mGestureCropImageView!!.setImageToWrapCropBounds()
                }

                override fun onScrollStart() {
                    mGestureCropImageView!!.cancelAllAnimations()
                }
            })
        (findViewById<View>(R.id.scale_scroll_wheel) as HorizontalProgressWheelView).setMiddleLineColor(
            mActiveControlsWidgetColor
        )
        setScaleTextColor(mActiveControlsWidgetColor)
    }

    private fun setAngleText(angle: Float) {
        if (mTextViewRotateAngle != null) {
            mTextViewRotateAngle!!.text =
                String.format(Locale.getDefault(), "%.1f°", angle)
        }
    }

    private fun setAngleTextColor(textColor: Int) {
        if (mTextViewRotateAngle != null) {
            mTextViewRotateAngle!!.setTextColor(textColor)
        }
    }

    private fun setScaleText(scale: Float) {
        if (mTextViewScalePercent != null) {
            mTextViewScalePercent!!.text =
                String.format(Locale.getDefault(), "%d%%", (scale * 100).toInt())
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
        override fun onClick(v: View) {
            if (!v.isSelected) {
                setWidgetState(v.id)
            }
        }
    }

    private fun setInitialState() {
        if (mShowBottomControls) {
            if (mWrapperStateAspectRatio!!.visibility == View.VISIBLE) {
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
        mWrapperStateAspectRatio!!.isSelected = stateViewId == R.id.state_aspect_ratio
        mWrapperStateRotate!!.isSelected = stateViewId == R.id.state_rotate
        mWrapperStateScale!!.isSelected = stateViewId == R.id.state_scale
        mLayoutAspectRatio!!.visibility =
            if (stateViewId == R.id.state_aspect_ratio) View.VISIBLE else View.GONE
        mLayoutRotate!!.visibility =
            if (stateViewId == R.id.state_rotate) View.VISIBLE else View.GONE
        mLayoutScale!!.visibility =
            if (stateViewId == R.id.state_scale) View.VISIBLE else View.GONE
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
        TransitionManager.beginDelayedTransition(
            (findViewById<View>(R.id.ucrop_photobox) as ViewGroup),
            mControlsTransition
        )
        mWrapperStateScale!!.findViewById<View>(R.id.text_view_scale).visibility =
            if (stateViewId == R.id.state_scale) View.VISIBLE else View.GONE
        mWrapperStateAspectRatio!!.findViewById<View>(R.id.text_view_crop).visibility =
            if (stateViewId == R.id.state_aspect_ratio) View.VISIBLE else View.GONE
        mWrapperStateRotate!!.findViewById<View>(R.id.text_view_rotate).visibility =
            if (stateViewId == R.id.state_rotate) View.VISIBLE else View.GONE
    }

    private fun setAllowedGestures(tab: Int) {
        mGestureCropImageView!!.isScaleEnabled =
            mAllowedGestures.get(tab) == ALL || mAllowedGestures.get(tab) == SCALE
        mGestureCropImageView!!.isRotateEnabled =
            mAllowedGestures.get(tab) == ALL || mAllowedGestures.get(tab) == ROTATE
        mGestureCropImageView!!.isGestureEnabled =
            intent.getBooleanExtra(UCrop.Options.Companion.EXTRA_DRAG_IMAGES, true)
    }

    /**
     * Adds view that covers everything below the Toolbar.
     * When it's clickable - user won't be able to click/touch anything below the Toolbar.
     * Need to block user input while loading and cropping an image.
     */
    private fun addBlockingView() {
        if (mBlockingView == null) {
            mBlockingView = View(this)
            val lp = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            lp.addRule(RelativeLayout.BELOW, R.id.toolbar)
            mBlockingView!!.layoutParams = lp
            mBlockingView!!.isClickable = true
        }
        (findViewById<View>(R.id.ucrop_photobox) as RelativeLayout).addView(mBlockingView)
    }

    protected fun cropAndSaveImage() {
        mBlockingView!!.isClickable = true
        mShowLoader = true
        supportInvalidateOptionsMenu()
        mGestureCropImageView!!.cropAndSaveImage(
            mCompressFormat,
            mCompressQuality,
            object : BitmapCropCallback {
                override fun onBitmapCropped(
                    resultUri: Uri,
                    offsetX: Int,
                    offsetY: Int,
                    imageWidth: Int,
                    imageHeight: Int
                ) {
                    setResultUri(
                        resultUri,
                        mGestureCropImageView!!.targetAspectRatio,
                        offsetX,
                        offsetY,
                        imageWidth,
                        imageHeight
                    )
                    finish()
                }

                override fun onCropFailure(t: Throwable) {
                    setResultError(t)
                    finish()
                }
            })
    }

    protected fun setResultUri(
        uri: Uri?,
        resultAspectRatio: Float,
        offsetX: Int,
        offsetY: Int,
        imageWidth: Int,
        imageHeight: Int
    ) {
        val inputUri = intent.getParcelableExtra<Uri>(UCrop.Companion.EXTRA_INPUT_URI)
        setResult(
            RESULT_OK, Intent()
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

    protected fun setResultError(throwable: Throwable?) {
        setResult(
            UCrop.Companion.RESULT_ERROR,
            Intent().putExtra(UCrop.Companion.EXTRA_ERROR, throwable)
        )
    }

    override fun onDestroy() {
        UCropDevelopConfig.destroy()
        super.onDestroy()
    }
}