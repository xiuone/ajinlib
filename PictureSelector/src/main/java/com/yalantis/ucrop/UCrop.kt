package com.yalantis.ucrop

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import kotlin.jvm.JvmOverloads
import androidx.annotation.FloatRange
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.fragment.app.Fragment
import com.luck.picture.lib.BuildConfig
import com.yalantis.ucrop.model.AspectRatio
import java.lang.NullPointerException
import java.util.*

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 *
 *
 * Builder class to ease Intent setup.
 */
class UCrop {
    private var mCropIntent: Intent
    private var mCropOptionsBundle: Bundle

    /**
     * This method creates new Intent builder and sets both source and destination image URIs.
     *
     * @param source      Uri for image to crop
     * @param destination Uri for saving the cropped image
     */
    private constructor(source: Uri, destination: Uri) {
        mCropIntent = Intent()
        mCropOptionsBundle = Bundle()
        mCropOptionsBundle.putParcelable(EXTRA_INPUT_URI, source)
        mCropOptionsBundle.putParcelable(EXTRA_OUTPUT_URI, destination)
    }

    /**
     * This method creates new Intent builder and sets both source and destination image URIs.
     *
     * @param source      Uri for image to crop
     * @param destination Uri for saving the cropped image
     * @param totalSource crop total data
     */
    private constructor(source: Uri, destination: Uri, totalSource: ArrayList<String>) {
        mCropIntent = Intent()
        mCropOptionsBundle = Bundle()
        mCropOptionsBundle.putParcelable(EXTRA_INPUT_URI, source)
        mCropOptionsBundle.putParcelable(EXTRA_OUTPUT_URI, destination)
        mCropOptionsBundle.putStringArrayList(EXTRA_CROP_TOTAL_DATA_SOURCE, totalSource)
    }

    /**
     * Set Multiple Crop gallery Preview Image Engine
     *
     * @param engine
     * @return
     */
    fun setImageEngine(engine: UCropImageEngine?) {
        val dataSource = mCropOptionsBundle.getStringArrayList(EXTRA_CROP_TOTAL_DATA_SOURCE)
        val isUseBitmap =
            mCropOptionsBundle.getBoolean(Options.EXTRA_CROP_CUSTOM_LOADER_BITMAP, false)
        if (dataSource != null && dataSource.size > 1 || isUseBitmap) {
            if (engine == null) {
                throw NullPointerException("Missing ImageEngine,please implement UCrop.setImageEngine")
            }
        }
        UCropDevelopConfig.imageEngine = engine
    }

    /**
     * Set an aspect ratio for crop bounds.
     * User won't see the menu with other ratios options.
     *
     * @param x aspect ratio X
     * @param y aspect ratio Y
     */
    fun withAspectRatio(x: Float, y: Float): UCrop {
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_X, x)
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_Y, y)
        return this
    }

    /**
     * Set an aspect ratio for crop bounds that is evaluated from source image width and height.
     * User won't see the menu with other ratios options.
     */
    fun useSourceImageAspectRatio(): UCrop {
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_X, 0f)
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_Y, 0f)
        return this
    }

    /**
     * Set maximum size for result cropped image. Maximum size cannot be less then {@value MIN_SIZE}
     *
     * @param width  max cropped image width
     * @param height max cropped image height
     */
    fun withMaxResultSize(
        @IntRange(from = MIN_SIZE.toLong()) width: Int,
        @IntRange(from = MIN_SIZE.toLong()) height: Int
    ): UCrop {
        var width = width
        var height = height
        if (width < MIN_SIZE) {
            width = MIN_SIZE
        }
        if (height < MIN_SIZE) {
            height = MIN_SIZE
        }
        mCropOptionsBundle.putInt(EXTRA_MAX_SIZE_X, width)
        mCropOptionsBundle.putInt(EXTRA_MAX_SIZE_Y, height)
        return this
    }

    fun withOptions(options: Options): UCrop {
        mCropOptionsBundle.putAll(options.optionBundle)
        return this
    }
    /**
     * Send the crop Intent from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    /**
     * Send the crop Intent from an Activity
     *
     * @param activity Activity to receive result
     */
    @JvmOverloads
    fun start(activity: Activity, requestCode: Int = REQUEST_CROP) {
        activity.startActivityForResult(getIntent(activity), requestCode)
    }
    /**
     * Send the crop Intent with a custom request code
     *
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    /**
     * Send the crop Intent from a Fragment
     *
     * @param fragment Fragment to receive result
     */
    @JvmOverloads
    fun start(context: Context, fragment: Fragment, requestCode: Int = REQUEST_CROP) {
        fragment.startActivityForResult(getIntent(context), requestCode)
    }

    /**
     * Send the crop Intent with a custom request code
     *
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    fun startEdit(context: Context, fragment: Fragment, requestCode: Int) {
        fragment.startActivityForResult(getIntent(context), requestCode)
    }

    /**
     * Get Intent to start [UCropActivity]
     *
     * @return Intent for [UCropActivity]
     */
    fun getIntent(context: Context): Intent {
        val dataSource = mCropOptionsBundle.getStringArrayList(EXTRA_CROP_TOTAL_DATA_SOURCE)
        if (dataSource != null && dataSource.size > 1) {
            mCropIntent.setClass(context, UCropMultipleActivity::class.java)
        } else {
            mCropIntent.setClass(context, UCropActivity::class.java)
        }
        mCropIntent.putExtras(mCropOptionsBundle)
        return mCropIntent
    }

    /**
     * Get Fragment [UCropFragment]
     *
     * @return Fragment of [UCropFragment]
     */
    val fragment: UCropFragment
        get() = UCropFragment.Companion.newInstance(mCropOptionsBundle)

    fun getFragment(bundle: Bundle): UCropFragment {
        mCropOptionsBundle = bundle
        return fragment
    }

    /**
     * Class that helps to setup advanced configs that are not commonly used.
     * Use it with method [.withOptions]
     */
    class Options {
        val optionBundle: Bundle

        /**
         * Set one of [Bitmap.CompressFormat] that will be used to save resulting Bitmap.
         */
        fun setCompressionFormat(format: Bitmap.CompressFormat) {
            optionBundle.putString(EXTRA_COMPRESSION_FORMAT_NAME, format.name)
        }

        /**
         * when clipping multiple drawings
         * Valid when multiple pictures are cropped
         */
        fun setCropOutputPathDir(dir: String) {
            optionBundle.putString(EXTRA_CROP_OUTPUT_DIR, dir)
        }

        /**
         * File name after clipping output
         * Valid when multiple pictures are cropped
         *
         *
         * When multiple pictures are cropped, the front will automatically keep up with the timestamp
         *
         */
        fun setCropOutputFileName(fileName: String) {
            optionBundle.putString(EXTRA_CROP_OUTPUT_FILE_NAME, fileName)
        }

        /**
         * @param isForbidSkipCrop - It is forbidden to skip when cutting multiple drawings
         */
        fun isForbidSkipMultipleCrop(isForbidSkipCrop: Boolean) {
            optionBundle.putBoolean(EXTRA_CROP_FORBID_SKIP, isForbidSkipCrop)
        }

        /**
         * Get the bitmap of the uCrop resource using the custom loader
         *
         * @param isUseBitmap
         */
        fun isUseCustomLoaderBitmap(isUseBitmap: Boolean) {
            optionBundle.putBoolean(EXTRA_CROP_CUSTOM_LOADER_BITMAP, isUseBitmap)
        }

        /**
         * isDragCenter
         *
         * @param isDragCenter Crop and drag automatically center
         */
        fun isCropDragSmoothToCenter(isDragCenter: Boolean) {
            optionBundle.putBoolean(EXTRA_CROP_DRAG_CENTER, isDragCenter)
        }

        /**
         * @param isForbidCropGifWebp - Do you need to support clipping dynamic graphs gif or webp
         */
        fun isForbidCropGifWebp(isForbidCropGifWebp: Boolean) {
            optionBundle.putBoolean(EXTRA_CROP_FORBID_GIF_WEBP, isForbidCropGifWebp)
        }

        /**
         * Set compression quality [0-100] that will be used to save resulting Bitmap.
         */
        fun setCompressionQuality(@IntRange(from = 0) compressQuality: Int) {
            optionBundle.putInt(EXTRA_COMPRESSION_QUALITY, compressQuality)
        }

        /**
         * Choose what set of gestures will be enabled on each tab - if any.
         */
        fun setAllowedGestures(
            @UCropActivity.GestureTypes tabScale: Int,
            @UCropActivity.GestureTypes tabRotate: Int,
            @UCropActivity.GestureTypes tabAspectRatio: Int
        ) {
            optionBundle.putIntArray(
                EXTRA_ALLOWED_GESTURES,
                intArrayOf(tabScale, tabRotate, tabAspectRatio)
            )
        }

        /**
         * This method sets multiplier that is used to calculate max image scale from min image scale.
         *
         * @param maxScaleMultiplier - (minScale * maxScaleMultiplier) = maxScale
         */
        fun setMaxScaleMultiplier(
            @FloatRange(
                from = 1.0,
                fromInclusive = false
            ) maxScaleMultiplier: Float
        ) {
            optionBundle.putFloat(EXTRA_MAX_SCALE_MULTIPLIER, maxScaleMultiplier)
        }

        /**
         * This method sets animation duration for image to wrap the crop bounds
         *
         * @param durationMillis - duration in milliseconds
         */
        fun setImageToCropBoundsAnimDuration(@IntRange(from = MIN_SIZE.toLong()) durationMillis: Int) {
            optionBundle.putInt(EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, durationMillis)
        }

        /**
         * Setter for max size for both width and height of bitmap that will be decoded from an input Uri and used in the view.
         *
         * @param maxBitmapSize - size in pixels
         */
        fun setMaxBitmapSize(@IntRange(from = MIN_SIZE.toLong()) maxBitmapSize: Int) {
            optionBundle.putInt(EXTRA_MAX_BITMAP_SIZE, maxBitmapSize)
        }

        /**
         * @param color - desired color of dimmed area around the crop bounds
         */
        fun setDimmedLayerColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_DIMMED_LAYER_COLOR, color)
        }

        /**
         * @param color - desired color of dimmed stroke area around the crop bounds
         */
        fun setCircleStrokeColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_CIRCLE_STROKE_COLOR, color)
        }

        /**
         * @param isCircle - set it to true if you want dimmed layer to have an circle inside
         */
        fun setCircleDimmedLayer(isCircle: Boolean) {
            optionBundle.putBoolean(EXTRA_CIRCLE_DIMMED_LAYER, isCircle)
        }

        /**
         * @param show - set to true if you want to see a crop frame rectangle on top of an image
         */
        fun setShowCropFrame(show: Boolean) {
            optionBundle.putBoolean(EXTRA_SHOW_CROP_FRAME, show)
        }

        /**
         * @param color - desired color of crop frame
         */
        fun setCropFrameColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_CROP_FRAME_COLOR, color)
        }

        /**
         * @param width - desired width of crop frame line in pixels
         */
        fun setCropFrameStrokeWidth(@IntRange(from = 0) width: Int) {
            optionBundle.putInt(EXTRA_CROP_FRAME_STROKE_WIDTH, width)
        }

        /**
         * @param show - set to true if you want to see a crop grid/guidelines on top of an image
         */
        fun setShowCropGrid(show: Boolean) {
            optionBundle.putBoolean(EXTRA_SHOW_CROP_GRID, show)
        }

        /**
         * @param count - crop grid rows count.
         */
        fun setCropGridRowCount(@IntRange(from = 0) count: Int) {
            optionBundle.putInt(EXTRA_CROP_GRID_ROW_COUNT, count)
        }

        /**
         * @param count - crop grid columns count.
         */
        fun setCropGridColumnCount(@IntRange(from = 0) count: Int) {
            optionBundle.putInt(EXTRA_CROP_GRID_COLUMN_COUNT, count)
        }

        /**
         * @param color - desired color of crop grid/guidelines
         */
        fun setCropGridColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_CROP_GRID_COLOR, color)
        }

        /**
         * @param width - desired width of crop grid lines in pixels
         */
        fun setCropGridStrokeWidth(@IntRange(from = 0) width: Int) {
            optionBundle.putInt(EXTRA_CROP_GRID_STROKE_WIDTH, width)
        }

        /**
         * @param width Set the circular clipping border
         */
        fun setCircleStrokeWidth(@IntRange(from = 0) width: Int) {
            optionBundle.putInt(EXTRA_CIRCLE_STROKE_WIDTH_LAYER, width)
        }

        /**
         * @param color - desired resolved color of the gallery bar background
         */
        fun setCropGalleryBarBackgroundResources(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_GALLERY_BAR_BACKGROUND, color)
        }

        /**
         * @param color - desired resolved color of the toolbar
         */
        fun setToolbarColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_TOOL_BAR_COLOR, color)
        }

        /**
         * @param color - desired resolved color of the statusbar
         */
        fun setStatusBarColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_STATUS_BAR_COLOR, color)
        }

        /**
         * @param Is the font of the status bar black
         */
        fun isDarkStatusBarBlack(isDarkStatusBarBlack: Boolean) {
            optionBundle.putBoolean(EXTRA_DARK_STATUS_BAR_BLACK, isDarkStatusBarBlack)
        }

        /**
         * Can I drag and drop images when crop
         *
         * @param isDragImages
         */
        fun isDragCropImages(isDragImages: Boolean) {
            optionBundle.putBoolean(EXTRA_DRAG_IMAGES, isDragImages)
        }

        /**
         * @param color - desired resolved color of the active and selected widget and progress wheel middle line (default is white)
         */
        fun setActiveControlsWidgetColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_UCROP_COLOR_CONTROLS_WIDGET_ACTIVE, color)
        }

        /**
         * @param color - desired resolved color of Toolbar text and buttons (default is darker orange)
         */
        fun setToolbarWidgetColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_UCROP_WIDGET_COLOR_TOOLBAR, color)
        }

        /**
         * @param text - desired text for Toolbar title
         */
        fun setToolbarTitle(text: String?) {
            optionBundle.putString(EXTRA_UCROP_TITLE_TEXT_TOOLBAR, text)
        }

        /**
         * @param textSize - desired text for Toolbar title
         */
        fun setToolbarTitleSize(textSize: Int) {
            if (textSize > 0) {
                optionBundle.putInt(EXTRA_UCROP_TITLE_TEXT_SIZE_TOOLBAR, textSize)
            }
        }

        /**
         * @param drawable - desired drawable for the Toolbar left cancel icon
         */
        fun setToolbarCancelDrawable(@DrawableRes drawable: Int) {
            optionBundle.putInt(EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE, drawable)
        }

        /**
         * @param drawable - desired drawable for the Toolbar right crop icon
         */
        fun setToolbarCropDrawable(@DrawableRes drawable: Int) {
            optionBundle.putInt(EXTRA_UCROP_WIDGET_CROP_DRAWABLE, drawable)
        }

        /**
         * @param color - desired resolved color of logo fill (default is darker grey)
         */
        fun setLogoColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_UCROP_LOGO_COLOR, color)
        }

        /**
         * @param hide - set to true to hide the bottom controls (shown by default)
         */
        fun setHideBottomControls(hide: Boolean) {
            optionBundle.putBoolean(EXTRA_HIDE_BOTTOM_CONTROLS, hide)
        }

        /**
         * @param enabled - set to true to let user resize crop bounds (disabled by default)
         */
        fun setFreeStyleCropEnabled(enabled: Boolean) {
            optionBundle.putBoolean(EXTRA_FREE_STYLE_CROP, enabled)
        }

        /**
         * Pass an ordered list of desired aspect ratios that should be available for a user.
         *
         * @param selectedByDefault - index of aspect ratio option that is selected by default (starts with 0).
         * @param aspectRatio       - list of aspect ratio options that are available to user
         */
        fun setAspectRatioOptions(selectedByDefault: Int, vararg aspectRatio: AspectRatio?) {
            require(selectedByDefault < aspectRatio.size) {
                String.format(
                    Locale.US,
                    "Index [selectedByDefault = %d] (0-based) cannot be higher or equal than aspect ratio options count [count = %d].",
                    selectedByDefault, aspectRatio.size
                )
            }
            optionBundle.putInt(EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, selectedByDefault)
            optionBundle.putParcelableArrayList(
                EXTRA_ASPECT_RATIO_OPTIONS, ArrayList<Parcelable>(
                    Arrays.asList(*aspectRatio)
                )
            )
        }

        /**
         * Skip crop mimeType
         *
         * @param mimeTypes Use example [{]
         * @return
         */
        fun setSkipCropMimeType(vararg mimeTypes: String?) {
            if (mimeTypes != null && mimeTypes.size > 0) {
                optionBundle.putStringArrayList(
                    EXTRA_SKIP_CROP_MIME_TYPE,
                    ArrayList(Arrays.asList(*mimeTypes))
                )
            }
        }

        /**
         * @param color - desired background color that should be applied to the root view
         */
        fun setRootViewBackgroundColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR, color)
        }

        /**
         * Set an aspect ratio for crop bounds.
         * User won't see the menu with other ratios options.
         *
         * @param x aspect ratio X
         * @param y aspect ratio Y
         */
        fun withAspectRatio(x: Float, y: Float) {
            optionBundle.putFloat(EXTRA_ASPECT_RATIO_X, x)
            optionBundle.putFloat(EXTRA_ASPECT_RATIO_Y, y)
        }

        /**
         * The corresponding crop scale of each graph in multi graph crop
         *
         * @param aspectRatio - The corresponding crop scale of each graph in multi graph crop
         */
        fun setMultipleCropAspectRatio(vararg aspectRatio: AspectRatio) {
            val aspectRatioX = optionBundle.getFloat(EXTRA_ASPECT_RATIO_X, 0f)
            val aspectRatioY = optionBundle.getFloat(EXTRA_ASPECT_RATIO_Y, 0f)
            if (aspectRatio.size > 0 && aspectRatioX <= 0 && aspectRatioY <= 0) {
                withAspectRatio(aspectRatio[0].aspectRatioX, aspectRatio[0].aspectRatioY)
            }
            optionBundle.putParcelableArrayList(
                EXTRA_MULTIPLE_ASPECT_RATIO, ArrayList<Parcelable>(
                    Arrays.asList(*aspectRatio)
                )
            )
        }

        /**
         * Set an aspect ratio for crop bounds that is evaluated from source image width and height.
         * User won't see the menu with other ratios options.
         */
        fun useSourceImageAspectRatio() {
            optionBundle.putFloat(EXTRA_ASPECT_RATIO_X, 0f)
            optionBundle.putFloat(EXTRA_ASPECT_RATIO_Y, 0f)
        }

        /**
         * Set maximum size for result cropped image.
         *
         * @param width  max cropped image width
         * @param height max cropped image height
         */
        fun withMaxResultSize(
            @IntRange(from = MIN_SIZE.toLong()) width: Int,
            @IntRange(from = MIN_SIZE.toLong()) height: Int
        ) {
            optionBundle.putInt(EXTRA_MAX_SIZE_X, width)
            optionBundle.putInt(EXTRA_MAX_SIZE_Y, height)
        }

        companion object {
            const val EXTRA_COMPRESSION_FORMAT_NAME = EXTRA_PREFIX + ".CompressionFormatName"
            const val EXTRA_COMPRESSION_QUALITY = EXTRA_PREFIX + ".CompressionQuality"
            const val EXTRA_CROP_OUTPUT_DIR = EXTRA_PREFIX + ".CropOutputDir"
            const val EXTRA_CROP_OUTPUT_FILE_NAME = EXTRA_PREFIX + ".CropOutputFileName"
            const val EXTRA_CROP_FORBID_GIF_WEBP = EXTRA_PREFIX + ".ForbidCropGifWebp"
            const val EXTRA_CROP_FORBID_SKIP = EXTRA_PREFIX + ".ForbidSkipCrop"
            const val EXTRA_DARK_STATUS_BAR_BLACK = EXTRA_PREFIX + ".isDarkStatusBarBlack"
            const val EXTRA_DRAG_IMAGES = EXTRA_PREFIX + ".isDragImages"
            const val EXTRA_CROP_CUSTOM_LOADER_BITMAP = EXTRA_PREFIX + ".CustomLoaderCropBitmap"
            const val EXTRA_CROP_DRAG_CENTER = EXTRA_PREFIX + ".DragSmoothToCenter"
            const val EXTRA_ALLOWED_GESTURES = EXTRA_PREFIX + ".AllowedGestures"
            const val EXTRA_MAX_BITMAP_SIZE = EXTRA_PREFIX + ".MaxBitmapSize"
            const val EXTRA_MAX_SCALE_MULTIPLIER = EXTRA_PREFIX + ".MaxScaleMultiplier"
            const val EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION =
                EXTRA_PREFIX + ".ImageToCropBoundsAnimDuration"
            const val EXTRA_DIMMED_LAYER_COLOR = EXTRA_PREFIX + ".DimmedLayerColor"
            const val EXTRA_CIRCLE_STROKE_COLOR = EXTRA_PREFIX + ".CircleStrokeColor"
            const val EXTRA_CIRCLE_DIMMED_LAYER = EXTRA_PREFIX + ".CircleDimmedLayer"
            const val EXTRA_SHOW_CROP_FRAME = EXTRA_PREFIX + ".ShowCropFrame"
            const val EXTRA_CROP_FRAME_COLOR = EXTRA_PREFIX + ".CropFrameColor"
            const val EXTRA_CROP_FRAME_STROKE_WIDTH = EXTRA_PREFIX + ".CropFrameStrokeWidth"
            const val EXTRA_SHOW_CROP_GRID = EXTRA_PREFIX + ".ShowCropGrid"
            const val EXTRA_CROP_GRID_ROW_COUNT = EXTRA_PREFIX + ".CropGridRowCount"
            const val EXTRA_CROP_GRID_COLUMN_COUNT = EXTRA_PREFIX + ".CropGridColumnCount"
            const val EXTRA_CROP_GRID_COLOR = EXTRA_PREFIX + ".CropGridColor"
            const val EXTRA_CROP_GRID_STROKE_WIDTH = EXTRA_PREFIX + ".CropGridStrokeWidth"
            const val EXTRA_CIRCLE_STROKE_WIDTH_LAYER = EXTRA_PREFIX + ".CircleStrokeWidth"
            const val EXTRA_GALLERY_BAR_BACKGROUND = EXTRA_PREFIX + ".GalleryBarBackground"
            const val EXTRA_TOOL_BAR_COLOR = EXTRA_PREFIX + ".ToolbarColor"
            const val EXTRA_STATUS_BAR_COLOR = EXTRA_PREFIX + ".StatusBarColor"
            const val EXTRA_UCROP_COLOR_CONTROLS_WIDGET_ACTIVE =
                EXTRA_PREFIX + ".UcropColorControlsWidgetActive"
            const val EXTRA_UCROP_WIDGET_COLOR_TOOLBAR = EXTRA_PREFIX + ".UcropToolbarWidgetColor"
            const val EXTRA_UCROP_TITLE_TEXT_TOOLBAR = EXTRA_PREFIX + ".UcropToolbarTitleText"
            const val EXTRA_UCROP_TITLE_TEXT_SIZE_TOOLBAR =
                EXTRA_PREFIX + ".UcropToolbarTitleTextSize"
            const val EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE =
                EXTRA_PREFIX + ".UcropToolbarCancelDrawable"
            const val EXTRA_UCROP_WIDGET_CROP_DRAWABLE = EXTRA_PREFIX + ".UcropToolbarCropDrawable"
            const val EXTRA_UCROP_LOGO_COLOR = EXTRA_PREFIX + ".UcropLogoColor"
            const val EXTRA_HIDE_BOTTOM_CONTROLS = EXTRA_PREFIX + ".HideBottomControls"
            const val EXTRA_FREE_STYLE_CROP = EXTRA_PREFIX + ".FreeStyleCrop"
            const val EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT =
                EXTRA_PREFIX + ".AspectRatioSelectedByDefault"
            const val EXTRA_ASPECT_RATIO_OPTIONS = EXTRA_PREFIX + ".AspectRatioOptions"
            const val EXTRA_SKIP_CROP_MIME_TYPE = EXTRA_PREFIX + ".SkipCropMimeType"
            const val EXTRA_MULTIPLE_ASPECT_RATIO = EXTRA_PREFIX + ".MultipleAspectRatio"
            const val EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR =
                EXTRA_PREFIX + ".UcropRootViewBackgroundColor"
        }

        init {
            optionBundle = Bundle()
        }
    }

    companion object {
        const val REQUEST_CROP = 69
        const val RESULT_ERROR = 96
        const val MIN_SIZE = 10
        private const val EXTRA_PREFIX = BuildConfig.LIBRARY_PACKAGE_NAME
        const val EXTRA_CROP_TOTAL_DATA_SOURCE = EXTRA_PREFIX + ".CropTotalDataSource"
        const val EXTRA_CROP_INPUT_ORIGINAL = EXTRA_PREFIX + ".CropInputOriginal"
        const val EXTRA_INPUT_URI = EXTRA_PREFIX + ".InputUri"
        const val EXTRA_OUTPUT_URI = EXTRA_PREFIX + ".OutputUri"
        const val EXTRA_OUTPUT_CROP_ASPECT_RATIO = EXTRA_PREFIX + ".CropAspectRatio"
        const val EXTRA_OUTPUT_IMAGE_WIDTH = EXTRA_PREFIX + ".ImageWidth"
        const val EXTRA_OUTPUT_IMAGE_HEIGHT = EXTRA_PREFIX + ".ImageHeight"
        const val EXTRA_OUTPUT_OFFSET_X = EXTRA_PREFIX + ".OffsetX"
        const val EXTRA_OUTPUT_OFFSET_Y = EXTRA_PREFIX + ".OffsetY"
        const val EXTRA_ERROR = EXTRA_PREFIX + ".Error"
        const val EXTRA_ASPECT_RATIO_X = EXTRA_PREFIX + ".AspectRatioX"
        const val EXTRA_ASPECT_RATIO_Y = EXTRA_PREFIX + ".AspectRatioY"
        const val EXTRA_MAX_SIZE_X = EXTRA_PREFIX + ".MaxSizeX"
        const val EXTRA_MAX_SIZE_Y = EXTRA_PREFIX + ".MaxSizeY"

        /**
         * This method creates new Intent builder and sets both source and destination image URIs.
         *
         * @param source      Uri for image to crop
         * @param destination Uri for saving the cropped image
         * @param totalSource crop data source for list
         */
        fun of(source: Uri, destination: Uri, totalSource: ArrayList<String>?): UCrop {
            require(!(totalSource == null || totalSource.size <= 0)) { "Missing required parameters, count cannot be less than 1" }
            return if (totalSource.size == 1) {
                UCrop(source, destination)
            } else UCrop(source, destination, totalSource)
        }

        /**
         * This method creates new Intent builder and sets both source and destination image URIs.
         *
         * @param source      Uri for image to crop
         * @param destination Uri for saving the cropped image
         */
        fun <T> of(source: Uri, destination: Uri): UCrop {
            return UCrop(source, destination)
        }

        /**
         * Retrieve cropped image Uri from the result Intent
         *
         * @param intent crop result intent
         */
        fun getOutput(intent: Intent): Uri? {
            return intent.getParcelableExtra(EXTRA_OUTPUT_URI)
        }

        /**
         * Retrieve the width of the cropped image
         *
         * @param intent crop result intent
         */
        fun getOutputImageWidth(intent: Intent): Int {
            return intent.getIntExtra(EXTRA_OUTPUT_IMAGE_WIDTH, -1)
        }

        /**
         * Retrieve the height of the cropped image
         *
         * @param intent crop result intent
         */
        fun getOutputImageHeight(intent: Intent): Int {
            return intent.getIntExtra(EXTRA_OUTPUT_IMAGE_HEIGHT, -1)
        }

        /**
         * Retrieve cropped image aspect ratio from the result Intent
         *
         * @param intent crop result intent
         * @return aspect ratio as a floating point value (x:y) - so it will be 1 for 1:1 or 4/3 for 4:3
         */
        fun getOutputCropAspectRatio(intent: Intent): Float {
            return intent.getFloatExtra(EXTRA_OUTPUT_CROP_ASPECT_RATIO, 0f)
        }

        /**
         * Retrieve the x of the cropped offset x
         *
         * @param intent crop result intent
         */
        fun getOutputImageOffsetX(intent: Intent): Int {
            return intent.getIntExtra(EXTRA_OUTPUT_OFFSET_X, 0)
        }

        /**
         * Retrieve the y of the cropped offset y
         *
         * @param intent crop result intent
         */
        fun getOutputImageOffsetY(intent: Intent): Int {
            return intent.getIntExtra(EXTRA_OUTPUT_OFFSET_Y, 0)
        }

        /**
         * Method retrieves error from the result intent.
         *
         * @param result crop result Intent
         * @return Throwable that could happen while image processing
         */
        fun getError(result: Intent): Throwable? {
            return result.getSerializableExtra(EXTRA_ERROR) as Throwable?
        }
    }
}