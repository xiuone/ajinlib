package com.yalantis.ucrop

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.ColorFilter
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import android.widget.Toast
import com.yalantis.ucrop.statusbar.ImmersiveManager.immersiveAboveAPI23
import com.yalantis.ucrop.util.FileUtils.getMimeTypeFromMediaContentUri
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
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.R
import com.yalantis.ucrop.UCropFragment.UCropResult
import com.yalantis.ucrop.decoration.GridSpacingItemDecoration
import com.yalantis.ucrop.model.AspectRatio
import com.yalantis.ucrop.model.CustomIntentKey
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.ArrayList
import java.util.HashSet
import java.util.LinkedHashMap

/**
 * @author：luck
 * @date：2021/11/28 7:59 下午
 * @describe：UCropMultipleActivity
 */
class UCropMultipleActivity constructor() : AppCompatActivity(), UCropFragmentCallback {
    private var mToolbarTitle: String? = null
    private var mToolbarTitleSize: Int = 0

    // Enables dynamic coloring
    private var mToolbarColor: Int = 0
    private var mStatusBarColor: Int = 0

    @DrawableRes
    private var mToolbarCancelDrawable: Int = 0

    @DrawableRes
    private var mToolbarCropDrawable: Int = 0
    private var mToolbarWidgetColor: Int = 0
    private var mShowLoader: Boolean = false
    private val fragments: MutableList<UCropFragment> = ArrayList()
    private var uCropCurrentFragment: UCropFragment? = null
    private var currentFragmentPosition: Int = 0
    private var uCropSupportList: ArrayList<String>? = null
    private var uCropNotSupportList: ArrayList<String>? = null
    private val uCropTotalQueue: LinkedHashMap<String?, JSONObject?> = LinkedHashMap()
    private var outputCropFileName: String? = null
    private var galleryAdapter: UCropGalleryAdapter? = null
    private var isForbidCropGifWebp: Boolean = false
    private var isSkipCropForbid: Boolean = false
    private var aspectRatioList: ArrayList<AspectRatio>? = null
    private val filterSet: HashSet<String?> = HashSet()

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersive()
        setContentView(R.layout.ucrop_activity_multiple)
        setupViews(getIntent())
        initCropFragments(getIntent())
    }

    private fun immersive() {
        val intent: Intent = getIntent()
        val isDarkStatusBarBlack: Boolean =
            intent.getBooleanExtra(UCrop.Options.Companion.EXTRA_DARK_STATUS_BAR_BLACK, false)
        mStatusBarColor = intent.getIntExtra(
            UCrop.Options.Companion.EXTRA_STATUS_BAR_COLOR,
            ContextCompat.getColor(this, R.color.ucrop_color_statusbar)
        )
        immersiveAboveAPI23(this, mStatusBarColor, mStatusBarColor, isDarkStatusBarBlack)
    }

    private fun initCropFragments(intent: Intent) {
        isSkipCropForbid =
            intent.getBooleanExtra(UCrop.Options.Companion.EXTRA_CROP_FORBID_SKIP, false)
        val totalCropData: ArrayList<String>? =
            intent.getStringArrayListExtra(UCrop.Companion.EXTRA_CROP_TOTAL_DATA_SOURCE)
        if (totalCropData == null || totalCropData.size <= 0) {
            throw IllegalArgumentException("Missing required parameters, count cannot be less than 1")
        }
        uCropSupportList = ArrayList()
        uCropNotSupportList = ArrayList()
        for (i in totalCropData.indices) {
            val path: String = totalCropData.get(i)
            uCropTotalQueue.put(path, JSONObject())
            val realPath: String? = if (isContent(path)) getPath(this, Uri.parse(path)) else path
            val mimeType: String? = getPathToMimeType(path)
            if (isUrlHasVideo((realPath)!!) || isHasVideo(mimeType) || isHasAudio(mimeType)) {
                // not crop type
                uCropNotSupportList!!.add(path)
            } else {
                uCropSupportList!!.add(path)
                val extras: Bundle? = intent.getExtras()
                if (extras == null) {
                    continue
                }
                val inputUri: Uri =
                    if (isContent(path) || isHasHttp(path)) Uri.parse(path) else Uri.fromFile(
                        File(path)
                    )
                val postfix: String = getPostfixDefaultJPEG(
                    this@UCropMultipleActivity,
                    isForbidCropGifWebp, inputUri
                )
                val fileName: String =
                    if (TextUtils.isEmpty(outputCropFileName)) getCreateFileName("CROP_" + (i + 1)) + postfix else (i + 1).toString() + createFileName + "_" + outputCropFileName
                val destinationUri: Uri = Uri.fromFile(
                    File(
                        sandboxPathDir, fileName
                    )
                )
                extras.putParcelable(UCrop.Companion.EXTRA_INPUT_URI, inputUri)
                extras.putParcelable(UCrop.Companion.EXTRA_OUTPUT_URI, destinationUri)
                val aspectRatio: AspectRatio? =
                    if (aspectRatioList != null && aspectRatioList!!.size > i) aspectRatioList!!.get(
                        i
                    ) else null
                extras.putFloat(
                    UCrop.Companion.EXTRA_ASPECT_RATIO_X,
                    if (aspectRatio != null) aspectRatio.aspectRatioX else -1
                )
                extras.putFloat(
                    UCrop.Companion.EXTRA_ASPECT_RATIO_Y,
                    if (aspectRatio != null) aspectRatio.aspectRatioY else -1
                )
                val uCropFragment: UCropFragment = UCropFragment.Companion.newInstance(extras)
                fragments.add(uCropFragment)
            }
        }
        if (uCropSupportList!!.size == 0) {
            throw IllegalArgumentException("No clipping data sources are available")
        }
        setGalleryAdapter()
        val uCropFragment: UCropFragment = fragments.get(cropSupportPosition)
        switchCropFragment(uCropFragment, cropSupportPosition)
        galleryAdapter.setCurrentSelectPosition(cropSupportPosition)
    }

    /**
     * getCropSupportPosition
     *
     * @return
     */
    private val cropSupportPosition: Int
        private get() {
            var position: Int = 0
            val intent: Intent = getIntent()
            val extras: Bundle? = intent.getExtras()
            if (extras == null) {
                return position
            }
            val skipCropMimeType: ArrayList<String?>? =
                extras.getStringArrayList(UCrop.Options.Companion.EXTRA_SKIP_CROP_MIME_TYPE)
            if (skipCropMimeType != null && skipCropMimeType.size > 0) {
                position = -1
                filterSet.addAll(skipCropMimeType)
                for (i in uCropSupportList!!.indices) {
                    val path: String = uCropSupportList!!.get(i)
                    val mimeType: String? = getPathToMimeType(path)
                    position++
                    if (!filterSet.contains(mimeType)) {
                        break
                    }
                }
                if (position == -1 || position > fragments.size) {
                    position = 0
                }
            }
            return position
        }

    /**
     * getPathToMimeType
     *
     * @param path
     * @return
     */
    private fun getPathToMimeType(path: String): String? {
        val mimeType: String?
        if (isContent(path)) {
            mimeType = getMimeTypeFromMediaContentUri(this, Uri.parse(path))
        } else {
            mimeType = getMimeTypeFromMediaContentUri(this, Uri.fromFile(File(path)))
        }
        return mimeType
    }

    /**
     * switch crop fragment tab
     *
     * @param targetFragment target fragment
     * @param position       target index
     */
    private fun switchCropFragment(targetFragment: UCropFragment, position: Int) {
        val transaction: FragmentTransaction = getSupportFragmentManager().beginTransaction()
        if (!targetFragment.isAdded()) {
            if (uCropCurrentFragment != null) {
                transaction.hide(uCropCurrentFragment!!)
            }
            transaction.add(
                R.id.fragment_container,
                targetFragment,
                UCropFragment.Companion.TAG + "-" + position
            )
        } else {
            transaction.hide((uCropCurrentFragment)!!).show(targetFragment)
            targetFragment.fragmentReVisible()
        }
        currentFragmentPosition = position
        uCropCurrentFragment = targetFragment
        transaction.commitAllowingStateLoss()
    }

    private fun setGalleryAdapter() {
        val galleryRecycle: RecyclerView = findViewById(R.id.recycler_gallery)
        val layoutManager: LinearLayoutManager = LinearLayoutManager(this)
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL)
        galleryRecycle.setLayoutManager(layoutManager)
        if (galleryRecycle.getItemDecorationCount() == 0) {
            galleryRecycle.addItemDecoration(
                GridSpacingItemDecoration(
                    Int.MAX_VALUE,
                    dip2px(this, 6f), true
                )
            )
        }
        val animation: LayoutAnimationController = AnimationUtils
            .loadLayoutAnimation(this, R.anim.ucrop_layout_animation_fall_down)
        galleryRecycle.setLayoutAnimation(animation)
        val galleryBarBackground: Int = getIntent().getIntExtra(
            UCrop.Options.Companion.EXTRA_GALLERY_BAR_BACKGROUND,
            R.drawable.ucrop_gallery_bg
        )
        galleryRecycle.setBackgroundResource(galleryBarBackground)
        galleryAdapter = UCropGalleryAdapter(uCropSupportList)
        galleryAdapter!!.setOnItemClickListener(object : UCropGalleryAdapter.OnItemClickListener {
            public override fun onItemClick(position: Int, view: View?) {
                if (isSkipCropForbid) {
                    return
                }
                val path: String = uCropSupportList!!.get(position)
                val mimeType: String? = getPathToMimeType(path)
                if (filterSet.contains(mimeType)) {
                    Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.ucrop_not_crop), Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                if (galleryAdapter.getCurrentSelectPosition() == position) {
                    return
                }
                galleryAdapter!!.notifyItemChanged(galleryAdapter.getCurrentSelectPosition())
                galleryAdapter.setCurrentSelectPosition(position)
                galleryAdapter!!.notifyItemChanged(position)
                val uCropFragment: UCropFragment = fragments.get(position)
                switchCropFragment(uCropFragment, position)
            }
        })
        galleryRecycle.setAdapter(galleryAdapter)
    }

    /**
     * create crop output path dir
     *
     * @return
     */
    private val sandboxPathDir: String
        private get() {
            val customFile: File
            val outputDir: String? =
                getIntent().getStringExtra(UCrop.Options.Companion.EXTRA_CROP_OUTPUT_DIR)
            if (outputDir == null || ("" == outputDir)) {
                customFile = File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.getAbsolutePath(),
                    "Sandbox"
                )
            } else {
                customFile = File(outputDir)
            }
            if (!customFile.exists()) {
                customFile.mkdirs()
            }
            return customFile.getAbsolutePath() + File.separator
        }

    private fun setupViews(intent: Intent) {
        aspectRatioList =
            getIntent().getParcelableArrayListExtra(UCrop.Options.Companion.EXTRA_MULTIPLE_ASPECT_RATIO)
        isForbidCropGifWebp =
            intent.getBooleanExtra(UCrop.Options.Companion.EXTRA_CROP_FORBID_GIF_WEBP, false)
        outputCropFileName =
            intent.getStringExtra(UCrop.Options.Companion.EXTRA_CROP_OUTPUT_FILE_NAME)
        mStatusBarColor = intent.getIntExtra(
            UCrop.Options.Companion.EXTRA_STATUS_BAR_COLOR,
            ContextCompat.getColor(this, R.color.ucrop_color_statusbar)
        )
        mToolbarColor = intent.getIntExtra(
            UCrop.Options.Companion.EXTRA_TOOL_BAR_COLOR,
            ContextCompat.getColor(this, R.color.ucrop_color_toolbar)
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
            if (mToolbarTitle != null) mToolbarTitle else getResources().getString(R.string.ucrop_label_edit_photo)
        setupAppBar()
    }

    /**
     * Configures and styles both status bar and toolbar.
     */
    private fun setupAppBar() {
        setStatusBarColor(mStatusBarColor)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        // Set all of the Toolbar coloring
        toolbar.setBackgroundColor(mToolbarColor)
        toolbar.setTitleTextColor(mToolbarWidgetColor)
        val toolbarTitle: TextView = toolbar.findViewById(R.id.toolbar_title)
        toolbarTitle.setTextColor(mToolbarWidgetColor)
        toolbarTitle.setText(mToolbarTitle)
        toolbarTitle.setTextSize(mToolbarTitleSize.toFloat())

        // Color buttons inside the Toolbar
        val stateButtonDrawable: Drawable =
            AppCompatResources.getDrawable(this, mToolbarCancelDrawable)!!
                .mutate()
        val colorFilter: ColorFilter? = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            mToolbarWidgetColor,
            BlendModeCompat.SRC_ATOP
        )
        stateButtonDrawable.setColorFilter(colorFilter)
        toolbar.setNavigationIcon(stateButtonDrawable)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
        }
    }

    /**
     * Sets status-bar color for L devices.
     *
     * @param color - status-bar color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setStatusBarColor(@ColorInt color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window? = getWindow()
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.setStatusBarColor(color)
            }
        }
    }

    public override fun loadingProgress(showLoader: Boolean) {
        mShowLoader = showLoader
        supportInvalidateOptionsMenu()
    }

    public override fun onCropFinish(result: UCropResult) {
        when (result.mResultCode) {
            RESULT_OK -> {
                val realPosition: Int = currentFragmentPosition + uCropNotSupportList!!.size
                val realTotalSize: Int = uCropNotSupportList!!.size + uCropSupportList!!.size - 1
                mergeCropResult(result.mResultData)
                if (realPosition == realTotalSize) {
                    onCropCompleteFinish()
                } else {
                    var nextFragmentPosition: Int = currentFragmentPosition + 1
                    var path: String = uCropSupportList!!.get(nextFragmentPosition)
                    var mimeType: String? = getPathToMimeType(path)
                    var isCropCompleteFinish: Boolean = false
                    while (filterSet.contains(mimeType)) {
                        if (nextFragmentPosition == realTotalSize) {
                            isCropCompleteFinish = true
                            break
                        } else {
                            nextFragmentPosition += 1
                            path = uCropSupportList!!.get(nextFragmentPosition)
                            mimeType = getPathToMimeType(path)
                        }
                    }
                    if (isCropCompleteFinish) {
                        onCropCompleteFinish()
                    } else {
                        val uCropFragment: UCropFragment = fragments.get(nextFragmentPosition)
                        switchCropFragment(uCropFragment, nextFragmentPosition)
                        galleryAdapter!!.notifyItemChanged(galleryAdapter.getCurrentSelectPosition())
                        galleryAdapter.setCurrentSelectPosition(nextFragmentPosition)
                        galleryAdapter!!.notifyItemChanged(galleryAdapter.getCurrentSelectPosition())
                    }
                }
            }
            UCrop.Companion.RESULT_ERROR -> handleCropError(result.mResultData)
        }
    }

    /**
     * onCropCompleteFinish
     */
    private fun onCropCompleteFinish() {
        val array: JSONArray = JSONArray()
        for (stringJSONObjectEntry: Map.Entry<String?, JSONObject?> in uCropTotalQueue.entries) {
            val `object`: JSONObject? = stringJSONObjectEntry.value
            array.put(`object`)
        }
        val intent: Intent = Intent()
        intent.putExtra(MediaStore.EXTRA_OUTPUT, array.toString())
        setResult(RESULT_OK, intent)
        finish()
    }

    /**
     * merge crop result
     *
     * @param intent
     */
    private fun mergeCropResult(intent: Intent?) {
        try {
            val key: String? = intent!!.getStringExtra(UCrop.Companion.EXTRA_CROP_INPUT_ORIGINAL)
            val uCropObject: JSONObject? = uCropTotalQueue.get(key)
            val output: Uri? = UCrop.Companion.getOutput((intent))
            uCropObject!!.put(
                CustomIntentKey.EXTRA_OUT_PUT_PATH,
                if (output != null) output.getPath() else ""
            )
            uCropObject.put(
                CustomIntentKey.EXTRA_IMAGE_WIDTH, UCrop.Companion.getOutputImageWidth(
                    (intent)
                )
            )
            uCropObject.put(
                CustomIntentKey.EXTRA_IMAGE_HEIGHT, UCrop.Companion.getOutputImageHeight(
                    (intent)
                )
            )
            uCropObject.put(
                CustomIntentKey.EXTRA_OFFSET_X, UCrop.Companion.getOutputImageOffsetX(
                    (intent)
                )
            )
            uCropObject.put(
                CustomIntentKey.EXTRA_OFFSET_Y, UCrop.Companion.getOutputImageOffsetY(
                    (intent)
                )
            )
            uCropObject.put(
                CustomIntentKey.EXTRA_ASPECT_RATIO, UCrop.Companion.getOutputCropAspectRatio(
                    (intent)
                ).toDouble()
            )
            uCropTotalQueue.put(key, uCropObject)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleCropError(result: Intent) {
        val cropError: Throwable? = UCrop.Companion.getError(result)
        if (cropError != null) {
            Toast.makeText(this@UCropMultipleActivity, cropError.message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this@UCropMultipleActivity, "Unexpected error", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onDestroy() {
        UCropDevelopConfig.destroy()
        super.onDestroy()
    }

    public override fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.ucrop_menu_activity, menu)

        // Change crop & loader menu icons color to match the rest of the UI colors
        val menuItemLoader: MenuItem = menu.findItem(R.id.menu_loader)
        val menuItemLoaderIcon: Drawable? = menuItemLoader.getIcon()
        if (menuItemLoaderIcon != null) {
            try {
                menuItemLoaderIcon.mutate()
                val colorFilter: ColorFilter? =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        mToolbarWidgetColor,
                        BlendModeCompat.SRC_ATOP
                    )
                menuItemLoaderIcon.setColorFilter(colorFilter)
                menuItemLoader.setIcon(menuItemLoaderIcon)
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
            (menuItemLoader.getIcon() as Animatable?)!!.start()
        }
        val menuItemCrop: MenuItem = menu.findItem(R.id.menu_crop)
        val menuItemCropIcon: Drawable? = ContextCompat.getDrawable(this, mToolbarCropDrawable)
        if (menuItemCropIcon != null) {
            menuItemCropIcon.mutate()
            val colorFilter: ColorFilter? =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    mToolbarWidgetColor,
                    BlendModeCompat.SRC_ATOP
                )
            menuItemCropIcon.setColorFilter(colorFilter)
            menuItemCrop.setIcon(menuItemCropIcon)
        }
        return true
    }

    public override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.menu_crop).setVisible(!mShowLoader)
        menu.findItem(R.id.menu_loader).setVisible(mShowLoader)
        return super.onPrepareOptionsMenu(menu)
    }

    public override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == R.id.menu_crop) {
            if (uCropCurrentFragment != null && uCropCurrentFragment!!.isAdded()) {
                uCropCurrentFragment!!.cropAndSaveImage()
            }
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}