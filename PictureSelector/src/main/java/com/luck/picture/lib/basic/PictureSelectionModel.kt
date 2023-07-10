package com.luck.picture.lib.basic

import android.content.Intent
import android.text.TextUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.luck.picture.lib.PictureSelectorFragment
import com.luck.picture.lib.R
import com.luck.picture.lib.config.*
import com.luck.picture.lib.engine.*
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.*
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.utils.DoubleUtils
import com.luck.picture.lib.utils.SdkVersionUtils
import java.lang.NullPointerException
import java.util.*

/**
 * @author：luck
 * @date：2017-5-24 21:30
 * @describe：PictureSelectionModel
 */
class PictureSelectionModel(private val selector: PictureSelector, chooseMode: Int) {
    private val selectionConfig: SelectorConfig

    /**
     * PictureSelector theme style settings
     *
     * @param uiStyle
     *
     *
     * Use [                It consists of the following parts and can be set separately][PictureSelectorStyle]
     * [com.luck.picture.lib.style.TitleBarStyle]
     * [com.luck.picture.lib.style.AlbumWindowStyle]
     * [com.luck.picture.lib.style.SelectMainStyle]
     * [com.luck.picture.lib.style.BottomNavBarStyle]
     * [com.luck.picture.lib.style.PictureWindowAnimationStyle]
     *
     *
     * @return PictureSelectorStyle
     */
    fun setSelectorUIStyle(uiStyle: PictureSelectorStyle?): PictureSelectionModel {
        if (uiStyle != null) {
            selectionConfig.selectorStyle = uiStyle
        }
        return this
    }

    /**
     * Set App Language
     *
     * @param language [LanguageConfig]
     * @return PictureSelectionModel
     */
    fun setLanguage(language: Int): PictureSelectionModel {
        selectionConfig.language = language
        return this
    }

    /**
     * Set App default Language
     *
     * @param defaultLanguage default language [LanguageConfig]
     * @return PictureSelectionModel
     */
    fun setDefaultLanguage(defaultLanguage: Int): PictureSelectionModel {
        selectionConfig.defaultLanguage = defaultLanguage
        return this
    }

    /**
     * Image Load the engine
     *
     * @param engine Image Load the engine
     *
     *
     * [
](https://github.com/LuckSiege/PictureSelector/blob/version_component/app/src/main/java/com/luck/pictureselector/GlideEngine.java) *
     * @return
     */
    fun setImageEngine(engine: ImageEngine?): PictureSelectionModel {
        selectionConfig.imageEngine = engine
        return this
    }

    /**
     * Set up player engine
     *
     *
     * Used to preview custom player instances，MediaPlayer by default
     *
     * @param engine
     * @return
     */
    fun setVideoPlayerEngine(engine: VideoPlayerEngine<*>?): PictureSelectionModel {
        selectionConfig.videoPlayerEngine = engine
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
    fun setCompressEngine(engine: CompressEngine?): PictureSelectionModel {
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
    fun setCompressEngine(engine: CompressFileEngine?): PictureSelectionModel {
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
    fun setCropEngine(engine: CropEngine?): PictureSelectionModel {
        selectionConfig.cropEngine = engine
        return this
    }

    /**
     * Image Crop the engine
     *
     * @param engine Image Crop the engine
     * @return
     */
    fun setCropEngine(engine: CropFileEngine?): PictureSelectionModel {
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
    fun setSandboxFileEngine(engine: SandboxFileEngine?): PictureSelectionModel {
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
    fun setSandboxFileEngine(engine: UriToFileTransformEngine?): PictureSelectionModel {
        if (SdkVersionUtils.isQ()) {
            selectionConfig.uriToFileTransformEngine = engine
            selectionConfig.isSandboxFileEngine = true
        } else {
            selectionConfig.isSandboxFileEngine = false
        }
        return this
    }

    /**
     * Users can implement some interfaces to access their own query data
     * The premise is that you need to comply with the model specification of PictureSelector
     * [ExtendLoaderEngine]
     * [LocalMediaFolder]
     * [LocalMedia]
     *
     *
     * Use [;][..setLoaderFactoryEngine]
     *
     *
     * @param engine
     * @return
     */
    @Deprecated("")
    fun setExtendLoaderEngine(engine: ExtendLoaderEngine?): PictureSelectionModel {
        selectionConfig.loaderDataEngine = engine
        selectionConfig.isLoaderDataEngine = true
        return this
    }

    /**
     * Users can implement some interfaces to access their own query data
     * The premise is that you need to comply with the model specification of PictureSelector
     * [IBridgeLoaderFactory]
     * [LocalMediaFolder]
     * [LocalMedia]
     *
     * @param engine
     * @return
     */
    fun setLoaderFactoryEngine(loaderFactory: IBridgeLoaderFactory?): PictureSelectionModel {
        selectionConfig.loaderFactory = loaderFactory
        selectionConfig.isLoaderFactoryEngine = true
        return this
    }

    /**
     * An interpolator defines the rate of change of an animation.
     * This allows the basic animation effects (alpha, scale, translate, rotate) to be accelerated, decelerated, repeated, etc.
     * Use [ ][]
     */
    fun setMagicalEffectInterpolator(interpolatorFactory: InterpolatorFactory?): PictureSelectionModel {
        selectionConfig.interpolatorFactory = interpolatorFactory
        return this
    }

    /**
     * Intercept camera click events, and users can implement their own camera framework
     *
     * @param listener
     * @return
     */
    fun setCameraInterceptListener(listener: OnCameraInterceptListener?): PictureSelectionModel {
        selectionConfig.onCameraInterceptListener = listener
        return this
    }

    /**
     * Intercept Record Audio click events, and users can implement their own Record Audio framework
     *
     * @param listener
     * @return
     */
    fun setRecordAudioInterceptListener(listener: OnRecordAudioInterceptListener?): PictureSelectionModel {
        selectionConfig.onRecordAudioListener = listener
        return this
    }

    /**
     * Intercept preview click events, and users can implement their own preview framework
     *
     * @param listener
     * @return
     */
    fun setPreviewInterceptListener(listener: OnPreviewInterceptListener?): PictureSelectionModel {
        selectionConfig.onPreviewInterceptListener = listener
        return this
    }

    /**
     * Intercept custom inject layout events, Users can implement their own layout
     * on the premise that the view ID must be consistent
     *
     * @param listener
     * @return
     */
    fun setInjectLayoutResourceListener(listener: OnInjectLayoutResourceListener?): PictureSelectionModel {
        selectionConfig.isInjectLayoutResource = listener != null
        selectionConfig.onLayoutResourceListener = listener
        return this
    }

    /**
     * Intercept media edit click events, and users can implement their own edit media framework
     *
     * @param listener
     * @return
     */
    fun setEditMediaInterceptListener(listener: OnMediaEditInterceptListener?): PictureSelectionModel {
        selectionConfig.onEditMediaEventListener = listener
        return this
    }

    /**
     * Custom interception permission processing
     *
     * @param listener
     * @return
     */
    fun setPermissionsInterceptListener(listener: OnPermissionsInterceptListener?): PictureSelectionModel {
        selectionConfig.onPermissionsEventListener = listener
        return this
    }

    /**
     * permission description
     *
     * @param listener
     * @return
     */
    fun setPermissionDescriptionListener(listener: OnPermissionDescriptionListener?): PictureSelectionModel {
        selectionConfig.onPermissionDescriptionListener = listener
        return this
    }

    /**
     * Permission denied
     *
     * @param listener
     * @return
     */
    fun setPermissionDeniedListener(listener: OnPermissionDeniedListener?): PictureSelectionModel {
        selectionConfig.onPermissionDeniedListener = listener
        return this
    }

    /**
     * Custom limit tips
     *
     * @param listener
     */
    fun setSelectLimitTipsListener(listener: OnSelectLimitTipsListener?): PictureSelectionModel {
        selectionConfig.onSelectLimitTipsListener = listener
        return this
    }

    /**
     * You need to filter out the content that does not meet the selection criteria
     *
     * @param listener
     * @return
     */
    fun setSelectFilterListener(listener: OnSelectFilterListener?): PictureSelectionModel {
        selectionConfig.onSelectFilterListener = listener
        return this
    }

    /**
     * You need to filter out what doesn't meet the standards
     *
     * @param listener
     * @return
     */
    fun setQueryFilterListener(listener: OnQueryFilterListener?): PictureSelectionModel {
        selectionConfig.onQueryFilterListener = listener
        return this
    }

    /**
     * Animate the selected item in the list
     *
     * @param listener
     * @return
     */
    fun setGridItemSelectAnimListener(listener: OnGridItemSelectAnimListener?): PictureSelectionModel {
        selectionConfig.onItemSelectAnimListener = listener
        return this
    }

    /**
     * Animate the selected item
     *
     * @param listener
     * @return
     */
    fun setSelectAnimListener(listener: OnSelectAnimListener?): PictureSelectionModel {
        selectionConfig.onSelectAnimListener = listener
        return this
    }

    /**
     * You can add a watermark to the image
     *
     * @param listener
     * @return
     */
    fun setAddBitmapWatermarkListener(listener: OnBitmapWatermarkEventListener?): PictureSelectionModel {
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
    fun setVideoThumbnailListener(listener: OnVideoThumbnailEventListener?): PictureSelectionModel {
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
    fun setCustomLoadingListener(listener: OnCustomLoadingListener?): PictureSelectionModel {
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
    fun isCameraForegroundService(isForeground: Boolean): PictureSelectionModel {
        selectionConfig.isCameraForegroundService = isForeground
        return this
    }

    /**
     * Android 10 preloads data first, then asynchronously obtains album list
     *
     *
     * Please consult the developer for detailed reasons
     *
     *
     * @param isPreloadFirst Enable preload by default
     */
    fun isPreloadFirst(isPreloadFirst: Boolean): PictureSelectionModel {
        selectionConfig.isPreloadFirst = isPreloadFirst
        return this
    }

    /**
     * Using the system player
     *
     * @param isUseSystemVideoPlayer
     */
    fun isUseSystemVideoPlayer(isUseSystemVideoPlayer: Boolean): PictureSelectionModel {
        selectionConfig.isUseSystemVideoPlayer = isUseSystemVideoPlayer
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
    fun setRequestedOrientation(requestedOrientation: Int): PictureSelectionModel {
        selectionConfig.requestedOrientation = requestedOrientation
        return this
    }

    /**
     * @param selectionMode PictureSelector Selection model
     * and [SelectModeConfig.MULTIPLE] or [SelectModeConfig.SINGLE]
     *
     *
     * Use [SelectModeConfig]
     *
     * @return
     */
    fun setSelectionMode(selectionMode: Int): PictureSelectionModel {
        selectionConfig.selectionMode = selectionMode
        selectionConfig.maxSelectNum = if (selectionConfig.selectionMode ==
            SelectModeConfig.SINGLE
        ) 1 else selectionConfig.maxSelectNum
        return this
    }

    /**
     * You can select pictures and videos at the same time
     *
     * @param isWithVideoImage Whether the pictures and videos can be selected together
     * @return
     */
    fun isWithSelectVideoImage(isWithVideoImage: Boolean): PictureSelectionModel {
        selectionConfig.isWithVideoImage =
            selectionConfig.chooseMode == SelectMimeType.ofAll() && isWithVideoImage
        return this
    }

    /**
     * Choose between photographing and shooting in ofAll mode
     *
     * @param ofAllCameraType [or SelectMimeType.ofVideo][SelectMimeType.ofImage]
     * The default is ofAll() mode
     * @return
     */
    fun setOfAllCameraType(ofAllCameraType: Int): PictureSelectionModel {
        selectionConfig.ofAllCameraType = ofAllCameraType
        return this
    }

    /**
     * When the maximum number of choices is reached, does the list enable the mask effect
     *
     * @param isMaxSelectEnabledMask
     * @return
     */
    fun isMaxSelectEnabledMask(isMaxSelectEnabledMask: Boolean): PictureSelectionModel {
        selectionConfig.isMaxSelectEnabledMask = isMaxSelectEnabledMask
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
    fun isOriginalControl(isOriginalControl: Boolean): PictureSelectionModel {
        selectionConfig.isOriginalControl = isOriginalControl
        return this
    }

    /**
     * If SyncCover
     *
     * @param isSyncCover
     * @return
     */
    fun isSyncCover(isSyncCover: Boolean): PictureSelectionModel {
        selectionConfig.isSyncCover = isSyncCover
        return this
    }

    /**
     * Select the maximum number of files
     *
     * @param maxSelectNum PictureSelector max selection
     * @return
     */
    fun setMaxSelectNum(maxSelectNum: Int): PictureSelectionModel {
        selectionConfig.maxSelectNum =
            if (selectionConfig.selectionMode == SelectModeConfig.SINGLE) 1 else maxSelectNum
        return this
    }

    /**
     * Select the minimum number of files
     *
     * @param minSelectNum PictureSelector min selection
     * @return
     */
    fun setMinSelectNum(minSelectNum: Int): PictureSelectionModel {
        selectionConfig.minSelectNum = minSelectNum
        return this
    }

    /**
     * By clicking the title bar consecutively, RecyclerView automatically rolls back to the top
     *
     * @param isAutomaticTitleRecyclerTop
     * @return
     */
    fun isAutomaticTitleRecyclerTop(isAutomaticTitleRecyclerTop: Boolean): PictureSelectionModel {
        selectionConfig.isAutomaticTitleRecyclerTop = isAutomaticTitleRecyclerTop
        return this
    }

    /**
     * @param Select whether to return directly
     * @return
     */
    fun isDirectReturnSingle(isDirectReturn: Boolean): PictureSelectionModel {
        if (isDirectReturn) {
            selectionConfig.isFastSlidingSelect = false
        }
        selectionConfig.isDirectReturnSingle =
            selectionConfig.selectionMode == SelectModeConfig.SINGLE && isDirectReturn
        return this
    }

    /**
     * Whether to turn on paging mode
     *
     * @param isPageStrategy
     * @return
     */
    fun isPageStrategy(isPageStrategy: Boolean): PictureSelectionModel {
        selectionConfig.isPageStrategy = isPageStrategy
        return this
    }

    /**
     * Whether to turn on paging mode
     *
     * @param isPageStrategy
     * @param pageSize       Maximum number of pages [is preferably no less than 20][PageSize]
     * @return
     */
    fun isPageStrategy(isPageStrategy: Boolean, pageSize: Int): PictureSelectionModel {
        selectionConfig.isPageStrategy = isPageStrategy
        selectionConfig.pageSize =
            if (pageSize < PictureConfig.MIN_PAGE_SIZE) PictureConfig.MAX_PAGE_SIZE else pageSize
        return this
    }

    /**
     * Whether to turn on paging mode
     *
     * @param isPageStrategy
     * @param isFilterInvalidFile Whether to filter invalid files [of the query performance is consumed,Especially on the Q version][Some]
     * @return
     */
    @Deprecated("")
    fun isPageStrategy(
        isPageStrategy: Boolean,
        isFilterInvalidFile: Boolean
    ): PictureSelectionModel {
        selectionConfig.isPageStrategy = isPageStrategy
        selectionConfig.isFilterInvalidFile = isFilterInvalidFile
        return this
    }

    /**
     * Whether to turn on paging mode
     *
     * @param isPageStrategy
     * @param pageSize            Maximum number of pages [is preferably no less than 20][PageSize]
     * @param isFilterInvalidFile Whether to filter invalid files [of the query performance is consumed,Especially on the Q version][Some]
     * @return
     */
    @Deprecated("")
    fun isPageStrategy(
        isPageStrategy: Boolean,
        pageSize: Int,
        isFilterInvalidFile: Boolean
    ): PictureSelectionModel {
        selectionConfig.isPageStrategy = isPageStrategy
        selectionConfig.pageSize =
            if (pageSize < PictureConfig.MIN_PAGE_SIZE) PictureConfig.MAX_PAGE_SIZE else pageSize
        selectionConfig.isFilterInvalidFile = isFilterInvalidFile
        return this
    }

    /**
     * View lifecycle listener
     *
     * @param viewLifecycle
     * @return
     */
    fun setAttachViewLifecycle(viewLifecycle: IBridgeViewLifecycle?): PictureSelectionModel {
        selectionConfig.viewLifecycle = viewLifecycle
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
    fun setVideoQuality(videoQuality: Int): PictureSelectionModel {
        selectionConfig.videoQuality = videoQuality
        return this
    }

    /**
     * Set the first default album name
     *
     * @param defaultAlbumName
     * @return
     */
    fun setDefaultAlbumName(defaultAlbumName: String?): PictureSelectionModel {
        selectionConfig.defaultAlbumName = defaultAlbumName
        return this
    }

    /**
     * camera output image format
     *
     * @param imageFormat PictureSelector media format
     * @return
     */
    fun setCameraImageFormat(imageFormat: String?): PictureSelectionModel {
        selectionConfig.cameraImageFormat = imageFormat
        return this
    }

    /**
     * camera output image format
     *
     * @param imageFormat PictureSelector media format
     * @return
     */
    fun setCameraImageFormatForQ(imageFormat: String?): PictureSelectionModel {
        selectionConfig.cameraImageFormatForQ = imageFormat
        return this
    }

    /**
     * camera output video format
     *
     * @param videoFormat PictureSelector media format
     * @return
     */
    fun setCameraVideoFormat(videoFormat: String?): PictureSelectionModel {
        selectionConfig.cameraVideoFormat = videoFormat
        return this
    }

    /**
     * camera output video format
     *
     * @param videoFormat PictureSelector media format
     * @return
     */
    fun setCameraVideoFormatForQ(videoFormat: String?): PictureSelectionModel {
        selectionConfig.cameraVideoFormatForQ = videoFormat
        return this
    }

    /**
     * filter max seconds video
     *
     * @param videoMaxSecond filter video max second
     * @return
     */
    fun setFilterVideoMaxSecond(videoMaxSecond: Int): PictureSelectionModel {
        selectionConfig.filterVideoMaxSecond = videoMaxSecond * 1000
        return this
    }

    /**
     * filter min seconds video
     *
     * @param videoMinSecond filter video min second
     * @return
     */
    fun setFilterVideoMinSecond(videoMinSecond: Int): PictureSelectionModel {
        selectionConfig.filterVideoMinSecond = videoMinSecond * 1000
        return this
    }

    /**
     * Select the max number of seconds for video or audio support
     *
     * @param maxDurationSecond select video max second
     * @return
     */
    fun setSelectMaxDurationSecond(maxDurationSecond: Int): PictureSelectionModel {
        selectionConfig.selectMaxDurationSecond = maxDurationSecond * 1000
        return this
    }

    /**
     * Select the min number of seconds for video or audio support
     *
     * @param minDurationSecond select video min second
     * @return
     */
    fun setSelectMinDurationSecond(minDurationSecond: Int): PictureSelectionModel {
        selectionConfig.selectMinDurationSecond = minDurationSecond * 1000
        return this
    }

    /**
     * The max duration of video recording. If it is system recording, there may be compatibility problems
     *
     * @param maxSecond video record second
     * @return
     */
    fun setRecordVideoMaxSecond(maxSecond: Int): PictureSelectionModel {
        selectionConfig.recordVideoMaxSecond = maxSecond
        return this
    }

    /**
     * Select the maximum video number of files
     *
     * @param maxVideoSelectNum PictureSelector video max selection
     * @return
     */
    fun setMaxVideoSelectNum(maxVideoSelectNum: Int): PictureSelectionModel {
        selectionConfig.maxVideoSelectNum =
            if (selectionConfig.chooseMode == SelectMimeType.ofVideo()) 0 else maxVideoSelectNum
        return this
    }

    /**
     * Select the minimum video number of files
     *
     * @param minVideoSelectNum PictureSelector video min selection
     * @return
     */
    fun setMinVideoSelectNum(minVideoSelectNum: Int): PictureSelectionModel {
        selectionConfig.minVideoSelectNum = minVideoSelectNum
        return this
    }

    /**
     * Select the minimum audio number of files
     *
     * @param minAudioSelectNum PictureSelector audio min selection
     * @return
     */
    fun setMinAudioSelectNum(minAudioSelectNum: Int): PictureSelectionModel {
        selectionConfig.minAudioSelectNum = minAudioSelectNum
        return this
    }

    /**
     * @param minSecond video record second
     * @return
     */
    fun setRecordVideoMinSecond(minSecond: Int): PictureSelectionModel {
        selectionConfig.recordVideoMinSecond = minSecond
        return this
    }

    /**
     * @param imageSpanCount PictureSelector image span count
     * @return
     */
    fun setImageSpanCount(imageSpanCount: Int): PictureSelectionModel {
        selectionConfig.imageSpanCount = imageSpanCount
        return this
    }

    /**
     * @param isEmptyReturn No data can be returned
     * @return
     */
    fun isEmptyResultReturn(isEmptyReturn: Boolean): PictureSelectionModel {
        selectionConfig.isEmptyResultReturn = isEmptyReturn
        return this
    }

    /**
     * After recording with the system camera, does it support playing the video immediately using the system player
     *
     * @param isQuickCapture
     * @return
     */
    fun isQuickCapture(isQuickCapture: Boolean): PictureSelectionModel {
        selectionConfig.isQuickCapture = isQuickCapture
        return this
    }

    /**
     * @param isDisplayCamera Whether to open camera button
     * @return
     */
    fun isDisplayCamera(isDisplayCamera: Boolean): PictureSelectionModel {
        selectionConfig.isDisplayCamera = isDisplayCamera
        return this
    }

    /**
     * @param outPutCameraDir Camera output path
     *
     * Audio mode setting is not supported
     * @return
     */
    fun setOutputCameraDir(outPutCameraDir: String?): PictureSelectionModel {
        selectionConfig.outPutCameraDir = outPutCameraDir
        return this
    }

    /**
     * @param outPutAudioDir Audio output path
     * @return
     */
    fun setOutputAudioDir(outPutAudioDir: String?): PictureSelectionModel {
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
    fun setOutputCameraImageFileName(fileName: String?): PictureSelectionModel {
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
    fun setOutputCameraVideoFileName(fileName: String?): PictureSelectionModel {
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
    fun setOutputAudioFileName(fileName: String?): PictureSelectionModel {
        selectionConfig.outPutAudioFileName = fileName
        return this
    }

    /**
     * Query the pictures or videos in the specified directory
     *
     * @param dir Camera out path
     *
     *
     * Normally, it should be consistent with [];
     *
     *
     *
     *
     * If build.version.sdk_INT < 29,[;][]
     * Do not set the external storage path,
     * which may cause the problem of picture duplication
     *
     * @return
     */
    fun setQuerySandboxDir(dir: String?): PictureSelectionModel {
        selectionConfig.sandboxDir = dir
        return this
    }

    /**
     * Only the resources in the specified directory are displayed
     *
     *
     * Only Display setQuerySandboxDir();  Source
     *
     *
     *
     * @param isOnlySandboxDir true or Only Display [;][]
     * @return
     */
    fun isOnlyObtainSandboxDir(isOnlySandboxDir: Boolean): PictureSelectionModel {
        selectionConfig.isOnlySandboxDir = isOnlySandboxDir
        return this
    }

    /**
     * Displays the creation timeline of the resource
     *
     * @param isDisplayTimeAxis
     * @return
     */
    fun isDisplayTimeAxis(isDisplayTimeAxis: Boolean): PictureSelectionModel {
        selectionConfig.isDisplayTimeAxis = isDisplayTimeAxis
        return this
    }

    /**
     * # file size The unit is KB
     *
     * @param fileKbSize Filter max file size
     * @return
     */
    fun setFilterMaxFileSize(fileKbSize: Long): PictureSelectionModel {
        if (fileKbSize >= FileSizeUnit.MB) {
            selectionConfig.filterMaxFileSize = fileKbSize
        } else {
            selectionConfig.filterMaxFileSize = fileKbSize * FileSizeUnit.KB
        }
        return this
    }

    /**
     * # file size The unit is KB
     *
     * @param fileKbSize Filter min file size
     * @return
     */
    fun setFilterMinFileSize(fileKbSize: Long): PictureSelectionModel {
        if (fileKbSize >= FileSizeUnit.MB) {
            selectionConfig.filterMinFileSize = fileKbSize
        } else {
            selectionConfig.filterMinFileSize = fileKbSize * FileSizeUnit.KB
        }
        return this
    }

    /**
     * # file size The unit is KB
     *
     * @param fileKbSize Filter max file size
     * @return
     */
    fun setSelectMaxFileSize(fileKbSize: Long): PictureSelectionModel {
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
    fun setSelectMinFileSize(fileKbSize: Long): PictureSelectionModel {
        if (fileKbSize >= FileSizeUnit.MB) {
            selectionConfig.selectMinFileSize = fileKbSize
        } else {
            selectionConfig.selectMinFileSize = fileKbSize * FileSizeUnit.KB
        }
        return this
    }

    /**
     * query only mimeType
     *
     * @param mimeTypes Use example [{]
     * @return
     */
    fun setQueryOnlyMimeType(vararg mimeTypes: String?): PictureSelectionModel {
        if (mimeTypes != null && mimeTypes.size > 0) {
            selectionConfig.queryOnlyList.addAll(Arrays.asList(*mimeTypes))
        }
        return this
    }

    /**
     * Skip crop mimeType
     *
     * @param mimeTypes Use example [{]
     * @return
     */
    fun setSkipCropMimeType(vararg mimeTypes: String?): PictureSelectionModel {
        if (mimeTypes != null && mimeTypes.size > 0) {
            selectionConfig.skipCropList.addAll(Arrays.asList(*mimeTypes))
        }
        return this
    }

    /**
     * query local data source sort
     * [# DATE_ADDED # _ID][MediaStore.MediaColumns.DATE_MODIFIED]
     *
     *
     * example:
     * MediaStore.MediaColumns.DATE_MODIFIED + " DESC";  or MediaStore.MediaColumns.DATE_MODIFIED + " ASC";
     *
     *
     * @param sortOrder
     * @return
     */
    fun setQuerySortOrder(sortOrder: String?): PictureSelectionModel {
        if (!TextUtils.isEmpty(sortOrder)) {
            selectionConfig.sortOrder = sortOrder
        }
        return this
    }

    /**
     * @param isGif Whether to open gif
     * @return
     */
    fun isGif(isGif: Boolean): PictureSelectionModel {
        selectionConfig.isGif = isGif
        return this
    }

    /**
     * @param isWebp Whether to open .webp
     * @return
     */
    fun isWebp(isWebp: Boolean): PictureSelectionModel {
        selectionConfig.isWebp = isWebp
        return this
    }

    /**
     * @param isBmp Whether to open .isBmp
     * @return
     */
    fun isBmp(isBmp: Boolean): PictureSelectionModel {
        selectionConfig.isBmp = isBmp
        return this
    }

    /**
     * Preview Full Screen Mode
     *
     * @param isFullScreenModel
     * @return
     */
    fun isPreviewFullScreenMode(isFullScreenModel: Boolean): PictureSelectionModel {
        selectionConfig.isPreviewFullScreenMode = isFullScreenModel
        return this
    }

    /**
     * Preview Zoom Effect Mode
     *
     * @return
     */
    fun isPreviewZoomEffect(isPreviewZoomEffect: Boolean): PictureSelectionModel {
        if (selectionConfig.chooseMode == SelectMimeType.ofAudio()) {
            selectionConfig.isPreviewZoomEffect = false
        } else {
            selectionConfig.isPreviewZoomEffect = isPreviewZoomEffect
        }
        return this
    }

    /**
     * It is forbidden to correct or synchronize the width and height of the video
     *
     * @param isEnableVideoSize Use []
     */
    @Deprecated("")
    fun isEnableVideoSize(isEnableVideoSize: Boolean): PictureSelectionModel {
        selectionConfig.isSyncWidthAndHeight = isEnableVideoSize
        return this
    }

    /**
     * It is forbidden to correct or synchronize the width and height of the video
     *
     * @param isSyncWidthAndHeight
     * @return
     */
    fun isSyncWidthAndHeight(isSyncWidthAndHeight: Boolean): PictureSelectionModel {
        selectionConfig.isSyncWidthAndHeight = isSyncWidthAndHeight
        return this
    }

    /**
     * Do you want to preview play the audio file?
     *
     * @param isPreviewAudio
     * @return
     */
    fun isPreviewAudio(isPreviewAudio: Boolean): PictureSelectionModel {
        selectionConfig.isEnablePreviewAudio = isPreviewAudio
        return this
    }

    /**
     * @param isPreviewImage Do you want to preview the picture?
     * @return
     */
    fun isPreviewImage(isPreviewImage: Boolean): PictureSelectionModel {
        selectionConfig.isEnablePreviewImage = isPreviewImage
        return this
    }

    /**
     * @param isPreviewVideo Do you want to preview the video?
     * @return
     */
    fun isPreviewVideo(isPreviewVideo: Boolean): PictureSelectionModel {
        selectionConfig.isEnablePreviewVideo = isPreviewVideo
        return this
    }

    /**
     * Whether to play video automatically when previewing
     *
     * @param isAutoPlay
     * @return
     */
    fun isAutoVideoPlay(isAutoPlay: Boolean): PictureSelectionModel {
        selectionConfig.isAutoVideoPlay = isAutoPlay
        return this
    }

    /**
     * loop video
     *
     * @param isLoopAutoPlay
     * @return
     */
    fun isLoopAutoVideoPlay(isLoopAutoPlay: Boolean): PictureSelectionModel {
        selectionConfig.isLoopAutoPlay = isLoopAutoPlay
        return this
    }

    /**
     * The video supports pause and resume
     *
     * @param isPauseResumePlay
     * @return
     */
    fun isVideoPauseResumePlay(isPauseResumePlay: Boolean): PictureSelectionModel {
        selectionConfig.isPauseResumePlay = isPauseResumePlay
        return this
    }

    /**
     * Whether to sync the number of resources under the latest album in paging mode with filter conditions
     *
     * @param isPageSyncAsCount
     */
    fun isPageSyncAlbumCount(isPageSyncAsCount: Boolean): PictureSelectionModel {
        selectionConfig.isPageSyncAsCount = isPageSyncAsCount
        return this
    }

    /**
     * Select original image to skip compression
     *
     * @param isOriginalSkipCompress
     * @return
     */
    fun isOriginalSkipCompress(isOriginalSkipCompress: Boolean): PictureSelectionModel {
        selectionConfig.isOriginalSkipCompress = isOriginalSkipCompress
        return this
    }

    /**
     * Filter the validity of file size or duration of audio and video
     *
     * @param isFilterSizeDuration
     * @return
     */
    fun isFilterSizeDuration(isFilterSizeDuration: Boolean): PictureSelectionModel {
        selectionConfig.isFilterSizeDuration = isFilterSizeDuration
        return this
    }

    /**
     * Quick slide selection results
     *
     * @param isFastSlidingSelect
     * @return
     */
    fun isFastSlidingSelect(isFastSlidingSelect: Boolean): PictureSelectionModel {
        if (selectionConfig.isDirectReturnSingle) {
            selectionConfig.isFastSlidingSelect = false
        } else {
            selectionConfig.isFastSlidingSelect = isFastSlidingSelect
        }
        return this
    }

    /**
     * @param isClickSound Whether to open click voice
     * @return
     */
    fun isOpenClickSound(isClickSound: Boolean): PictureSelectionModel {
        selectionConfig.isOpenClickSound = isClickSound
        return this
    }

    /**
     * Set camera direction (after default image)
     */
    fun isCameraAroundState(isCameraAroundState: Boolean): PictureSelectionModel {
        selectionConfig.isCameraAroundState = isCameraAroundState
        return this
    }

    /**
     * Camera image rotation, automatic correction
     */
    fun isCameraRotateImage(isCameraRotateImage: Boolean): PictureSelectionModel {
        selectionConfig.isCameraRotateImage = isCameraRotateImage
        return this
    }

    /**
     * Zoom animation is required when selecting an asset
     */
    fun isSelectZoomAnim(isSelectZoomAnim: Boolean): PictureSelectionModel {
        selectionConfig.isSelectZoomAnim = isSelectZoomAnim
        return this
    }

    /**
     * @param selectedList Select the selected picture set
     * @return
     */
    fun setSelectedData(selectedList: List<LocalMedia>?): PictureSelectionModel {
        if (selectedList == null) {
            return this
        }
        if (selectionConfig.selectionMode == SelectModeConfig.SINGLE && selectionConfig.isDirectReturnSingle) {
            selectionConfig.selectedResult.clear()
        } else {
            selectionConfig.addAllSelectResult(ArrayList(selectedList))
        }
        return this
    }

    /**
     * Photo album list animation {}
     * Use [or SLIDE_IN_BOTTOM_ANIMATION][AnimationType.ALPHA_IN_ANIMATION] directly.
     *
     * @param animationMode
     * @return
     */
    fun setRecyclerAnimationMode(animationMode: Int): PictureSelectionModel {
        selectionConfig.animationMode = animationMode
        return this
    }

    /**
     * Start PictureSelector
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
            if (selectionConfig.imageEngine == null && selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
                throw NullPointerException("imageEngine is null,Please implement ImageEngine")
            }
            val intent = Intent(activity, PictureSelectorSupporterActivity::class.java)
            activity.startActivity(intent)
            val windowAnimationStyle = selectionConfig.selectorStyle.windowAnimationStyle
            activity.overridePendingTransition(
                windowAnimationStyle.activityEnterAnimation,
                R.anim.ps_anim_fade_in
            )
        }
    }

    /**
     * Start PictureSelector
     *
     * @param requestCode
     */
    fun forResult(requestCode: Int) {
        if (!DoubleUtils.isFastDoubleClick()) {
            val activity = selector.activity
                ?: throw NullPointerException("Activity cannot be null")
            selectionConfig.isResultListenerBack = false
            selectionConfig.isActivityResultBack = true
            if (selectionConfig.imageEngine == null && selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
                throw NullPointerException("imageEngine is null,Please implement ImageEngine")
            }
            val intent = Intent(activity, PictureSelectorSupporterActivity::class.java)
            val fragment = selector.fragment
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode)
            } else {
                activity.startActivityForResult(intent, requestCode)
            }
            val windowAnimationStyle = selectionConfig.selectorStyle.windowAnimationStyle
            activity.overridePendingTransition(
                windowAnimationStyle.activityEnterAnimation,
                R.anim.ps_anim_fade_in
            )
        }
    }

    /**
     * ActivityResultLauncher PictureSelector
     *
     * @param launcher use []
     */
    fun forResult(launcher: ActivityResultLauncher<Intent?>?) {
        if (!DoubleUtils.isFastDoubleClick()) {
            val activity = selector.activity
                ?: throw NullPointerException("Activity cannot be null")
            if (launcher == null) {
                throw NullPointerException("ActivityResultLauncher cannot be null")
            }
            selectionConfig.isResultListenerBack = false
            selectionConfig.isActivityResultBack = true
            if (selectionConfig.imageEngine == null && selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
                throw NullPointerException("imageEngine is null,Please implement ImageEngine")
            }
            val intent = Intent(activity, PictureSelectorSupporterActivity::class.java)
            launcher.launch(intent)
            val windowAnimationStyle = selectionConfig.selectorStyle.windowAnimationStyle
            activity.overridePendingTransition(
                windowAnimationStyle.activityEnterAnimation,
                R.anim.ps_anim_fade_in
            )
        }
    }

    /**
     * build PictureSelectorFragment
     *
     *
     * The [IBridgePictureBehavior] interface needs to be
     * implemented in the activity or fragment you call to receive the returned results
     *
     */
    fun build(): PictureSelectorFragment {
        val activity = selector.activity
            ?: throw NullPointerException("Activity cannot be null")
        if (activity !is IBridgePictureBehavior) {
            throw NullPointerException(
                "Use only build PictureSelectorFragment," +
                        "Activity or Fragment interface needs to be implemented " + IBridgePictureBehavior::class.java
            )
        }
        // 绑定回调监听
        selectionConfig.isResultListenerBack = false
        selectionConfig.isActivityResultBack = true
        selectionConfig.onResultCallListener = null
        return PictureSelectorFragment()
    }

    /**
     * build and launch PictureSelector
     *
     * @param containerViewId fragment container id
     * @param call
     */
    fun buildLaunch(
        containerViewId: Int,
        call: OnResultCallbackListener<LocalMedia?>?
    ): PictureSelectorFragment {
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
        val selectorFragment = PictureSelectorFragment()
        val fragment = fragmentManager.findFragmentByTag(selectorFragment.fragmentTag)
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
        fragmentManager.beginTransaction()
            .add(containerViewId, selectorFragment, selectorFragment.fragmentTag)
            .addToBackStack(selectorFragment.fragmentTag)
            .commitAllowingStateLoss()
        return selectorFragment
    }

    init {
        selectionConfig = SelectorConfig()
        SelectorProviders.instance.addSelectorConfigQueue(selectionConfig)
        selectionConfig.chooseMode = chooseMode
        setMaxVideoSelectNum(selectionConfig.maxVideoSelectNum)
    }
}