package com.luck.picture.lib.basic

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.luck.picture.lib.app.PictureAppMaster.Companion.instance
import com.luck.picture.lib.app.PictureAppMaster.appContext
import com.luck.picture.lib.app.PictureAppMaster.pictureSelectorEngine
import com.luck.picture.lib.PictureOnlyCameraFragment.Companion.newInstance
import com.luck.picture.lib.PictureOnlyCameraFragment.getFragmentTag
import com.luck.picture.lib.PictureSelectorFragment.getFragmentTag
import com.luck.picture.lib.PictureSelectorPreviewFragment.getFragmentTag
import com.luck.picture.lib.PictureSelectorPreviewFragment.Companion.newInstance
import com.luck.picture.lib.PictureSelectorPreviewFragment.setExternalPreviewData
import com.luck.picture.lib.PictureSelectorSystemFragment.Companion.newInstance
import com.luck.picture.lib.PictureSelectorFragment.Companion.newInstance
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.utils.FileDirMap
import androidx.core.content.FileProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.luck.picture.lib.PictureSelectorSystemFragment
import com.luck.picture.lib.R
import com.luck.picture.lib.config.*
import com.luck.picture.lib.engine.*
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.*
import com.luck.picture.lib.utils.DoubleUtils
import com.luck.picture.lib.utils.SdkVersionUtils
import java.lang.NullPointerException
import java.util.*

/**
 * @author：luck
 * @date：2022/1/17 5:52 下午
 * @describe：PictureSelectionSystemModel
 */
class PictureSelectionSystemModel(private val selector: PictureSelector, chooseMode: Int) {
    private val selectionConfig: SelectorConfig

    /**
     * @param selectionMode PictureSelector Selection model
     * and [SelectModeConfig.MULTIPLE] or [SelectModeConfig.SINGLE]
     *
     *
     * Use [SelectModeConfig]
     *
     * @return
     */
    fun setSelectionMode(selectionMode: Int): PictureSelectionSystemModel {
        selectionConfig.selectionMode = selectionMode
        return this
    }

    /**
     * Do you need to display the original controller
     *
     *
     * It needs to be used with setSandboxFileEngine
     * [.setOriginalPath()][LocalMedia]
     *
     *
     * @param isOriginalControl
     * @return
     */
    fun isOriginalControl(isOriginalControl: Boolean): PictureSelectionSystemModel {
        selectionConfig.isCheckOriginalImage = isOriginalControl
        return this
    }

    /**
     * Skip crop mimeType
     *
     * @param mimeTypes Use example [{]
     * @return
     */
    fun setSkipCropMimeType(vararg mimeTypes: String?): PictureSelectionSystemModel {
        if (mimeTypes != null && mimeTypes.size > 0) {
            selectionConfig.skipCropList.addAll(Arrays.asList(*mimeTypes))
        }
        return this
    }

    /**
     * Select original image to skip compression
     *
     * @param isOriginalSkipCompress
     * @return
     */
    fun isOriginalSkipCompress(isOriginalSkipCompress: Boolean): PictureSelectionSystemModel {
        selectionConfig.isOriginalSkipCompress = isOriginalSkipCompress
        return this
    }

    /**
     * Image Compress the engine
     *
     * @param engine Image Compress the engine
     * Please use [CompressFileEngine]
     * @return
     */
    @Deprecated("")
    fun setCompressEngine(engine: CompressEngine?): PictureSelectionSystemModel {
        selectionConfig.compressEngine = engine
        selectionConfig.isCompressEngine = true
        return this
    }

    /**
     * Image Compress the engine
     *
     * @param engine Image Compress the engine
     * @return
     */
    fun setCompressEngine(engine: CompressFileEngine?): PictureSelectionSystemModel {
        selectionConfig.compressFileEngine = engine
        selectionConfig.isCompressEngine = true
        return this
    }

    /**
     * Image Crop the engine
     *
     * @param engine Image Crop the engine
     * Please Use [CropFileEngine]
     * @return
     */
    @Deprecated("")
    fun setCropEngine(engine: CropEngine?): PictureSelectionSystemModel {
        selectionConfig.cropEngine = engine
        return this
    }

    /**
     * Image Crop the engine
     *
     * @param engine Image Crop the engine
     * @return
     */
    fun setCropEngine(engine: CropFileEngine?): PictureSelectionSystemModel {
        selectionConfig.cropFileEngine = engine
        return this
    }

    /**
     * App Sandbox file path transform
     *
     * @param engine App Sandbox path transform
     * Please Use [UriToFileTransformEngine]
     * @return
     */
    @Deprecated("")
    fun setSandboxFileEngine(engine: SandboxFileEngine?): PictureSelectionSystemModel {
        if (SdkVersionUtils.isQ()) {
            selectionConfig.sandboxFileEngine = engine
            selectionConfig.isSandboxFileEngine = true
        } else {
            selectionConfig.isSandboxFileEngine = false
        }
        return this
    }

    /**
     * App Sandbox file path transform
     *
     * @param engine App Sandbox path transform
     * @return
     */
    fun setSandboxFileEngine(engine: UriToFileTransformEngine?): PictureSelectionSystemModel {
        if (SdkVersionUtils.isQ()) {
            selectionConfig.uriToFileTransformEngine = engine
            selectionConfig.isSandboxFileEngine = true
        } else {
            selectionConfig.isSandboxFileEngine = false
        }
        return this
    }

    /**
     * # file size The unit is KB
     *
     * @param fileKbSize Filter max file size
     * @return
     */
    fun setSelectMaxFileSize(fileKbSize: Long): PictureSelectionSystemModel {
        if (fileKbSize >= FileSizeUnit.MB) {
            selectionConfig.selectMaxFileSize = fileKbSize
        } else {
            selectionConfig.selectMaxFileSize = fileKbSize * FileSizeUnit.KB
        }
        return this
    }

    /**
     * # file size The unit is KB
     *
     * @param fileKbSize Filter min file size
     * @return
     */
    fun setSelectMinFileSize(fileKbSize: Long): PictureSelectionSystemModel {
        if (fileKbSize >= FileSizeUnit.MB) {
            selectionConfig.selectMinFileSize = fileKbSize
        } else {
            selectionConfig.selectMinFileSize = fileKbSize * FileSizeUnit.KB
        }
        return this
    }

    /**
     * Select the max number of seconds for video or audio support
     *
     * @param maxDurationSecond select video max second
     * @return
     */
    fun setSelectMaxDurationSecond(maxDurationSecond: Int): PictureSelectionSystemModel {
        selectionConfig.selectMaxDurationSecond = maxDurationSecond * 1000
        return this
    }

    /**
     * Select the min number of seconds for video or audio support
     *
     * @param minDurationSecond select video min second
     * @return
     */
    fun setSelectMinDurationSecond(minDurationSecond: Int): PictureSelectionSystemModel {
        selectionConfig.selectMinDurationSecond = minDurationSecond * 1000
        return this
    }

    /**
     * Custom interception permission processing
     *
     * @param listener
     * @return
     */
    fun setPermissionsInterceptListener(listener: OnPermissionsInterceptListener?): PictureSelectionSystemModel {
        selectionConfig.onPermissionsEventListener = listener
        return this
    }

    /**
     * permission description
     *
     * @param listener
     * @return
     */
    fun setPermissionDescriptionListener(listener: OnPermissionDescriptionListener?): PictureSelectionSystemModel {
        selectionConfig.onPermissionDescriptionListener = listener
        return this
    }

    /**
     * Permission denied
     *
     * @param listener
     * @return
     */
    fun setPermissionDeniedListener(listener: OnPermissionDeniedListener?): PictureSelectionSystemModel {
        selectionConfig.onPermissionDeniedListener = listener
        return this
    }

    /**
     * Custom limit tips
     *
     * @param listener
     */
    fun setSelectLimitTipsListener(listener: OnSelectLimitTipsListener?): PictureSelectionSystemModel {
        selectionConfig.onSelectLimitTipsListener = listener
        return this
    }

    /**
     * You need to filter out the content that does not meet the selection criteria
     *
     * @param listener
     * @return
     */
    fun setSelectFilterListener(listener: OnSelectFilterListener?): PictureSelectionSystemModel {
        selectionConfig.onSelectFilterListener = listener
        return this
    }

    /**
     * You can add a watermark to the image
     *
     * @param listener
     * @return
     */
    fun setAddBitmapWatermarkListener(listener: OnBitmapWatermarkEventListener?): PictureSelectionSystemModel {
        if (selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
            selectionConfig.onBitmapWatermarkListener = listener
        }
        return this
    }

    /**
     * Process video thumbnails
     *
     * @param listener
     * @return
     */
    fun setVideoThumbnailListener(listener: OnVideoThumbnailEventListener?): PictureSelectionSystemModel {
        if (selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
            selectionConfig.onVideoThumbnailEventListener = listener
        }
        return this
    }

    /**
     * Custom show loading dialog
     *
     * @param listener
     * @return
     */
    fun setCustomLoadingListener(listener: OnCustomLoadingListener?): PictureSelectionSystemModel {
        selectionConfig.onCustomLoadingListener = listener
        return this
    }

    /**
     * Call the system library to obtain resources
     *
     *
     * Using the system gallery library, some API functions will not be supported
     *
     *
     * @param call
     */
    fun forSystemResult(call: OnResultCallbackListener<LocalMedia?>?) {
        if (!DoubleUtils.isFastDoubleClick()) {
            val activity = selector.activity
                ?: throw NullPointerException("Activity cannot be null")
            if (call == null) {
                throw NullPointerException("OnResultCallbackListener cannot be null")
            }
            selectionConfig.onResultCallListener = call
            selectionConfig.isResultListenerBack = true
            selectionConfig.isActivityResultBack = false
            var fragmentManager: FragmentManager? = null
            if (activity is FragmentActivity) {
                fragmentManager = activity.supportFragmentManager
            }
            if (fragmentManager == null) {
                throw NullPointerException("FragmentManager cannot be null")
            }
            val fragment = fragmentManager.findFragmentByTag(PictureSelectorSystemFragment.TAG)
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
            }
            FragmentInjectManager.injectSystemRoomFragment(
                fragmentManager,
                PictureSelectorSystemFragment.TAG, PictureSelectorSystemFragment.newInstance()
            )
        }
    }

    /**
     * Call the system library to obtain resources
     *
     *
     * Using the system gallery library, some API functions will not be supported
     *
     *
     *
     * The [IBridgePictureBehavior] interface needs to be
     * implemented in the activity or fragment you call to receive the returned results
     *
     */
    fun forSystemResult() {
        if (!DoubleUtils.isFastDoubleClick()) {
            val activity = selector.activity
                ?: throw NullPointerException("Activity cannot be null")
            if (activity !is IBridgePictureBehavior) {
                throw NullPointerException(
                    "Use only forSystemResult();," +
                            "Activity or Fragment interface needs to be implemented " + IBridgePictureBehavior::class.java
                )
            }
            selectionConfig.isActivityResultBack = true
            selectionConfig.onResultCallListener = null
            selectionConfig.isResultListenerBack = false
            var fragmentManager: FragmentManager? = null
            if (activity is FragmentActivity) {
                fragmentManager = (activity as FragmentActivity).supportFragmentManager
            }
            if (fragmentManager == null) {
                throw NullPointerException("FragmentManager cannot be null")
            }
            val fragment = fragmentManager.findFragmentByTag(PictureSelectorSystemFragment.TAG)
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
            }
            FragmentInjectManager.injectSystemRoomFragment(
                fragmentManager,
                PictureSelectorSystemFragment.TAG, PictureSelectorSystemFragment.newInstance()
            )
        }
    }

    /**
     * Start PictureSelector
     *
     * @param requestCode
     */
    fun forSystemResultActivity(requestCode: Int) {
        if (!DoubleUtils.isFastDoubleClick()) {
            val activity = selector.activity
                ?: throw NullPointerException("Activity cannot be null")
            selectionConfig.isResultListenerBack = false
            selectionConfig.isActivityResultBack = true
            val intent = Intent(activity, PictureSelectorTransparentActivity::class.java)
            intent.putExtra(
                PictureConfig.EXTRA_MODE_TYPE_SOURCE,
                PictureConfig.MODE_TYPE_SYSTEM_SOURCE
            )
            val fragment = selector.fragment
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode)
            } else {
                activity.startActivityForResult(intent, requestCode)
            }
            activity.overridePendingTransition(R.anim.ps_anim_fade_in, 0)
        }
    }

    /**
     * ActivityResultLauncher PictureSelector
     *
     * @param launcher use []
     */
    fun forSystemResultActivity(launcher: ActivityResultLauncher<Intent?>?) {
        if (!DoubleUtils.isFastDoubleClick()) {
            val activity = selector.activity
                ?: throw NullPointerException("Activity cannot be null")
            if (launcher == null) {
                throw NullPointerException("ActivityResultLauncher cannot be null")
            }
            selectionConfig.isResultListenerBack = false
            selectionConfig.isActivityResultBack = true
            val intent = Intent(activity, PictureSelectorTransparentActivity::class.java)
            intent.putExtra(
                PictureConfig.EXTRA_MODE_TYPE_SOURCE,
                PictureConfig.MODE_TYPE_SYSTEM_SOURCE
            )
            launcher.launch(intent)
            activity.overridePendingTransition(R.anim.ps_anim_fade_in, 0)
        }
    }

    /**
     * Start PictureSelector
     *
     * @param call
     */
    fun forSystemResultActivity(call: OnResultCallbackListener<LocalMedia?>?) {
        if (!DoubleUtils.isFastDoubleClick()) {
            val activity = selector.activity
                ?: throw NullPointerException("Activity cannot be null")
            if (call == null) {
                throw NullPointerException("OnResultCallbackListener cannot be null")
            }
            // 绑定回调监听
            selectionConfig.isResultListenerBack = true
            selectionConfig.isActivityResultBack = false
            selectionConfig.onResultCallListener = call
            val intent = Intent(activity, PictureSelectorTransparentActivity::class.java)
            intent.putExtra(
                PictureConfig.EXTRA_MODE_TYPE_SOURCE,
                PictureConfig.MODE_TYPE_SYSTEM_SOURCE
            )
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.ps_anim_fade_in, 0)
        }
    }

    init {
        selectionConfig = SelectorConfig()
        SelectorProviders.instance.addSelectorConfigQueue(selectionConfig)
        selectionConfig.chooseMode = chooseMode
        selectionConfig.isPreviewFullScreenMode = false
        selectionConfig.isPreviewZoomEffect = false
    }
}