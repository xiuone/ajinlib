package com.lib.camerax

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
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.lib.camerax.utils.FileUtils.createTempFile
import com.lib.camerax.utils.FileUtils.createCameraFile
import com.lib.camerax.SimpleCameraX.Companion.putOutputUri
import com.lib.camerax.utils.FileUtils.isContent
import com.lib.camerax.SimpleCameraX.Companion.getOutputPath
import com.lib.camerax.utils.FileUtils.copyPath
import com.lib.camerax.listener.CameraListener.onPictureSuccess
import com.lib.camerax.listener.CameraListener.onRecordSuccess
import com.lib.camerax.listener.ClickListener.onClick
import com.lib.camerax.utils.CameraUtils.buildImageContentValues
import com.lib.camerax.utils.CameraUtils.buildVideoContentValues
import com.lib.camerax.utils.FileUtils.writeFileFromIS
import com.lib.camerax.utils.FileUtils.deleteFile
import com.lib.camerax.listener.CameraXOrientationEventListener.star
import com.lib.camerax.listener.CameraXOrientationEventListener.stop
import com.lib.camerax.utils.DensityUtil.getScreenWidth
import com.lib.camerax.utils.DensityUtil.getScreenHeight
import com.lib.camerax.listener.CameraXPreviewViewTouchListener.setCustomTouchListener
import com.lib.camerax.listener.ImageCallbackListener.onLoadImage
import com.lib.camerax.listener.CameraXOrientationEventListener.OnOrientationChangedListener
import com.lib.camerax.CustomCameraView
import androidx.camera.view.PreviewView
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import com.lib.camerax.listener.CameraListener
import com.lib.camerax.listener.ClickListener
import com.lib.camerax.listener.ImageCallbackListener
import com.lib.camerax.widget.CaptureLayout
import com.lib.camerax.listener.CameraXOrientationEventListener
import com.lib.camerax.widget.FocusImageView
import androidx.core.content.ContextCompat
import com.lib.camerax.listener.CaptureListener
import com.lib.camerax.utils.CameraUtils
import com.lib.camerax.CustomCameraView.MyImageResultCallback
import com.lib.camerax.CustomCameraConfig
import com.lib.camerax.SimpleCameraX
import com.lib.camerax.listener.TypeListener
import com.google.common.util.concurrent.ListenableFuture
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.core.*
import androidx.camera.view.video.OnVideoSavedCallback
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.lib.camerax.listener.CameraXPreviewViewTouchListener
import com.lib.camerax.listener.CameraXPreviewViewTouchListener.CustomTouchListener
import com.luck.picture.lib.R
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

/**
 * @author：luck
 * @date：2020-01-04 13:41
 * @describe：自定义相机View
 */
class CustomCameraView : RelativeLayout, OnOrientationChangedListener {
    private var typeFlash = TYPE_FLASH_OFF
    private var mCameraPreviewView: PreviewView? = null
    private var mCameraProvider: ProcessCameraProvider? = null
    private var mImageCapture: ImageCapture? = null
    private var mImageAnalyzer: ImageAnalysis? = null
    private var mVideoCapture: VideoCapture? = null
    private var displayId = -1

    /**
     * 相机模式
     */
    private var buttonFeatures = 0

    /**
     * 自定义拍照输出路径
     */
    private var outPutCameraDir: String? = null

    /**
     * 自定义拍照文件名
     */
    private var outPutCameraFileName: String? = null

    /**
     * 设置每秒的录制帧数
     */
    private var videoFrameRate = 0

    /**
     * 设置编码比特率。
     */
    private var videoBitRate = 0

    /**
     * 视频录制最小时长
     */
    private var recordVideoMinSecond = 0

    /**
     * 是否显示录制时间
     */
    private var isDisplayRecordTime = false

    /**
     * 图片文件类型
     */
    private var imageFormat: String? = null
    private var imageFormatForQ: String? = null

    /**
     * 视频文件类型
     */
    private var videoFormat: String? = null
    private var videoFormatForQ: String? = null

    /**
     * 相机模式
     */
    private var useCameraCases = LifecycleCameraController.IMAGE_CAPTURE

    /**
     * 摄像头方向
     */
    private var lensFacing = CameraSelector.LENS_FACING_BACK

    /**
     * 手指点击对焦
     */
    private var isManualFocus = false

    /**
     * 双击可放大缩小
     */
    private var isZoomPreview = false

    /**
     * 是否自动纠偏
     */
    private var isAutoRotation = false
    private var recordTime: Long = 0

    /**
     * 回调监听
     */
    private var mCameraListener: CameraListener? = null
    private var mOnClickListener: ClickListener? = null
    private var mImageCallbackListener: ImageCallbackListener? = null
    private var mImagePreview: ImageView? = null
    private var mImagePreviewBg: View? = null
    private var mSwitchCamera: ImageView? = null
    private var mFlashLamp: ImageView? = null
    private var tvCurrentTime: TextView? = null
    private var mCaptureLayout: CaptureLayout? = null
    private var mMediaPlayer: MediaPlayer? = null
    private var mTextureView: TextureView? = null
    private var displayManager: DisplayManager? = null
    private var displayListener: DisplayListener? = null
    private var orientationEventListener: CameraXOrientationEventListener? = null
    private var mCameraInfo: CameraInfo? = null
    private var mCameraControl: CameraControl? = null
    private var focusImageView: FocusImageView? = null
    private var mainExecutor: Executor? = null
    private var activity: Activity? = null
    private val isImageCaptureEnabled: Boolean
        private get() = useCameraCases == LifecycleCameraController.IMAGE_CAPTURE

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        inflate(context, R.layout.picture_camera_view, this)
        activity = context as Activity
        setBackgroundColor(ContextCompat.getColor(context, R.color.picture_color_black))
        mCameraPreviewView = findViewById(R.id.cameraPreviewView)
        mTextureView = findViewById(R.id.video_play_preview)
        focusImageView = findViewById(R.id.focus_view)
        mImagePreview = findViewById(R.id.cover_preview)
        mImagePreviewBg = findViewById(R.id.cover_preview_bg)
        mSwitchCamera = findViewById(R.id.image_switch)
        mFlashLamp = findViewById(R.id.image_flash)
        mCaptureLayout = findViewById(R.id.capture_layout)
        tvCurrentTime = findViewById(R.id.tv_current_time)
        mSwitchCamera.setImageResource(R.drawable.picture_ic_camera)
        displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayListener = DisplayListener()
        displayManager!!.registerDisplayListener(displayListener, null)
        mainExecutor = ContextCompat.getMainExecutor(context)
        mCameraPreviewView.post(Runnable {
            if (mCameraPreviewView != null) {
                val display = mCameraPreviewView!!.display
                if (display != null) {
                    displayId = display.displayId
                }
            }
        })
        mFlashLamp.setOnClickListener(OnClickListener { v: View? ->
            typeFlash++
            if (typeFlash > 0x023) {
                typeFlash = TYPE_FLASH_AUTO
            }
            setFlashMode()
        })
        mSwitchCamera.setOnClickListener(OnClickListener { toggleCamera() })
        mCaptureLayout.setCaptureListener(object : CaptureListener {
            override fun takePictures() {
                if (!mCameraProvider!!.isBound(mImageCapture!!)) {
                    bindCameraImageUseCases()
                }
                useCameraCases = LifecycleCameraController.IMAGE_CAPTURE
                mCaptureLayout.setButtonCaptureEnabled(false)
                mSwitchCamera.setVisibility(INVISIBLE)
                mFlashLamp.setVisibility(INVISIBLE)
                tvCurrentTime.setVisibility(GONE)
                val metadata = ImageCapture.Metadata()
                metadata.isReversedHorizontal = isReversedHorizontal
                val fileOptions: ImageCapture.OutputFileOptions
                val cameraFile: File
                cameraFile = if (isSaveExternal) {
                    createTempFile(context, false)
                } else {
                    createCameraFile(
                        context, CameraUtils.TYPE_IMAGE,
                        outPutCameraFileName!!, imageFormat!!, outPutCameraDir!!
                    )
                }
                fileOptions = ImageCapture.OutputFileOptions.Builder(cameraFile)
                    .setMetadata(metadata).build()
                mImageCapture!!.takePicture(
                    fileOptions, mainExecutor!!,
                    MyImageResultCallback(
                        this@CustomCameraView, mImagePreview, mImagePreviewBg,
                        mCaptureLayout, mImageCallbackListener, mCameraListener
                    )
                )
            }

            override fun recordStart() {
                if (!mCameraProvider!!.isBound(mVideoCapture!!)) {
                    bindCameraVideoUseCases()
                }
                useCameraCases = LifecycleCameraController.VIDEO_CAPTURE
                mSwitchCamera.setVisibility(INVISIBLE)
                mFlashLamp.setVisibility(INVISIBLE)
                tvCurrentTime.setVisibility(if (isDisplayRecordTime) VISIBLE else GONE)
                val fileOptions: VideoCapture.OutputFileOptions
                val cameraFile: File
                cameraFile = if (isSaveExternal) {
                    createTempFile(context, true)
                } else {
                    createCameraFile(
                        context, CameraUtils.TYPE_VIDEO,
                        outPutCameraFileName!!, videoFormat!!, outPutCameraDir!!
                    )
                }
                fileOptions = VideoCapture.OutputFileOptions.Builder(cameraFile).build()
                mVideoCapture!!.startRecording(fileOptions, mainExecutor!!,
                    object : VideoCapture.OnVideoSavedCallback {
                        override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                            val minSecond =
                                if (recordVideoMinSecond <= 0) CustomCameraConfig.DEFAULT_MIN_RECORD_VIDEO.toLong() else recordVideoMinSecond.toLong()
                            if (recordTime < minSecond || outputFileResults.savedUri == null) {
                                return
                            }
                            val savedUri = outputFileResults.savedUri
                            putOutputUri(activity!!.intent, savedUri)
                            val outPutPath =
                                if (isContent(savedUri.toString())) savedUri.toString() else savedUri!!.path!!
                            mTextureView.setVisibility(VISIBLE)
                            tvCurrentTime.setVisibility(GONE)
                            if (mTextureView.isAvailable()) {
                                startVideoPlay(outPutPath)
                            } else {
                                mTextureView.setSurfaceTextureListener(surfaceTextureListener)
                            }
                        }

                        override fun onError(
                            videoCaptureError: Int, message: String,
                            cause: Throwable?
                        ) {
                            if (mCameraListener != null) {
                                if (videoCaptureError == ERROR_RECORDING_TOO_SHORT || videoCaptureError == OnVideoSavedCallback.ERROR_MUXER) {
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

            override fun recordZoom(zoom: Float) {}
            override fun recordError() {
                if (mCameraListener != null) {
                    mCameraListener!!.onError(0, "An unknown error", null)
                }
            }
        })
        mCaptureLayout.setTypeListener(object : TypeListener {
            override fun cancel() {
                onCancelMedia()
            }

            override fun confirm() {
                var outputPath = getOutputPath(activity!!.intent)
                if (isSaveExternal) {
                    outputPath = isMergeExternalStorageState(activity, outputPath)
                } else {
                    // 对前置镜头导致的镜像进行一个纠正
                    if (isImageCaptureEnabled && isReversedHorizontal) {
                        val cameraFile = createCameraFile(
                            context, CameraUtils.TYPE_IMAGE,
                            outPutCameraFileName!!, imageFormat!!, outPutCameraDir!!
                        )
                        if (copyPath(activity!!, outputPath, cameraFile.absolutePath)) {
                            outputPath = cameraFile.absolutePath
                            putOutputUri(activity!!.intent, Uri.fromFile(cameraFile))
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
        })
        mCaptureLayout.setLeftClickListener(object : ClickListener {
            override fun onClick() {
                if (mOnClickListener != null) {
                    mOnClickListener!!.onClick()
                }
            }
        })
    }

    private fun isMergeExternalStorageState(activity: Activity?, outputPath: String): String {
        var outputPath = outputPath
        try {
            // 对前置镜头导致的镜像进行一个纠正
            if (isImageCaptureEnabled && isReversedHorizontal) {
                val tempFile = createTempFile(activity!!, false)
                if (copyPath(activity, outputPath, tempFile.absolutePath)) {
                    outputPath = tempFile.absolutePath
                }
            }
            // 当用户未设置存储路径时，相片默认是存在外部公共目录下
            val externalSavedUri: Uri?
            externalSavedUri = if (isImageCaptureEnabled) {
                val contentValues = buildImageContentValues(
                    outPutCameraFileName!!, imageFormatForQ!!
                )
                context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
            } else {
                val contentValues = buildVideoContentValues(
                    outPutCameraFileName!!, videoFormatForQ!!
                )
                context.contentResolver.insert(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
            }
            if (externalSavedUri == null) {
                return outputPath
            }
            val outputStream = context.contentResolver.openOutputStream(externalSavedUri)
            val isWriteFileSuccess = writeFileFromIS(FileInputStream(outputPath), outputStream!!)
            if (isWriteFileSuccess) {
                deleteFile(context, outputPath)
                putOutputUri(activity!!.intent, externalSavedUri)
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
        buttonFeatures =
            extras.getInt(SimpleCameraX.EXTRA_CAMERA_MODE, CustomCameraConfig.BUTTON_STATE_BOTH)
        lensFacing =
            if (isCameraAroundState) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
        outPutCameraDir = extras.getString(SimpleCameraX.EXTRA_OUTPUT_PATH_DIR)
        outPutCameraFileName = extras.getString(SimpleCameraX.EXTRA_CAMERA_FILE_NAME)
        videoFrameRate = extras.getInt(SimpleCameraX.EXTRA_VIDEO_FRAME_RATE)
        videoBitRate = extras.getInt(SimpleCameraX.EXTRA_VIDEO_BIT_RATE)
        isManualFocus = extras.getBoolean(SimpleCameraX.EXTRA_MANUAL_FOCUS)
        isZoomPreview = extras.getBoolean(SimpleCameraX.EXTRA_ZOOM_PREVIEW)
        isAutoRotation = extras.getBoolean(SimpleCameraX.EXTRA_AUTO_ROTATION)
        val recordVideoMaxSecond = extras.getInt(
            SimpleCameraX.EXTRA_RECORD_VIDEO_MAX_SECOND,
            CustomCameraConfig.DEFAULT_MAX_RECORD_VIDEO
        )
        recordVideoMinSecond = extras.getInt(
            SimpleCameraX.EXTRA_RECORD_VIDEO_MIN_SECOND,
            CustomCameraConfig.DEFAULT_MIN_RECORD_VIDEO
        )
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
        val captureLoadingColor =
            extras.getInt(SimpleCameraX.EXTRA_CAPTURE_LOADING_COLOR, -0x828201)
        isDisplayRecordTime =
            extras.getBoolean(SimpleCameraX.EXTRA_DISPLAY_RECORD_CHANGE_TIME, false)
        mCaptureLayout!!.setButtonFeatures(buttonFeatures)
        if (recordVideoMaxSecond > 0) {
            setRecordVideoMaxTime(recordVideoMaxSecond)
        }
        if (recordVideoMinSecond > 0) {
            setRecordVideoMinTime(recordVideoMinSecond)
        }
        val format = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(recordVideoMaxSecond.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(recordVideoMaxSecond.toLong())
                    - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    recordVideoMaxSecond.toLong()
                )
            )
        )
        tvCurrentTime!!.text = format
        if (isAutoRotation && buttonFeatures != CustomCameraConfig.BUTTON_STATE_ONLY_RECORDER) {
            orientationEventListener = CameraXOrientationEventListener(context, this)
            startCheckOrientation()
        }
        setCaptureLoadingColor(captureLoadingColor)
        setProgressColor(captureLoadingColor)
        val isCheckSelfPermission: Boolean = PermissionChecker.checkSelfPermission(
            context, arrayOf(
                Manifest.permission.CAMERA
            )
        )
        if (isCheckSelfPermission) {
            buildUseCameraCases()
        } else {
            if (CustomCameraConfig.explainListener != null) {
                if (!SimpleXSpUtils.getBoolean(context, Manifest.permission.CAMERA, false)) {
                    CustomCameraConfig.explainListener
                        .onPermissionDescription(context, this, Manifest.permission.CAMERA)
                }
            }
            PermissionChecker.getInstance()
                .requestPermissions(activity, arrayOf(Manifest.permission.CAMERA),
                    object : PermissionResultCallback() {
                        fun onGranted() {
                            buildUseCameraCases()
                            if (CustomCameraConfig.explainListener != null) {
                                CustomCameraConfig.explainListener.onDismiss(this@CustomCameraView)
                            }
                        }

                        fun onDenied() {
                            if (CustomCameraConfig.deniedListener != null) {
                                SimpleXSpUtils.putBoolean(context, Manifest.permission.CAMERA, true)
                                CustomCameraConfig.deniedListener.onDenied(
                                    context,
                                    Manifest.permission.CAMERA,
                                    PermissionChecker.PERMISSION_SETTING_CODE
                                )
                                if (CustomCameraConfig.explainListener != null) {
                                    CustomCameraConfig.explainListener.onDismiss(this@CustomCameraView)
                                }
                            } else {
                                SimpleXPermissionUtil.goIntentSetting(
                                    activity,
                                    PermissionChecker.PERMISSION_SETTING_CODE
                                )
                            }
                        }
                    })
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

    private val targetRotation: Int
        private get() = mImageCapture!!.targetRotation

    override fun onOrientationChanged(orientation: Int) {
        if (mImageCapture != null) {
            mImageCapture!!.targetRotation = orientation
        }
        if (mImageAnalyzer != null) {
            mImageAnalyzer!!.targetRotation = orientation
        }
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
                if (mImageCapture != null) {
                    mImageCapture!!.targetRotation = mCameraPreviewView!!.display.rotation
                }
                if (mImageAnalyzer != null) {
                    mImageAnalyzer!!.targetRotation = mCameraPreviewView!!.display.rotation
                }
            }
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
            if (CustomCameraConfig.BUTTON_STATE_ONLY_RECORDER == buttonFeatures) {
                bindCameraVideoUseCases()
            } else {
                bindCameraImageUseCases()
            }
        } else {
            when (buttonFeatures) {
                CustomCameraConfig.BUTTON_STATE_ONLY_CAPTURE -> bindCameraImageUseCases()
                CustomCameraConfig.BUTTON_STATE_ONLY_RECORDER -> bindCameraVideoUseCases()
                else -> bindCameraWithUserCases()
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun isBackCameraLevel3Device(cameraProvider: ProcessCameraProvider): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val cameraInfos: List<CameraInfo> = CameraSelector.DEFAULT_BACK_CAMERA
                .filter(cameraProvider.getAvailableCameraInfos())
            if (!cameraInfos.isEmpty()) {
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
            setFlashMode()
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
            val screenAspectRatio = aspectRatio(
                getScreenWidth(
                    context
                ), getScreenHeight(context)
            )
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
            setFlashMode()
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
        val screenAspectRatio = aspectRatio(
            getScreenWidth(
                context
            ), getScreenHeight(context)
        )
        mImageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(mCameraPreviewView!!.display.rotation)
            .build()
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
        val zoomState = mCameraInfo!!.zoomState
        val cameraXPreviewViewTouchListener = CameraXPreviewViewTouchListener(context)
        cameraXPreviewViewTouchListener.setCustomTouchListener(object : CustomTouchListener {
            override fun zoom(delta: Float) {
                if (isZoomPreview) {
                    if (zoomState.value != null) {
                        val currentZoomRatio = zoomState.value!!.zoomRatio
                        mCameraControl!!.setZoomRatio(currentZoomRatio * delta)
                    }
                }
            }

            override fun click(x: Float, y: Float) {
                if (isManualFocus) {
                    val factory = mCameraPreviewView!!.meteringPointFactory
                    val point = factory.createPoint(x, y)
                    val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                        .setAutoCancelDuration(3, TimeUnit.SECONDS)
                        .build()
                    if (mCameraInfo.isFocusMeteringSupported(action)) {
                        mCameraControl!!.cancelFocusAndMetering()
                        focusImageView!!.setDisappear(false)
                        focusImageView!!.startFocus(Point(x.toInt(), y.toInt()))
                        val future = mCameraControl!!.startFocusAndMetering(action)
                        future.addListener({
                            try {
                                val result = future.get()
                                focusImageView!!.setDisappear(true)
                                if (result.isFocusSuccessful) {
                                    focusImageView!!.onFocusSuccess()
                                } else {
                                    focusImageView!!.onFocusFailed()
                                }
                            } catch (ignored: Exception) {
                            }
                        }, mainExecutor)
                    }
                }
            }

            override fun doubleClick(x: Float, y: Float) {
                if (isZoomPreview) {
                    if (zoomState.value != null) {
                        val currentZoomRatio = zoomState.value!!.zoomRatio
                        val minZoomRatio = zoomState.value!!.minZoomRatio
                        if (currentZoomRatio > minZoomRatio) {
                            mCameraControl!!.setLinearZoom(0f)
                        } else {
                            mCameraControl!!.setLinearZoom(0.5f)
                        }
                    }
                }
            }
        })
        mCameraPreviewView!!.setOnTouchListener(cameraXPreviewViewTouchListener)
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
        val aspect = Math.max(width, height).toDouble()
        val previewRatio = aspect / Math.min(width, height)
        return if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(
                previewRatio - RATIO_16_9_VALUE
            )
        ) {
            AspectRatio.RATIO_4_3
        } else AspectRatio.RATIO_16_9
    }

    /**
     * 拍照回调
     */
    private class MyImageResultCallback(
        cameraView: CustomCameraView,
        imagePreview: ImageView?,
        imagePreviewBg: View?,
        captureLayout: CaptureLayout?,
        imageCallbackListener: ImageCallbackListener?,
        cameraListener: CameraListener?
    ) : ImageCapture.OnImageSavedCallback {
        private val mImagePreviewReference: WeakReference<ImageView?>
        private val mImagePreviewBgReference: WeakReference<View?>
        private val mCaptureLayoutReference: WeakReference<CaptureLayout?>
        private val mImageCallbackListenerReference: WeakReference<ImageCallbackListener?>
        private val mCameraListenerReference: WeakReference<CameraListener?>
        private val mCameraViewLayoutReference: WeakReference<CustomCameraView>
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val savedUri = outputFileResults.savedUri
            if (savedUri != null) {
                val customCameraView = mCameraViewLayoutReference.get()
                customCameraView?.stopCheckOrientation()
                val mImagePreview = mImagePreviewReference.get()
                if (mImagePreview != null) {
                    val context = mImagePreview.context
                    putOutputUri((context as Activity).intent, savedUri)
                    mImagePreview.visibility = VISIBLE
                    if (customCameraView != null && customCameraView.isAutoRotation) {
                        val targetRotation = customCameraView.targetRotation
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
                        val outPutCameraPath =
                            if (isContent(savedUri.toString())) savedUri.toString() else savedUri.path!!
                        imageCallbackListener.onLoadImage(outPutCameraPath, mImagePreview)
                    }
                }
                val captureLayout = mCaptureLayoutReference.get()
                if (captureLayout != null) {
                    captureLayout.setButtonCaptureEnabled(true)
                    captureLayout.startTypeBtnAnimator()
                }
            }
        }

        override fun onError(exception: ImageCaptureException) {
            if (mCaptureLayoutReference.get() != null) {
                mCaptureLayoutReference.get()!!.setButtonCaptureEnabled(true)
            }
            if (mCameraListenerReference.get() != null) {
                mCameraListenerReference.get()!!.onError(
                    exception.imageCaptureError,
                    exception.message, exception.cause
                )
            }
        }

        init {
            mCameraViewLayoutReference = WeakReference(cameraView)
            mImagePreviewReference = WeakReference(imagePreview)
            mImagePreviewBgReference = WeakReference(imagePreviewBg)
            mCaptureLayoutReference = WeakReference(captureLayout)
            mImageCallbackListenerReference = WeakReference(imageCallbackListener)
            mCameraListenerReference = WeakReference(cameraListener)
        }
    }

    private val surfaceTextureListener: TextureView.SurfaceTextureListener =
        object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                val outputPath = getOutputPath(activity!!.intent)
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
     * 设置录制视频最大时长 秒
     */
    fun setRecordVideoMaxTime(maxDurationTime: Int) {
        mCaptureLayout!!.setDuration(maxDurationTime)
    }

    /**
     * 设置录制视频最小时长 秒
     */
    fun setRecordVideoMinTime(minDurationTime: Int) {
        mCaptureLayout!!.setMinDuration(minDurationTime)
    }

    /**
     * 设置拍照时loading色值
     *
     * @param color
     */
    fun setCaptureLoadingColor(color: Int) {
        mCaptureLayout!!.setCaptureLoadingColor(color)
    }

    /**
     * 设置录像时loading色值
     *
     * @param color
     */
    fun setProgressColor(color: Int) {
        mCaptureLayout!!.setProgressColor(color)
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
     * 闪光灯模式
     */
    private fun setFlashMode() {
        if (mImageCapture == null) {
            return
        }
        when (typeFlash) {
            TYPE_FLASH_AUTO -> {
                mFlashLamp!!.setImageResource(R.drawable.picture_ic_flash_auto)
                mImageCapture!!.flashMode = ImageCapture.FLASH_MODE_AUTO
            }
            TYPE_FLASH_ON -> {
                mFlashLamp!!.setImageResource(R.drawable.picture_ic_flash_on)
                mImageCapture!!.flashMode = ImageCapture.FLASH_MODE_ON
            }
            TYPE_FLASH_OFF -> {
                mFlashLamp!!.setImageResource(R.drawable.picture_ic_flash_off)
                mImageCapture!!.flashMode = ImageCapture.FLASH_MODE_OFF
            }
        }
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
            mImagePreview!!.visibility = INVISIBLE
            mImagePreviewBg!!.alpha = 0f
        } else {
            try {
                mVideoCapture!!.stopRecording()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mSwitchCamera!!.visibility = VISIBLE
        mFlashLamp!!.visibility = VISIBLE
        mCaptureLayout!!.resetCaptureLayout()
    }

    /**
     * 开始循环播放视频
     *
     * @param url
     */
    private fun startVideoPlay(url: String) {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = MediaPlayer()
            } else {
                mMediaPlayer!!.reset()
            }
            if (isContent(url)) {
                mMediaPlayer!!.setDataSource(context, Uri.parse(url))
            } else {
                mMediaPlayer!!.setDataSource(url)
            }
            mMediaPlayer!!.setSurface(Surface(mTextureView!!.surfaceTexture))
            mMediaPlayer!!.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
            mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mMediaPlayer!!.setOnVideoSizeChangedListener { mp, width, height ->
                updateVideoViewSize(
                    mMediaPlayer!!.videoWidth.toFloat(),
                    mMediaPlayer!!.videoHeight.toFloat()
                )
            }
            mMediaPlayer!!.setOnPreparedListener { mMediaPlayer!!.start() }
            mMediaPlayer!!.isLooping = true
            mMediaPlayer!!.prepareAsync()
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
            mTextureView!!.layoutParams = videoViewParam
        }
    }

    /**
     * 取消拍摄相关
     */
    fun onCancelMedia() {
        val outputPath = getOutputPath(activity!!.intent)
        deleteFile(context, outputPath)
        stopVideoPlay()
        resetState()
        startCheckOrientation()
    }

    /**
     * 停止视频播放
     */
    private fun stopVideoPlay() {
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
        mTextureView!!.visibility = GONE
    }

    /**
     * onConfigurationChanged
     *
     * @param newConfig
     */
    public override fun onConfigurationChanged(newConfig: Configuration) {
        buildUseCameraCases()
    }

    /**
     * onDestroy
     */
    fun onDestroy() {
        displayManager!!.unregisterDisplayListener(displayListener)
        stopCheckOrientation()
        focusImageView!!.destroy()
    }

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        /**
         * 闪关灯状态
         */
        private const val TYPE_FLASH_AUTO = 0x021
        private const val TYPE_FLASH_ON = 0x022
        private const val TYPE_FLASH_OFF = 0x023
    }
}