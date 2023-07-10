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
import com.luck.picture.lib.PictureOnlyCameraFragment
import com.luck.picture.lib.R
import com.luck.picture.lib.config.*
import com.luck.picture.lib.engine.*
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.*
import com.luck.picture.lib.utils.DoubleUtils
import com.luck.picture.lib.utils.SdkVersionUtils
import java.lang.NullPointerException
import java.util.ArrayList

/**
 * @author：luck
 * @date：2022/1/18 9:33 上午
 * @describe：PictureSelectionCameraModel
 */
class PictureSelectionCameraModel(private val selector: PictureSelector, chooseMode: Int) {
    private val selectionConfig: SelectorConfig

    /**
     * Set App Language
     *
     * @param language [LanguageConfig]
     * @return PictureSelectionModel
     */
    fun setLanguage(language: Int): PictureSelectionCameraModel {
        selectionConfig.language = language
        return this
    }

    /**
     * Set App default Language
     *
     * @param defaultLanguage default language [LanguageConfig]
     * @return PictureSelectionModel
     */
    fun setDefaultLanguage(defaultLanguage: Int): PictureSelectionCameraModel {
        selectionConfig.defaultLanguage = defaultLanguage
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
    fun setCompressEngine(engine: CompressEngine?): PictureSelectionCameraModel {
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
    fun setCompressEngine(engine: CompressFileEngine?): PictureSelectionCameraModel {
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
    fun setCropEngine(engine: CropEngine?): PictureSelectionCameraModel {
        selectionConfig.cropEngine = engine
        return this
    }

    /**
     * Image Crop the engine
     *
     * @param engine Image Crop the engine
     * @return
     */
    fun setCropEngine(engine: CropFileEngine?): PictureSelectionCameraModel {
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
    fun setSandboxFileEngine(engine: SandboxFileEngine?): PictureSelectionCameraModel {
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
    fun setSandboxFileEngine(engine: UriToFileTransformEngine?): PictureSelectionCameraModel {
        if (SdkVersionUtils.isQ()) {
            selectionConfig.uriToFileTransformEngine = engine
            selectionConfig.isSandboxFileEngine = true
        } else {
            selectionConfig.isSandboxFileEngine = false
        }
        return this
    }

    /**
     * Change the desired orientation of this activity.  If the activity
     * is currently in the foreground or otherwise impacting the screen
     * orientation, the screen will immediately be changed (possibly causing
     * the activity to be restarted). Otherwise, this will be used the next
     * time the activity is visible.
     *
     * @param requestedOrientation An orientation constant as used in
     * [ActivityInfo.screenOrientation][android.content.pm.ActivityInfo.screenOrientation].
     */
    fun setRequestedOrientation(requestedOrientation: Int): PictureSelectionCameraModel {
        selectionConfig.requestedOrientation = requestedOrientation
        return this
    }

    /**
     * Intercept camera click events, and users can implement their own camera framework
     *
     * @param listener
     * @return
     */
    fun setCameraInterceptListener(listener: OnCameraInterceptListener?): PictureSelectionCameraModel {
        selectionConfig.onCameraInterceptListener = listener
        return this
    }

    /**
     * Intercept Record Audio click events, and users can implement their own Record Audio framework
     *
     * @param listener
     * @return
     */
    fun setRecordAudioInterceptListener(listener: OnRecordAudioInterceptListener?): PictureSelectionCameraModel {
        selectionConfig.onRecordAudioListener = listener
        return this
    }

    /**
     * Custom interception permission processing
     *
     * @param listener
     * @return
     */
    fun setPermissionsInterceptListener(listener: OnPermissionsInterceptListener?): PictureSelectionCameraModel {
        selectionConfig.onPermissionsEventListener = listener
        return this
    }

    /**
     * permission description
     *
     * @param listener
     * @return
     */
    fun setPermissionDescriptionListener(listener: OnPermissionDescriptionListener?): PictureSelectionCameraModel {
        selectionConfig.onPermissionDescriptionListener = listener
        return this
    }

    /**
     * Permission denied
     *
     * @param listener
     * @return
     */
    fun setPermissionDeniedListener(listener: OnPermissionDeniedListener?): PictureSelectionCameraModel {
        selectionConfig.onPermissionDeniedListener = listener
        return this
    }

    /**
     * Custom limit tips
     *
     * @param listener
     */
    fun setSelectLimitTipsListener(listener: OnSelectLimitTipsListener?): PictureSelectionCameraModel {
        selectionConfig.onSelectLimitTipsListener = listener
        return this
    }

    /**
     * You can add a watermark to the image
     *
     * @param listener
     * @return
     */
    fun setAddBitmapWatermarkListener(listener: OnBitmapWatermarkEventListener?): PictureSelectionCameraModel {
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
    fun setVideoThumbnailListener(listener: OnVideoThumbnailEventListener?): PictureSelectionCameraModel {
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
    fun setCustomLoadingListener(listener: OnCustomLoadingListener?): PictureSelectionCameraModel {
        selectionConfig.onCustomLoadingListener = listener
        return this
    }

    /**
     * Do you want to open a foreground service to prevent the system from reclaiming the memory
     * of some models due to the use of cameras
     *
     * @param isForeground
     * @return
     */
    fun isCameraForegroundService(isForeground: Boolean): PictureSelectionCameraModel {
        selectionConfig.isCameraForegroundService = isForeground
        return this
    }

    /**
     * Choose between photographing and shooting in ofAll mode
     *
     * @param ofAllCameraType [or SelectMimeType.ofVideo][SelectMimeType.ofImage]
     * The default is ofAll() mode
     * @return
     */
    fun setOfAllCameraType(ofAllCameraType: Int): PictureSelectionCameraModel {
        selectionConfig.ofAllCameraType = ofAllCameraType
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
    fun isOriginalControl(isOriginalControl: Boolean): PictureSelectionCameraModel {
        selectionConfig.isOriginalControl = isOriginalControl
        selectionConfig.isCheckOriginalImage = isOriginalControl
        return this
    }

    /**
     * Select original image to skip compression
     *
     * @param isOriginalSkipCompress
     * @return
     */
    fun isOriginalSkipCompress(isOriginalSkipCompress: Boolean): PictureSelectionCameraModel {
        selectionConfig.isOriginalSkipCompress = isOriginalSkipCompress
        return this
    }

    /**
     * The video quality output mode is only for system recording, and there are only two modes: poor quality or high quality
     *
     * @param videoQuality video quality and 0 or 1
     * Use [VideoQuality]
     *
     *
     * There are limitations, only high or low
     *
     * @return
     */
    @Deprecated("")
    fun setVideoQuality(videoQuality: Int): PictureSelectionCameraModel {
        selectionConfig.videoQuality = videoQuality
        return this
    }

    /**
     * Select the maximum number of files
     *
     * @param maxSelectNum PictureSelector max selection
     */
    private fun setMaxSelectNum(maxSelectNum: Int): PictureSelectionCameraModel {
        selectionConfig.maxSelectNum =
            if (selectionConfig.selectionMode == SelectModeConfig.SINGLE) 1 else maxSelectNum
        return this
    }

    /**
     * Select the maximum video number of files
     *
     * @param maxVideoSelectNum PictureSelector video max selection
     */
    fun setMaxVideoSelectNum(maxVideoSelectNum: Int): PictureSelectionCameraModel {
        selectionConfig.maxVideoSelectNum =
            if (selectionConfig.chooseMode == SelectMimeType.ofVideo()) 0 else maxVideoSelectNum
        return this
    }

    /**
     * # file size The unit is KB
     *
     * @param fileKbSize Filter max file size
     * @return
     */
    fun setSelectMaxFileSize(fileKbSize: Long): PictureSelectionCameraModel {
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
    fun setSelectMinFileSize(fileKbSize: Long): PictureSelectionCameraModel {
        if (fileKbSize >= FileSizeUnit.MB) {
            selectionConfig.selectMinFileSize = fileKbSize
        } else {
            selectionConfig.selectMinFileSize = fileKbSize * FileSizeUnit.KB
        }
        return this
    }

    /**
     * camera output image format
     *
     * @param imageFormat PictureSelector media format
     * @return
     */
    fun setCameraImageFormat(imageFormat: String?): PictureSelectionCameraModel {
        selectionConfig.cameraImageFormat = imageFormat
        return this
    }

    /**
     * camera output image format
     *
     * @param imageFormat PictureSelector media format
     * @return
     */
    fun setCameraImageFormatForQ(imageFormat: String?): PictureSelectionCameraModel {
        selectionConfig.cameraImageFormatForQ = imageFormat
        return this
    }

    /**
     * camera output video format
     *
     * @param videoFormat PictureSelector media format
     * @return
     */
    fun setCameraVideoFormat(videoFormat: String?): PictureSelectionCameraModel {
        selectionConfig.cameraVideoFormat = videoFormat
        return this
    }

    /**
     * camera output video format
     *
     * @param videoFormat PictureSelector media format
     * @return
     */
    fun setCameraVideoFormatForQ(videoFormat: String?): PictureSelectionCameraModel {
        selectionConfig.cameraVideoFormatForQ = videoFormat
        return this
    }

    /**
     * The max duration of video recording. If it is system recording, there may be compatibility problems
     *
     * @param maxSecond video record second
     * @return
     */
    fun setRecordVideoMaxSecond(maxSecond: Int): PictureSelectionCameraModel {
        selectionConfig.recordVideoMaxSecond = maxSecond
        return this
    }

    /**
     * @param minSecond video record second
     * @return
     */
    fun setRecordVideoMinSecond(minSecond: Int): PictureSelectionCameraModel {
        selectionConfig.recordVideoMinSecond = minSecond
        return this
    }

    /**
     * Select the max number of seconds for video or audio support
     *
     * @param maxDurationSecond select video max second
     * @return
     */
    fun setSelectMaxDurationSecond(maxDurationSecond: Int): PictureSelectionCameraModel {
        selectionConfig.selectMaxDurationSecond = maxDurationSecond * 1000
        return this
    }

    /**
     * Select the min number of seconds for video or audio support
     *
     * @param minDurationSecond select video min second
     * @return
     */
    fun setSelectMinDurationSecond(minDurationSecond: Int): PictureSelectionCameraModel {
        selectionConfig.selectMinDurationSecond = minDurationSecond * 1000
        return this
    }

    /**
     * @param outPutCameraDir Camera output path
     *
     * Audio mode setting is not supported
     * @return
     */
    fun setOutputCameraDir(outPutCameraDir: String?): PictureSelectionCameraModel {
        selectionConfig.outPutCameraDir = outPutCameraDir
        return this
    }

    /**
     * @param outPutAudioDir Audio output path
     * @return
     */
    fun setOutputAudioDir(outPutAudioDir: String?): PictureSelectionCameraModel {
        selectionConfig.outPutAudioDir = outPutAudioDir
        return this
    }

    /**
     * Camera IMAGE custom local file name
     * # Such as xxx.png
     *
     * @param fileName
     * @return
     */
    fun setOutputCameraImageFileName(fileName: String?): PictureSelectionCameraModel {
        selectionConfig.outPutCameraImageFileName = fileName
        return this
    }

    /**
     * Camera VIDEO custom local file name
     * # Such as xxx.png
     *
     * @param fileName
     * @return
     */
    fun setOutputCameraVideoFileName(fileName: String?): PictureSelectionCameraModel {
        selectionConfig.outPutCameraVideoFileName = fileName
        return this
    }

    /**
     * Camera VIDEO custom local file name
     * # Such as xxx.amr
     *
     * @param fileName
     * @return
     */
    fun setOutputAudioFileName(fileName: String?): PictureSelectionCameraModel {
        selectionConfig.outPutAudioFileName = fileName
        return this
    }

    /**
     * @param selectedList Select the selected picture set
     * @return
     */
    fun setSelectedData(selectedList: List<LocalMedia>?): PictureSelectionCameraModel {
        if (selectedList == null) {
            return this
        }
        setMaxSelectNum(selectedList.size + 1)
        setMaxVideoSelectNum(selectedList.size + 1)
        if (selectionConfig.selectionMode == SelectModeConfig.SINGLE && selectionConfig.isDirectReturnSingle) {
            selectionConfig.selectedResult.clear()
        } else {
            selectionConfig.addAllSelectResult(ArrayList(selectedList))
        }
        return this
    }

    /**
     * After recording with the system camera, does it support playing the video immediately using the system player
     *
     * @param isQuickCapture
     * @return
     */
    fun isQuickCapture(isQuickCapture: Boolean): PictureSelectionCameraModel {
        selectionConfig.isQuickCapture = isQuickCapture
        return this
    }

    /**
     * Set camera direction (after default image)
     */
    fun isCameraAroundState(isCameraAroundState: Boolean): PictureSelectionCameraModel {
        selectionConfig.isCameraAroundState = isCameraAroundState
        return this
    }

    /**
     * Camera image rotation, automatic correction
     */
    fun isCameraRotateImage(isCameraRotateImage: Boolean): PictureSelectionCameraModel {
        selectionConfig.isCameraRotateImage = isCameraRotateImage
        return this
    }

    /**
     * Start PictureSelector
     *
     *
     * The [IBridgePictureBehavior] interface needs to be
     * implemented in the activity or fragment you call to receive the returned results
     *
     *
     *
     * If the navigation component manages fragments,
     * it is recommended to use [] in openCamera mode
     *
     */
    fun forResult() {
        if (!DoubleUtils.isFastDoubleClick()) {
            val activity = selector.activity
                ?: throw NullPointerException("Activity cannot be null")
            selectionConfig.isResultListenerBack = false
            selectionConfig.isActivityResultBack = true
            var fragmentManager: FragmentManager? = null
            if (activity is FragmentActivity) {
                fragmentManager = activity.supportFragmentManager
            }
            if (fragmentManager == null) {
                throw NullPointerException("FragmentManager cannot be null")
            }
            if (activity !is IBridgePictureBehavior) {
                throw NullPointerException(
                    "Use only camera openCamera mode," +
                            "Activity or Fragment interface needs to be implemented " + IBridgePictureBehavior::class.java
                )
            }
            val fragment = fragmentManager.findFragmentByTag(PictureOnlyCameraFragment.TAG)
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
            }
            FragmentInjectManager.injectSystemRoomFragment(
                fragmentManager,
                PictureOnlyCameraFragment.TAG, PictureOnlyCameraFragment.newInstance()
            )
        }
    }

    /**
     * Start PictureSelector Camera
     *
     *
     * If the navigation component manages fragments,
     * it is recommended to use [] in openCamera mode
     *
     *
     * @param call
     */
    fun forResult(call: OnResultCallbackListener<LocalMedia?>?) {
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
            var fragmentManager: FragmentManager? = null
            if (activity is FragmentActivity) {
                fragmentManager = activity.supportFragmentManager
            }
            if (fragmentManager == null) {
                throw NullPointerException("FragmentManager cannot be null")
            }
            val fragment = fragmentManager.findFragmentByTag(PictureOnlyCameraFragment.TAG)
            if (fragment != null) {
                fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
            }
            FragmentInjectManager.injectSystemRoomFragment(
                fragmentManager,
                PictureOnlyCameraFragment.TAG, PictureOnlyCameraFragment.newInstance()
            )
        }
    }

    /**
     * build PictureOnlyCameraFragment
     *
     *
     * The [IBridgePictureBehavior] interface needs to be
     * implemented in the activity or fragment you call to receive the returned results
     *
     */
    fun build(): PictureOnlyCameraFragment {
        val activity = selector.activity
            ?: throw NullPointerException("Activity cannot be null")
        if (activity !is IBridgePictureBehavior) {
            throw NullPointerException(
                "Use only build PictureOnlyCameraFragment," +
                        "Activity or Fragment interface needs to be implemented " + IBridgePictureBehavior::class.java
            )
        }
        // 绑定回调监听
        selectionConfig.isResultListenerBack = false
        selectionConfig.isActivityResultBack = true
        selectionConfig.onResultCallListener = null
        return PictureOnlyCameraFragment()
    }

    /**
     * build and launch PictureSelector Camera
     *
     * @param containerViewId fragment container id
     * @param call
     */
    fun buildLaunch(
        containerViewId: Int,
        call: OnResultCallbackListener<LocalMedia?>?
    ): PictureOnlyCameraFragment {
        val activity = selector.activity
            ?: throw NullPointerException("Activity cannot be null")
        if (call == null) {
            throw NullPointerException("OnResultCallbackListener cannot be null")
        }
        // 绑定回调监听
        selectionConfig.isResultListenerBack = true
        selectionConfig.isActivityResultBack = false
        selectionConfig.onResultCallListener = call
        var fragmentManager: FragmentManager? = null
        if (activity is FragmentActivity) {
            fragmentManager = activity.supportFragmentManager
        }
        if (fragmentManager == null) {
            throw NullPointerException("FragmentManager cannot be null")
        }
        val onlyCameraFragment = PictureOnlyCameraFragment()
        val fragment = fragmentManager.findFragmentByTag(onlyCameraFragment.fragmentTag)
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
        fragmentManager.beginTransaction()
            .add(containerViewId, onlyCameraFragment, onlyCameraFragment.fragmentTag)
            .addToBackStack(onlyCameraFragment.fragmentTag)
            .commitAllowingStateLoss()
        return onlyCameraFragment
    }

    /**
     * Start PictureSelector
     *
     *
     * If you are in the Navigation Fragment scene, you must use this method
     *
     *
     * @param requestCode
     */
    fun forResultActivity(requestCode: Int) {
        if (!DoubleUtils.isFastDoubleClick()) {
            val activity = selector.activity
                ?: throw NullPointerException("Activity cannot be null")
            selectionConfig.isResultListenerBack = false
            selectionConfig.isActivityResultBack = true
            val intent = Intent(activity, PictureSelectorTransparentActivity::class.java)
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
     *
     * If you are in the Navigation Fragment scene, you must use this method
     *
     *
     * @param launcher use []
     */
    fun forResultActivity(launcher: ActivityResultLauncher<Intent?>?) {
        if (!DoubleUtils.isFastDoubleClick()) {
            val activity = selector.activity
                ?: throw NullPointerException("Activity cannot be null")
            if (launcher == null) {
                throw NullPointerException("ActivityResultLauncher cannot be null")
            }
            selectionConfig.isResultListenerBack = false
            selectionConfig.isActivityResultBack = true
            val intent = Intent(activity, PictureSelectorTransparentActivity::class.java)
            launcher.launch(intent)
            activity.overridePendingTransition(R.anim.ps_anim_fade_in, 0)
        }
    }

    /**
     * Start PictureSelector
     *
     *
     * If you are in the Navigation Fragment scene, you must use this method
     * >
     *
     * @param call
     */
    fun forResultActivity(call: OnResultCallbackListener<LocalMedia?>?) {
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
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.ps_anim_fade_in, 0)
        }
    }

    init {
        selectionConfig = SelectorConfig()
        SelectorProviders.instance.addSelectorConfigQueue(selectionConfig)
        selectionConfig.chooseMode = chooseMode
        selectionConfig.isOnlyCamera = true
        selectionConfig.isDisplayTimeAxis = false
        selectionConfig.isPreviewFullScreenMode = false
        selectionConfig.isPreviewZoomEffect = false
        selectionConfig.isOpenClickSound = false
    }
}