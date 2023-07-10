package com.luck.picture.lib.config

import android.content.pm.ActivityInfo
import com.luck.picture.lib.basic.IBridgeLoaderFactory
import com.luck.picture.lib.basic.IBridgeViewLifecycle
import com.luck.picture.lib.basic.InterpolatorFactory
import com.luck.picture.lib.config.SelectorConfig
import kotlin.jvm.Synchronized
import com.luck.picture.lib.utils.FileDirMap
import kotlin.jvm.Volatile
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.engine.*
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaFolder
import com.luck.picture.lib.interfaces.*
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.magical.BuildRecycleItemViewParams
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.thread.PictureThreadUtils
import com.luck.picture.lib.utils.SdkVersionUtils
import java.util.ArrayList

/**
 * @author：luck
 * @date：2017-05-24 17:02
 * @describe：PictureSelector Config
 */
class SelectorConfig {
    @JvmField
    var chooseMode = 0
    @JvmField
    var isOnlyCamera = false
    @JvmField
    var isDirectReturnSingle = false
    @JvmField
    var cameraImageFormat: String? = null
    @JvmField
    var cameraVideoFormat: String? = null
    @JvmField
    var cameraImageFormatForQ: String? = null
    @JvmField
    var cameraVideoFormatForQ: String? = null
    var requestedOrientation = 0
    var isCameraAroundState = false
    var selectionMode = 0
    @JvmField
    var maxSelectNum = 0
    var minSelectNum = 0
    var maxVideoSelectNum = 0
    var minVideoSelectNum = 0
    var minAudioSelectNum = 0
    var videoQuality = 0
    @JvmField
    var filterVideoMaxSecond = 0
    @JvmField
    var filterVideoMinSecond = 0
    var selectMaxDurationSecond = 0
    var selectMinDurationSecond = 0
    var recordVideoMaxSecond = 0
    var recordVideoMinSecond = 0
    var imageSpanCount = 0
    @JvmField
    var filterMaxFileSize: Long = 0
    @JvmField
    var filterMinFileSize: Long = 0
    var selectMaxFileSize: Long = 0
    var selectMinFileSize: Long = 0
    var language = 0
    var defaultLanguage = 0
    var isDisplayCamera = false
    @JvmField
    var isGif = false
    @JvmField
    var isWebp = false
    @JvmField
    var isBmp = false
    var isEnablePreviewImage = false
    var isEnablePreviewVideo = false
    var isEnablePreviewAudio = false
    @JvmField
    var isPreviewFullScreenMode = false
    var isPreviewZoomEffect = false
    var isOpenClickSound = false
    @JvmField
    var isEmptyResultReturn = false
    var isHidePreviewDownload = false
    var isWithVideoImage = false
    @JvmField
    var queryOnlyList: List<String>? = null
    var skipCropList: List<String>? = null
    @JvmField
    var isCheckOriginalImage = false
    @JvmField
    var outPutCameraImageFileName: String? = null
    @JvmField
    var outPutCameraVideoFileName: String? = null
    var outPutAudioFileName: String? = null
    @JvmField
    var outPutCameraDir: String? = null
    var outPutAudioDir: String? = null
    @JvmField
    var sandboxDir: String? = null
    var originalPath: String? = null
    @JvmField
    var cameraPath: String? = null
    @JvmField
    var sortOrder: String? = null
    @JvmField
    var defaultAlbumName: String? = null
    var pageSize = 0
    var isPageStrategy = false
    @JvmField
    var isFilterInvalidFile = false
    var isMaxSelectEnabledMask = false
    var animationMode = 0
    var isAutomaticTitleRecyclerTop = false
    var isQuickCapture = false
    var isCameraRotateImage = false
    var isAutoRotating = false
    @JvmField
    var isSyncCover = false
    var ofAllCameraType = 0
    @JvmField
    var isOnlySandboxDir = false
    var isCameraForegroundService = false
    var isResultListenerBack = false
    var isInjectLayoutResource = false
    var isActivityResultBack = false
    var isCompressEngine = false
    var isLoaderDataEngine = false
    var isLoaderFactoryEngine = false
    var isSandboxFileEngine = false
    @JvmField
    var isOriginalControl = false
    var isDisplayTimeAxis = false
    var isFastSlidingSelect = false
    var isSelectZoomAnim = false
    var isAutoVideoPlay = false
    @JvmField
    var isLoopAutoPlay = false
    @JvmField
    var isFilterSizeDuration = false
    @JvmField
    var isPageSyncAsCount = false
    var isPauseResumePlay = false
    var isSyncWidthAndHeight = false
    var isOriginalSkipCompress = false
    var isPreloadFirst = false
    var isUseSystemVideoPlayer = false
    @JvmField
    var selectorStyle: PictureSelectorStyle? = null
    private fun initDefaultValue() {
        chooseMode = SelectMimeType.ofImage()
        isOnlyCamera = false
        selectionMode = SelectModeConfig.MULTIPLE
        selectorStyle = PictureSelectorStyle()
        maxSelectNum = 9
        minSelectNum = 0
        maxVideoSelectNum = 1
        minVideoSelectNum = 0
        minAudioSelectNum = 0
        videoQuality = VideoQuality.VIDEO_QUALITY_HIGH
        language = LanguageConfig.UNKNOWN_LANGUAGE
        defaultLanguage = LanguageConfig.SYSTEM_LANGUAGE
        filterVideoMaxSecond = 0
        filterVideoMinSecond = 0
        selectMaxDurationSecond = 0
        selectMinDurationSecond = 0
        filterMaxFileSize = 0
        filterMinFileSize = 0
        selectMaxFileSize = 0
        selectMinFileSize = 0
        recordVideoMaxSecond = 60
        recordVideoMinSecond = 0
        imageSpanCount = PictureConfig.DEFAULT_SPAN_COUNT
        isCameraAroundState = false
        isWithVideoImage = false
        isDisplayCamera = true
        isGif = false
        isWebp = true
        isBmp = true
        isCheckOriginalImage = false
        isDirectReturnSingle = false
        isEnablePreviewImage = true
        isEnablePreviewVideo = true
        isEnablePreviewAudio = true
        isHidePreviewDownload = false
        isOpenClickSound = false
        isEmptyResultReturn = false
        cameraImageFormat = PictureMimeType.JPEG
        cameraVideoFormat = PictureMimeType.MP4
        cameraImageFormatForQ = PictureMimeType.MIME_TYPE_IMAGE
        cameraVideoFormatForQ = PictureMimeType.MIME_TYPE_VIDEO
        outPutCameraImageFileName = ""
        outPutCameraVideoFileName = ""
        outPutAudioFileName = ""
        queryOnlyList = ArrayList()
        outPutCameraDir = ""
        outPutAudioDir = ""
        sandboxDir = ""
        originalPath = ""
        cameraPath = ""
        pageSize = PictureConfig.MAX_PAGE_SIZE
        isPageStrategy = true
        isFilterInvalidFile = false
        isMaxSelectEnabledMask = false
        animationMode = -1
        isAutomaticTitleRecyclerTop = true
        isQuickCapture = true
        isCameraRotateImage = true
        isAutoRotating = true
        isSyncCover = !SdkVersionUtils.isQ()
        ofAllCameraType = SelectMimeType.ofAll()
        isOnlySandboxDir = false
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        isCameraForegroundService = false
        isResultListenerBack = true
        isActivityResultBack = false
        isCompressEngine = false
        isLoaderDataEngine = false
        isLoaderFactoryEngine = false
        isSandboxFileEngine = false
        isPreviewFullScreenMode = true
        isPreviewZoomEffect = chooseMode != SelectMimeType.ofAudio()
        isOriginalControl = false
        isInjectLayoutResource = false
        isDisplayTimeAxis = true
        isFastSlidingSelect = false
        skipCropList = ArrayList()
        sortOrder = ""
        isSelectZoomAnim = true
        defaultAlbumName = ""
        isAutoVideoPlay = false
        isLoopAutoPlay = false
        isFilterSizeDuration = true
        isPageSyncAsCount = false
        isPauseResumePlay = false
        isSyncWidthAndHeight = true
        isOriginalSkipCompress = false
        isPreloadFirst = true
        isUseSystemVideoPlayer = false
    }

    /**
     * Callback listening
     */
    var imageEngine: ImageEngine? = null
    var compressEngine: CompressEngine? = null
    var compressFileEngine: CompressFileEngine? = null
    var cropEngine: CropEngine? = null
    var cropFileEngine: CropFileEngine? = null
    var sandboxFileEngine: SandboxFileEngine? = null
    var uriToFileTransformEngine: UriToFileTransformEngine? = null
    var loaderDataEngine: ExtendLoaderEngine? = null
    var videoPlayerEngine: VideoPlayerEngine<*>? = null
    var viewLifecycle: IBridgeViewLifecycle? = null
    var loaderFactory: IBridgeLoaderFactory? = null
    @JvmField
    var interpolatorFactory: InterpolatorFactory? = null
    var onCameraInterceptListener: OnCameraInterceptListener? = null
    var onSelectLimitTipsListener: OnSelectLimitTipsListener? = null
    var onResultCallListener: OnResultCallbackListener<LocalMedia>? = null
    var onExternalPreviewEventListener: OnExternalPreviewEventListener? = null
    var onInjectActivityPreviewListener: OnInjectActivityPreviewListener? = null
    @JvmField
    var onEditMediaEventListener: OnMediaEditInterceptListener? = null
    var onPermissionsEventListener: OnPermissionsInterceptListener? = null
    var onLayoutResourceListener: OnInjectLayoutResourceListener? = null
    var onPreviewInterceptListener: OnPreviewInterceptListener? = null
    var onSelectFilterListener: OnSelectFilterListener? = null
    var onPermissionDescriptionListener: OnPermissionDescriptionListener? = null
    var onPermissionDeniedListener: OnPermissionDeniedListener? = null
    var onRecordAudioListener: OnRecordAudioInterceptListener? = null
    @JvmField
    var onQueryFilterListener: OnQueryFilterListener? = null
    var onBitmapWatermarkListener: OnBitmapWatermarkEventListener? = null
    var onVideoThumbnailEventListener: OnVideoThumbnailEventListener? = null
    var onItemSelectAnimListener: OnGridItemSelectAnimListener? = null
    @JvmField
    var onSelectAnimListener: OnSelectAnimListener? = null
    var onCustomLoadingListener: OnCustomLoadingListener? = null

    /**
     * selected current album folder
     */
    var currentLocalMediaFolder: LocalMediaFolder? = null

    /**
     * selected result
     */
    @get:Synchronized
    val selectedResult = ArrayList<LocalMedia>()
    val selectCount: Int
        get() = selectedResult.size

    fun addSelectResult(media: LocalMedia) {
        selectedResult.add(media)
    }

    fun addAllSelectResult(result: ArrayList<LocalMedia>?) {
        selectedResult.addAll(result!!)
    }

    val resultFirstMimeType: String
        get() = if (selectedResult.size > 0) selectedResult[0].mimeType else ""

    /**
     * selected preview result
     */
    val selectedPreviewResult = ArrayList<LocalMedia>()
    fun addSelectedPreviewResult(list: ArrayList<LocalMedia>?) {
        if (list != null) {
            selectedPreviewResult.clear()
            selectedPreviewResult.addAll(list)
        }
    }

    /**
     * all album data source
     */
    val albumDataSource = ArrayList<LocalMediaFolder>()
    fun addAlbumDataSource(list: List<LocalMediaFolder>?) {
        if (list != null) {
            albumDataSource.clear()
            albumDataSource.addAll(list)
        }
    }

    /**
     * all data source
     */
    val dataSource = ArrayList<LocalMedia>()
    fun addDataSource(list: ArrayList<LocalMedia>?) {
        if (list != null) {
            dataSource.clear()
            dataSource.addAll(list)
        }
    }

    /**
     * 释放监听器
     */
    fun destroy() {
        imageEngine = null
        compressEngine = null
        compressFileEngine = null
        cropEngine = null
        cropFileEngine = null
        sandboxFileEngine = null
        uriToFileTransformEngine = null
        loaderDataEngine = null
        onResultCallListener = null
        onCameraInterceptListener = null
        onExternalPreviewEventListener = null
        onInjectActivityPreviewListener = null
        onEditMediaEventListener = null
        onPermissionsEventListener = null
        onLayoutResourceListener = null
        onPreviewInterceptListener = null
        onSelectLimitTipsListener = null
        onSelectFilterListener = null
        onPermissionDescriptionListener = null
        onPermissionDeniedListener = null
        onRecordAudioListener = null
        onQueryFilterListener = null
        onBitmapWatermarkListener = null
        onVideoThumbnailEventListener = null
        viewLifecycle = null
        loaderFactory = null
        interpolatorFactory = null
        onItemSelectAnimListener = null
        onSelectAnimListener = null
        videoPlayerEngine = null
        onCustomLoadingListener = null
        currentLocalMediaFolder = null
        dataSource.clear()
        selectedResult.clear()
        albumDataSource.clear()
        selectedPreviewResult.clear()
        PictureThreadUtils.cancel(PictureThreadUtils.getIoPool())
        BuildRecycleItemViewParams.clear()
        FileDirMap.clear()
        LocalMedia.destroyPool()
    }

    init {
        initDefaultValue()
    }
}