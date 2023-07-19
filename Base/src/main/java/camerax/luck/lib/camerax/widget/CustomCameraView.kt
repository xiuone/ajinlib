package camerax.luck.lib.camerax.widget

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.display.DisplayManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.video.OnVideoSavedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import camerax.luck.lib.camerax.CustomCameraConfig
import camerax.luck.lib.camerax.type.CustomCameraType
import camerax.luck.lib.camerax.SimpleCameraX
import camerax.luck.lib.camerax.listener.CameraListener
import camerax.luck.lib.camerax.listener.CaptureListener
import camerax.luck.lib.camerax.listener.ClickListener
import camerax.luck.lib.camerax.listener.ImageCallbackListener
import camerax.luck.lib.camerax.listener.OrientationEventListener
import camerax.luck.lib.camerax.listener.OrientationEventListener.OnOrientationChangedListener
import camerax.luck.lib.camerax.listener.PreviewViewTouchListener
import camerax.luck.lib.camerax.listener.PreviewViewTouchListener.CustomTouchListener
import camerax.luck.lib.camerax.listener.TypeListener
import camerax.luck.lib.camerax.utils.CameraUtils
import camerax.luck.lib.camerax.utils.FileUtils
import camerax.luck.lib.camerax.widget.capture.CaptureLayout
import camerax.luck.lib.camerax.widget.flash.FlashImageView
import camerax.luck.lib.camerax.widget.focus.FocusImageView
import com.hjq.permissions.XXPermissions
import xy.xy.base.R
import xy.xy.base.utils.exp.getResColor
import xy.xy.base.utils.exp.getScreenHeight
import xy.xy.base.utils.exp.getScreenWidth
import xy.xy.base.utils.exp.isContent
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.lang.ref.WeakReference
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * @author：luck
 * @date：2020-01-04 13:41
 * @describe：自定义相机View
 */
@SuppressLint("RestrictedApi", "UnsafeOptInUsageError", "MissingPermission")
class CustomCameraView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):
    RelativeLayout(context, attrs, defStyleAttr), OnOrientationChangedListener {
    private val mCameraPreviewView by lazy { findViewById<PreviewView>(R.id.cameraPreviewView) }
    private var mCameraProvider: ProcessCameraProvider? = null
    private var mImageCapture: ImageCapture? = null
    private var mImageAnalyzer: ImageAnalysis? = null
    private var mVideoCapture: VideoCapture? = null
    private var displayId = -1

    //自定义拍照输出路径
    private var outPutCameraDir: String? = null
    //自定义拍照文件名
    private var outPutCameraFileName: String? = null
    //设置每秒的录制帧数
    private var videoFrameRate = 0
    //设置编码比特率。
    private var videoBitRate = 0
    //视频录制最小时长
    private var recordVideoMinSecond = 0L
    //是否显示录制时间
    private var isDisplayRecordTime = false
    //图片文件类型
    private var imageFormat: String? = null
    private var imageFormatForQ: String? = null

    //视频文件类型
    private var videoFormat: String? = null
    private var videoFormatForQ: String? = null

    //相机模式
    private var useCameraCases = LifecycleCameraController.IMAGE_CAPTURE
    //摄像头方向
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    //是否自动纠偏
    var isAutoRotation = false
    private var recordTime: Long = 0

    /**
     * 回调监听
     */
    private var mCameraListener: CameraListener? = null
    private var mOnClickListener: ClickListener? = null

    private var mImageCallbackListener: ImageCallbackListener? = null
    private val mImagePreview by lazy { findViewById<ImageView>(R.id.cover_preview) }
    private val mImagePreviewBg by lazy { findViewById<View>(R.id.cover_preview_bg) }
    private val mSwitchCamera by lazy { findViewById<ImageView>(R.id.image_switch) }
    private val mFlashLamp by lazy { findViewById<FlashImageView>(R.id.image_flash) }
    private val tvCurrentTime by lazy { findViewById<TextView>(R.id.tv_current_time) }
    private val mCaptureLayout by lazy { findViewById<CaptureLayout>(R.id.capture_layout) }
    private val focusImageView by lazy { findViewById<FocusImageView>(R.id.focus_view) }
    private val mTextureView by lazy { findViewById<TextureView>(R.id.video_play_preview) }

    private var mMediaPlayer: MediaPlayer? = null
    private val displayManager by lazy { context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager }
    private val displayListener by lazy { DisplayListener() }
    private var orientationEventListener: OrientationEventListener? = null
    private var mCameraInfo: CameraInfo? = null
    private var mCameraControl: CameraControl? = null
    private val mainExecutor by lazy { ContextCompat.getMainExecutor(context) }
    private var activity: Activity? = null
    private val isImageCaptureEnabled: Boolean
        private get() = useCameraCases == LifecycleCameraController.IMAGE_CAPTURE

    init {
        inflate(context, R.layout.picture_camera_view, this)
        setBackgroundColor(ContextCompat.getColor(context, R.color.camerax_color_black))
        mSwitchCamera.setImageResource(R.drawable.picture_ic_camera)
        displayManager.registerDisplayListener(displayListener, null)
        initView()
    }

    private fun initView() {
        mCameraPreviewView?.post {
                val display = this.display
                if (display != null) {
                    displayId = display.displayId
                }
        }
        mSwitchCamera.setOnClickListener { toggleCamera() }
        mCaptureLayout.captureListener = object : CaptureListener {
            override fun takePictures() {
                val mImageCapture = mImageCapture?:return
                if (mCameraProvider?.isBound(mImageCapture) == false) {
                    bindCameraImageUseCases()
                }
                useCameraCases = LifecycleCameraController.IMAGE_CAPTURE
                mCaptureLayout.setButtonCaptureEnabled(false)
                mSwitchCamera.visibility = INVISIBLE
                mFlashLamp.visibility = INVISIBLE
                tvCurrentTime.visibility = GONE
                val metadata = ImageCapture.Metadata()
                metadata.isReversedHorizontal = isReversedHorizontal
                val cameraFile: File = if (isSaveExternal) {
                    FileUtils.createTempFile(context, false)
                } else {
                    FileUtils.createCameraFile(
                        context, CameraUtils.TYPE_IMAGE,
                        outPutCameraFileName, imageFormat, outPutCameraDir
                    )
                }
                val fileOptions = ImageCapture.OutputFileOptions.Builder(cameraFile).setMetadata(metadata).build()
                mImageCapture.takePicture(fileOptions, mainExecutor,
                    MyImageResultCallback(this@CustomCameraView, mImagePreview, mImagePreviewBg,
                        mCaptureLayout, mImageCallbackListener, mCameraListener
                    )
                )
            }

            override fun recordStart() {
                if (!mCameraProvider!!.isBound(mVideoCapture!!)) {
                    bindCameraVideoUseCases()
                }
                useCameraCases = LifecycleCameraController.VIDEO_CAPTURE
                mSwitchCamera.visibility = INVISIBLE
                mFlashLamp.visibility = INVISIBLE
                tvCurrentTime.visibility = if (isDisplayRecordTime) VISIBLE else GONE
                val fileOptions: VideoCapture.OutputFileOptions
                val cameraFile: File = if (isSaveExternal) {
                    FileUtils.createTempFile(context, true)
                } else {
                    FileUtils.createCameraFile(
                        context, CameraUtils.TYPE_VIDEO,
                        outPutCameraFileName, videoFormat, outPutCameraDir
                    )
                }
                fileOptions = VideoCapture.OutputFileOptions.Builder(cameraFile).build()
                mVideoCapture?.startRecording(fileOptions, mainExecutor!!,
                    object : VideoCapture.OnVideoSavedCallback {
                        override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                            val minSecond = (if (recordVideoMinSecond <= 0) CustomCameraConfig.minDuration else recordVideoMinSecond).toLong()
                            if (recordTime < minSecond || outputFileResults.savedUri == null) {
                                return
                            }
                            val savedUri = outputFileResults.savedUri
                            CustomCameraConfig.putOutputUri(activity!!.intent, savedUri)
                            val outPutPath =
                                if (FileUtils.isContent(savedUri.toString())) savedUri.toString() else savedUri!!.path!!
                            mTextureView.visibility = VISIBLE
                            tvCurrentTime.visibility = GONE
                            if (mTextureView.isAvailable) {
                                startVideoPlay(outPutPath)
                            } else {
                                mTextureView.surfaceTextureListener = surfaceTextureListener
                            }
                        }

                        override fun onError(
                            videoCaptureError: Int, message: String,
                            cause: Throwable?
                        ) {
                            if (mCameraListener != null) {
                                if (videoCaptureError == VideoCapture.ERROR_RECORDING_TOO_SHORT || videoCaptureError == OnVideoSavedCallback.ERROR_MUXER) {
                                    recordShort(0)
                                } else {
                                    mCameraListener!!.onError(videoCaptureError, message, cause)
                                }
                            }
                        }
                    })
            }

            override fun changeTime(duration: Long) {
                if (isDisplayRecordTime && tvCurrentTime.getVisibility() == VISIBLE) {
                    val format = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(duration),
                        TimeUnit.MILLISECONDS.toSeconds(duration)
                                - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(
                                duration
                            )
                        )
                    )
                    if (!TextUtils.equals(format, tvCurrentTime.getText())) {
                        tvCurrentTime.setText(format)
                    }
                    if (TextUtils.equals("00:00", tvCurrentTime.getText())) {
                        tvCurrentTime.setVisibility(GONE)
                    }
                }
            }

            override fun recordShort(time: Long) {
                recordTime = time
                mSwitchCamera.setVisibility(VISIBLE)
                mFlashLamp.setVisibility(VISIBLE)
                tvCurrentTime.setVisibility(GONE)
                mCaptureLayout.resetCaptureLayout()
                mCaptureLayout.setTextWithAnimation(context.getString(R.string.picture_recording_time_is_short))
                try {
                    mVideoCapture!!.stopRecording()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun recordEnd(time: Long) {
                recordTime = time
                try {
                    mVideoCapture!!.stopRecording()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun recordError() {
                if (mCameraListener != null) {
                    mCameraListener!!.onError(0, "An unknown error", null)
                }
            }
        }
        mCaptureLayout.typeListener = object : TypeListener {
            override fun cancel() {
                onCancelMedia()
            }

            override fun confirm() {
                var outputPath = SimpleCameraX.getOutputPath(activity?.intent)?:return
                if (isSaveExternal) {
                    outputPath = isMergeExternalStorageState(activity, outputPath)
                } else {
                    // 对前置镜头导致的镜像进行一个纠正
                    if (isImageCaptureEnabled && isReversedHorizontal) {
                        val cameraFile = FileUtils.createCameraFile(
                            context, CameraUtils.TYPE_IMAGE,
                            outPutCameraFileName, imageFormat, outPutCameraDir
                        )
                        if (FileUtils.copyPath(activity, outputPath, cameraFile.absolutePath)) {
                            outputPath = cameraFile.absolutePath
                            SimpleCameraX.putOutputUri(activity!!.intent, Uri.fromFile(cameraFile))
                        }
                    }
                }
                if (isImageCaptureEnabled) {
                    mImagePreview.setVisibility(INVISIBLE)
                    mImagePreviewBg.setAlpha(0f)
                    if (mCameraListener != null) {
                        mCameraListener!!.onPictureSuccess(outputPath)
                    }
                } else {
                    stopVideoPlay()
                    if (mCameraListener != null) {
                        mCameraListener!!.onRecordSuccess(outputPath)
                    }
                }
            }
        }
        mCaptureLayout.leftClickListener = object : ClickListener {
            override fun onClick() {
                if (mOnClickListener != null) {
                    mOnClickListener!!.onClick()
                }
            }
        }
    }

    private fun isMergeExternalStorageState(activity: Activity?, outputPath: String): String {
        var outputPath = outputPath
        try {
            // 对前置镜头导致的镜像进行一个纠正
            if (isImageCaptureEnabled && isReversedHorizontal) {
                val tempFile = FileUtils.createTempFile(activity, false)
                if (FileUtils.copyPath(activity, outputPath, tempFile.absolutePath)) {
                    outputPath = tempFile.absolutePath
                }
            }
            // 当用户未设置存储路径时，相片默认是存在外部公共目录下
            val externalSavedUri: Uri? = if (isImageCaptureEnabled) {
                val contentValues = CameraUtils.buildImageContentValues(outPutCameraFileName, imageFormatForQ)
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            } else {
                val contentValues = CameraUtils.buildVideoContentValues(outPutCameraFileName, videoFormatForQ)
                context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
            }
            if (externalSavedUri == null) {
                return outputPath
            }
            val outputStream = context.contentResolver.openOutputStream(externalSavedUri)
            val isWriteFileSuccess =
                FileUtils.writeFileFromIS(FileInputStream(outputPath), outputStream)
            if (isWriteFileSuccess) {
                FileUtils.deleteFile(context, outputPath)
                SimpleCameraX.putOutputUri(activity!!.intent, externalSavedUri)
                return externalSavedUri.toString()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return outputPath
    }

    private val isSaveExternal: Boolean
        private get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && TextUtils.isEmpty(
            outPutCameraDir
        )
    private val isReversedHorizontal: Boolean
        private get() = lensFacing == CameraSelector.LENS_FACING_FRONT

    /**
     * 用户针对相机的一些参数配制
     *
     * @param intent
     */
    fun setCameraConfig(intent: Intent) {
        val extras = intent.extras ?: return
        val isCameraAroundState = extras.getBoolean(SimpleCameraX.EXTRA_CAMERA_AROUND_STATE, false)
        lensFacing =
            if (isCameraAroundState) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
        outPutCameraDir = extras.getString(SimpleCameraX.EXTRA_OUTPUT_PATH_DIR)
        outPutCameraFileName = extras.getString(SimpleCameraX.EXTRA_CAMERA_FILE_NAME)
        videoFrameRate = extras.getInt(SimpleCameraX.EXTRA_VIDEO_FRAME_RATE)
        videoBitRate = extras.getInt(SimpleCameraX.EXTRA_VIDEO_BIT_RATE)
        isAutoRotation = extras.getBoolean(SimpleCameraX.EXTRA_AUTO_ROTATION)
        imageFormat = extras.getString(SimpleCameraX.EXTRA_CAMERA_IMAGE_FORMAT, CameraUtils.JPEG)
        imageFormatForQ = extras.getString(
            SimpleCameraX.EXTRA_CAMERA_IMAGE_FORMAT_FOR_Q,
            CameraUtils.MIME_TYPE_IMAGE
        )
        videoFormat = extras.getString(SimpleCameraX.EXTRA_CAMERA_VIDEO_FORMAT, CameraUtils.MP4)
        videoFormatForQ = extras.getString(
            SimpleCameraX.EXTRA_CAMERA_VIDEO_FORMAT_FOR_Q,
            CameraUtils.MIME_TYPE_VIDEO
        )
        val captureLoadingColor = context.getResColor(R.color.camerax_capture_loading_color)
        isDisplayRecordTime =
            extras.getBoolean(SimpleCameraX.EXTRA_DISPLAY_RECORD_CHANGE_TIME, false)
        val format = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(CustomCameraConfig.maxDuration),
            TimeUnit.MILLISECONDS.toSeconds(CustomCameraConfig.minDuration)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(CustomCameraConfig.maxDuration)
            )
        )
        tvCurrentTime.text = format
        if (isAutoRotation && !CustomCameraConfig.isOnlyCapture()) {
            orientationEventListener = OrientationEventListener(
                context, this
            )
            startCheckOrientation()
        }
        setCaptureLoadingColor(captureLoadingColor)
        val isCheckSelfPermission = XXPermissions.isGranted(context, Manifest.permission.CAMERA)
        if (isCheckSelfPermission) {
            buildUseCameraCases()
        } else {
            XXPermissions.with(context)
                .permission(Manifest.permission.CAMERA)
                .interceptor(CustomCameraConfig.interceptor?.onCreateIPermissionInterceptor())
                .request { _: List<String?>?, _: Boolean -> buildUseCameraCases() }
        }
    }

    /**
     * 检测手机方向
     */
    private fun startCheckOrientation() {
        if (orientationEventListener != null) {
            orientationEventListener!!.star()
        }
    }

    /**
     * 停止检测手机方向
     */
    fun stopCheckOrientation() {
        if (orientationEventListener != null) {
            orientationEventListener!!.stop()
        }
    }

    override fun onOrientationChanged(orientation: Int) {
        if (mImageCapture != null) {
            mImageCapture!!.targetRotation = orientation
        }
        if (mImageAnalyzer != null) {
            mImageAnalyzer!!.targetRotation = orientation
        }
    }

    /**
     * 开始打开相机预览
     */
    fun buildUseCameraCases() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                mCameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, mainExecutor)
    }

    /**
     * 初始相机预览模式
     */
    private fun bindCameraUseCases() {
        if (null != mCameraProvider && isBackCameraLevel3Device(mCameraProvider!!)) {
            if (CustomCameraConfig.haveRecord()) {
                bindCameraVideoUseCases()
            } else {
                bindCameraImageUseCases()
            }
        } else {
            when (CustomCameraConfig.buttonFeatures) {
                CustomCameraType.BUTTON_STATE_ONLY_CAPTURE -> bindCameraImageUseCases()
                CustomCameraType.BUTTON_STATE_ONLY_RECORDER -> bindCameraVideoUseCases()
                else -> bindCameraWithUserCases()
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun isBackCameraLevel3Device(cameraProvider: ProcessCameraProvider): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val cameraInfos = CameraSelector.DEFAULT_BACK_CAMERA
                .filter(cameraProvider.availableCameraInfos)
            if (cameraInfos.isNotEmpty()) {
                return Camera2CameraInfo.from(cameraInfos[0]).getCameraCharacteristic(
                    CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL
                ) == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY
            }
        }
        return false
    }

    /**
     * bindCameraWithUserCases
     */
    private fun bindCameraWithUserCases() {
        try {
            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
            // Preview
            val preview = Preview.Builder()
                .setTargetRotation(mCameraPreviewView!!.display.rotation)
                .build()
            // ImageCapture
            buildImageCapture()
            // VideoCapture
            buildVideoCapture()
            val useCase = UseCaseGroup.Builder()
            useCase.addUseCase(preview)
            useCase.addUseCase(mImageCapture!!)
            useCase.addUseCase(mVideoCapture!!)
            val useCaseGroup = useCase.build()
            // Must unbind the use-cases before rebinding them
            mCameraProvider!!.unbindAll()
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            val camera = mCameraProvider!!.bindToLifecycle(
                (context as LifecycleOwner),
                cameraSelector,
                useCaseGroup
            )
            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(mCameraPreviewView!!.surfaceProvider)
            // setFlashMode
            mFlashLamp.setFlashMode()
            mCameraInfo = camera.cameraInfo
            mCameraControl = camera.cameraControl
            initCameraPreviewListener()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * bindCameraImageUseCases
     */
    private fun bindCameraImageUseCases() {
        try {
            val screenAspectRatio = aspectRatio(context.getScreenWidth(),context.getScreenHeight())
            val rotation = mCameraPreviewView!!.display.rotation
            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
            // Preview
            val preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()

            // ImageCapture
            buildImageCapture()

            // ImageAnalysis
            mImageAnalyzer = ImageAnalysis.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()

            // Must unbind the use-cases before rebinding them
            mCameraProvider!!.unbindAll()
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            val camera = mCameraProvider!!.bindToLifecycle(
                (context as LifecycleOwner),
                cameraSelector,
                preview,
                mImageCapture,
                mImageAnalyzer
            )
            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(mCameraPreviewView!!.surfaceProvider)
            // setFlashMode
            mFlashLamp.setFlashMode()
            mCameraInfo = camera.cameraInfo
            mCameraControl = camera.cameraControl
            initCameraPreviewListener()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * bindCameraVideoUseCases
     */
    private fun bindCameraVideoUseCases() {
        try {
            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
            // Preview
            val preview = Preview.Builder()
                .setTargetRotation(mCameraPreviewView!!.display.rotation)
                .build()
            buildVideoCapture()
            // Must unbind the use-cases before rebinding them
            mCameraProvider!!.unbindAll()
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            val camera = mCameraProvider!!.bindToLifecycle(
                (context as LifecycleOwner),
                cameraSelector,
                preview,
                mVideoCapture
            )
            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(mCameraPreviewView!!.surfaceProvider)
            mCameraInfo = camera.cameraInfo
            mCameraControl = camera.cameraControl
            initCameraPreviewListener()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun buildImageCapture() {
        val screenAspectRatio = aspectRatio(context.getScreenWidth(), context.getScreenHeight())
        mImageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(mCameraPreviewView!!.display.rotation)
            .build()
        mFlashLamp.bindImageCapture(mImageCapture)
    }

    @SuppressLint("RestrictedApi")
    private fun buildVideoCapture() {
        val videoBuilder = VideoCapture.Builder()
        videoBuilder.setTargetRotation(mCameraPreviewView!!.display.rotation)
        if (videoFrameRate > 0) {
            videoBuilder.setVideoFrameRate(videoFrameRate)
        }
        if (videoBitRate > 0) {
            videoBuilder.setBitRate(videoBitRate)
        }
        mVideoCapture = videoBuilder.build()
    }

    private fun initCameraPreviewListener() {
        val cameraXPreviewViewTouchListener = PreviewViewTouchListener(context, CustomTouch())
        mCameraPreviewView?.setOnTouchListener(cameraXPreviewViewTouchListener)
    }

    /**
     * [androidx.camera.core.ImageAnalysis.Builder] requires enum value of
     * [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
     *
     *
     * Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     * of preview ratio to one of the provided values.
     *
     * @param width  - preview width
     * @param height - preview height
     * @return suitable aspect ratio
     */
    private fun aspectRatio(width: Int, height: Int): Int {
        val aspect = max(width, height).toDouble()
        val previewRatio = aspect / min(width, height)
        val ratio = abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)
        return if (ratio) AspectRatio.RATIO_4_3 else AspectRatio.RATIO_16_9
    }



    private val surfaceTextureListener: SurfaceTextureListener = object : SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            val outputPath = SimpleCameraX.getOutputPath(activity!!.intent)
            startVideoPlay(outputPath)
        }

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture,
            width: Int,
            height: Int
        ) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    }

    fun setCameraListener(cameraListener: CameraListener?) {
        mCameraListener = cameraListener
    }


    /**
     * 设置拍照时loading色值
     *
     * @param color
     */
    fun setCaptureLoadingColor(color: Int) {
        mCaptureLayout?.setCaptureLoadingColor(color)
    }
    /**
     * 切换前后摄像头
     */
    fun toggleCamera() {
        lensFacing =
            if (CameraSelector.LENS_FACING_FRONT == lensFacing) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT
        bindCameraUseCases()
    }

    /**
     * 关闭相机界面按钮
     *
     * @param clickListener
     */
    fun setOnCancelClickListener(clickListener: ClickListener?) {
        mOnClickListener = clickListener
    }

    fun setImageCallbackListener(mImageCallbackListener: ImageCallbackListener?) {
        this.mImageCallbackListener = mImageCallbackListener
    }

    /**
     * 重置状态
     */
    private fun resetState() {
        if (isImageCaptureEnabled) {
            mImagePreview?.visibility = INVISIBLE
            mImagePreviewBg?.alpha = 0f
        } else {
            try {
                mVideoCapture?.stopRecording()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mSwitchCamera?.visibility = VISIBLE
        mFlashLamp?.visibility = VISIBLE
        mCaptureLayout?.resetCaptureLayout()
    }

    /**
     * 开始循环播放视频
     *
     * @param url
     */
    private fun startVideoPlay(url: String?) {
        try {
            mMediaPlayer = this.mMediaPlayer?:MediaPlayer()
            mMediaPlayer?.reset()
            if (url.isContent()) {
                mMediaPlayer?.setDataSource(context, Uri.parse(url))
            } else {
                mMediaPlayer?.setDataSource(url)
            }
            mMediaPlayer?.setSurface(Surface(mTextureView.surfaceTexture))
            mMediaPlayer?.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
            mMediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer?.setOnVideoSizeChangedListener { _, _, _ ->
                val videoWidth = mMediaPlayer?.videoWidth?.toFloat()?:0F
                val videoHeight = mMediaPlayer?.videoHeight?.toFloat()?:0F
                updateVideoViewSize(videoWidth, videoHeight)
            }
            mMediaPlayer?.setOnPreparedListener { mMediaPlayer?.start() }
            mMediaPlayer?.isLooping = true
            mMediaPlayer?.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * updateVideoViewSize
     *
     * @param videoWidth
     * @param videoHeight
     */
    private fun updateVideoViewSize(videoWidth: Float, videoHeight: Float) {
        if (videoWidth > videoHeight) {
            val height = (videoHeight / videoWidth * width).toInt()
            val videoViewParam = LayoutParams(LayoutParams.MATCH_PARENT, height)
            videoViewParam.addRule(CENTER_IN_PARENT, TRUE)
            mTextureView?.layoutParams = videoViewParam
        }
    }

    /**
     * 取消拍摄相关
     */
    fun onCancelMedia() {
        val outputPath = SimpleCameraX.getOutputPath(activity?.intent)
        FileUtils.deleteFile(context, outputPath)
        stopVideoPlay()
        resetState()
        startCheckOrientation()
    }

    /**
     * 停止视频播放
     */
    private fun stopVideoPlay() {
        val mMediaPlayer = this.mMediaPlayer
        if (mMediaPlayer?.isPlaying == true) {
            mMediaPlayer.stop()
            mMediaPlayer.release()
        }
        this.mMediaPlayer = null
        mTextureView?.visibility = GONE
    }

    /**
     * onConfigurationChanged
     *
     * @param newConfig
     */
    public override fun onConfigurationChanged(newConfig: Configuration) {
        buildUseCameraCases()
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        displayManager.unregisterDisplayListener(displayListener)
        stopCheckOrientation()
    }


    /**
     * We need a display listener for orientation changes that do not trigger a configuration
     * change, for example if we choose to override config change in manifest or for 180-degree
     * orientation changes.
     */
    private inner class DisplayListener : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) {}
        override fun onDisplayRemoved(displayId: Int) {}
        override fun onDisplayChanged(displayId: Int) {
            if (displayId == this@CustomCameraView.displayId) {
                mImageCapture?.targetRotation = mCameraPreviewView.display.rotation
                mImageAnalyzer?.targetRotation = mCameraPreviewView.display.rotation
            }
        }
    }

    /**
     * 触碰事件
     */
    private inner class CustomTouch: CustomTouchListener {
        private val zoomState by lazy {  mCameraInfo?.zoomState }
        override fun zoom(delta: Float) {
            val value = zoomState?.value
            if (value != null) {
                val currentZoomRatio = value.zoomRatio
                mCameraControl?.setZoomRatio(currentZoomRatio * delta)
            }
        }

        override fun click(x: Float, y: Float) {
            val factory = mCameraPreviewView?.meteringPointFactory?:return
            val point = factory.createPoint(x, y)
            val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                .setAutoCancelDuration(3, TimeUnit.SECONDS)
                .build()
            if (mCameraInfo?.isFocusMeteringSupported(action) == true) {
                mCameraControl?.cancelFocusAndMetering()
                focusImageView?.isDisappear = false
                focusImageView?.startFocus(Point(x.toInt(), y.toInt()))
                val future = mCameraControl?.startFocusAndMetering(action)
                future?.addListener({
                    try {
                        val result = future.get()
                        focusImageView?.isDisappear = true
                        if (result.isFocusSuccessful) {
                            focusImageView?.onFocusSuccess()
                        } else {
                            focusImageView?.onFocusFailed()
                        }
                    } catch (ignored: Exception) {
                    }
                }, mainExecutor)
            }
        }

        override fun doubleClick(x: Float, y: Float) {
            val value = zoomState?.value
            if (value != null) {
                val currentZoomRatio = value.zoomRatio
                val minZoomRatio = value.minZoomRatio
                if (currentZoomRatio > minZoomRatio) {
                    mCameraControl?.setLinearZoom(0f)
                } else {
                    mCameraControl?.setLinearZoom(0.5f)
                }
            }
        }
    }

    /**
     * 拍照回调
     */
    private inner class MyImageResultCallback(cameraView: CustomCameraView, imagePreview: ImageView?, imagePreviewBg: View?, captureLayout: CaptureLayout?,
                                              imageCallbackListener: ImageCallbackListener?, cameraListener: CameraListener?) :
        ImageCapture.OnImageSavedCallback {

        private val mImagePreviewReference: WeakReference<ImageView> by lazy { WeakReference<ImageView>(imagePreview) }
        private val mImagePreviewBgReference: WeakReference<View> by lazy { WeakReference<View>(imagePreviewBg) }
        private val mCaptureLayoutReference by lazy { WeakReference<CaptureLayout>(captureLayout) }
        private val mImageCallbackListenerReference by lazy { WeakReference<ImageCallbackListener>(imageCallbackListener) }
        private val mCameraListenerReference by lazy { WeakReference<CameraListener>(cameraListener) }
        private val mCameraViewLayoutReference by lazy { WeakReference<CustomCameraView>(cameraView) }

        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val savedUri = outputFileResults.savedUri?:return
            val customCameraView = mCameraViewLayoutReference.get()
            customCameraView?.stopCheckOrientation()
            val mImagePreview = mImagePreviewReference.get()
            val captureLayout = mCaptureLayoutReference.get()

            if (mImagePreview != null) {
                val context = mImagePreview.context
                SimpleCameraX.putOutputUri((context as Activity).intent, savedUri)
                mImagePreview.visibility = RelativeLayout.VISIBLE
                if (customCameraView != null && customCameraView.isAutoRotation) {
                    val targetRotation = mImageCapture?.targetRotation
                    // 这种角度拍出来的图片宽比高大，所以使用ScaleType.FIT_CENTER缩放模式
                    if (targetRotation == Surface.ROTATION_90 || targetRotation == Surface.ROTATION_270) {
                        mImagePreview.adjustViewBounds = true
                    } else {
                        mImagePreview.adjustViewBounds = false
                        mImagePreview.scaleType = ImageView.ScaleType.FIT_CENTER
                    }
                    val mImagePreviewBackground = mImagePreviewBgReference.get()
                    mImagePreviewBackground?.animate()?.alpha(1f)?.setDuration(220)?.start()
                }
                val imageCallbackListener = mImageCallbackListenerReference.get()
                if (imageCallbackListener != null) {
                    val outPutCameraPath = if (FileUtils.isContent(savedUri.toString())) savedUri.toString() else savedUri.path
                    imageCallbackListener.onLoadImage(outPutCameraPath, mImagePreview)
                }
            }

            if (captureLayout != null) {
                captureLayout.setButtonCaptureEnabled(true)
                captureLayout.startTypeBtnAnimator()
            }
        }

        override fun onError(exception: ImageCaptureException) {
            mCaptureLayoutReference.get()?.setButtonCaptureEnabled(true)
            mCameraListenerReference.get()?.onError(exception.imageCaptureError, exception.message, exception.cause)
        }
    }

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

    }
}