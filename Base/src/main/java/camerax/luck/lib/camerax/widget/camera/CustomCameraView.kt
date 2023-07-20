package camerax.luck.lib.camerax.widget.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.hardware.camera2.CameraCharacteristics
import android.hardware.display.DisplayManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import camerax.luck.lib.camerax.CustomCameraConfig
import camerax.luck.lib.camerax.type.CustomCameraType
import camerax.luck.lib.camerax.listener.CameraListener
import camerax.luck.lib.camerax.listener.ClickListener
import camerax.luck.lib.camerax.listener.ImageCallbackListener
import camerax.luck.lib.camerax.widget.camera.OrientationEventListener.OnOrientationChangedListener
import camerax.luck.lib.camerax.utils.CameraUtils
import camerax.luck.lib.camerax.utils.FileUtils
import camerax.luck.lib.camerax.widget.camera.callback.CallBackCustomCapture
import camerax.luck.lib.camerax.widget.camera.callback.CallBackCustomDisplay
import camerax.luck.lib.camerax.widget.camera.callback.CallBackCustomTouch
import camerax.luck.lib.camerax.widget.camera.callback.CallBackCustomTypeCallBack
import camerax.luck.lib.camerax.widget.capture.CaptureLayout
import camerax.luck.lib.camerax.widget.flash.FlashImageView
import camerax.luck.lib.camerax.widget.focus.FocusImageView
import xy.xy.base.R
import xy.xy.base.utils.exp.getScreenHeight
import xy.xy.base.utils.exp.getScreenWidth
import xy.xy.base.utils.exp.isContent
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.concurrent.Executor
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
    RelativeLayout(context, attrs, defStyleAttr), OnOrientationChangedListener ,CustomCameraViewListener{
    //图片文件类型
    private val imageFormatForQ by lazy { CustomCameraConfig.getConfig().imageFormatQ }
    //视频文件类型
    private val videoFormatForQ by lazy { CustomCameraConfig.getConfig().videoFormatQ }
    //摄像头方向
    private var lensFacing = if (CustomCameraConfig.getConfig().isCameraAroundState) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK

    private val customTouch by lazy { CallBackCustomTouch(this) }
    private val customDisplay by lazy { CallBackCustomDisplay(this) }
    private val customCapture by lazy { CallBackCustomCapture(this) }

    private val mCameraPreviewView by lazy { findViewById<PreviewView>(R.id.cameraPreviewView) }
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
    private val orientationEventListener by lazy { OrientationEventListener(context, this) }
    private var mCameraInfo: CameraInfo? = null
    private var mCameraControl: CameraControl? = null
    private val mainExecutor by lazy { ContextCompat.getMainExecutor(context) }
    private var mCameraProvider: ProcessCameraProvider? = null
    private var mImageCapture: ImageCapture? = null
    private var mImageAnalyzer: ImageAnalysis? = null
    private var mVideoCapture: VideoCapture? = null
    /**
     * 回调监听
     */
    private var mCameraListener: CameraListener? = null
    private var mImageCallbackListener: ImageCallbackListener? = null

    init {
        initView()
        setListener()
    }



    private fun initView() {
        inflate(context, R.layout.picture_camera_view, this)
        setBackgroundColor(ContextCompat.getColor(context, R.color.camerax_color_black))
        mSwitchCamera.setImageResource(R.drawable.picture_ic_camera)
        displayManager.registerDisplayListener(customDisplay, null)
        customCapture.changeTime(0)
        if (!CustomCameraConfig.isOnlyCapture()) {
            orientationEventListener.star()
        }
    }

    private fun setListener(){
        mSwitchCamera.setOnClickListener { toggleCamera() }
        mCaptureLayout.captureListener = CallBackCustomCapture(this)
        mCaptureLayout.typeListener = CallBackCustomTypeCallBack(this)
    }


    override fun onOrientationChanged(orientation: Int) {
        mImageCapture?.targetRotation = orientation
        mImageAnalyzer?.targetRotation = orientation
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
        val mCameraProvider = this.mCameraProvider
        if (null != mCameraProvider && isBackCameraLevel3Device(mCameraProvider)) {
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
                .setTargetRotation(mCameraPreviewView.display.rotation)
                .build()
            // ImageCapture
            buildImageCapture()
            // VideoCapture
            buildVideoCapture()
            val useCase = UseCaseGroup.Builder()
            useCase.addUseCase(preview)
            mImageCapture?.run {
                useCase.addUseCase(this)
            }
            mVideoCapture?.run {
                useCase.addUseCase(this)
            }
            val useCaseGroup = useCase.build()
            // Must unbind the use-cases before rebinding them
            mCameraProvider?.unbindAll()
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            val camera = mCameraProvider?.bindToLifecycle((context as LifecycleOwner), cameraSelector, useCaseGroup)
            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(mCameraPreviewView.surfaceProvider)
            // setFlashMode
            mFlashLamp.setFlashMode()
            mCameraInfo = camera?.cameraInfo
            mCameraControl = camera?.cameraControl
            initCameraPreviewListener()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * bindCameraImageUseCases
     */
    override fun bindCameraImageUseCases() {
        try {
            val screenAspectRatio = aspectRatio(context.getScreenWidth(),context.getScreenHeight())
            val rotation = mCameraPreviewView.display.rotation
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
            mCameraProvider?.unbindAll()
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            val camera = mCameraProvider?.bindToLifecycle((context as LifecycleOwner), cameraSelector,
                preview, mImageCapture, mImageAnalyzer)
            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(mCameraPreviewView.surfaceProvider)
            // setFlashMode
            mFlashLamp.setFlashMode()
            mCameraInfo = camera?.cameraInfo
            mCameraControl = camera?.cameraControl
            initCameraPreviewListener()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * bindCameraVideoUseCases
     */
    override fun bindCameraVideoUseCases() {
        try {
            val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
            // Preview
            val preview = Preview.Builder()
                .setTargetRotation(mCameraPreviewView.display.rotation)
                .build()
            buildVideoCapture()
            // Must unbind the use-cases before rebinding them
            mCameraProvider?.unbindAll()
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            val camera = mCameraProvider?.bindToLifecycle((context as LifecycleOwner), cameraSelector, preview, mVideoCapture)
            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(mCameraPreviewView.surfaceProvider)
            mCameraInfo = camera?.cameraInfo
            mCameraControl = camera?.cameraControl
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
            .setTargetRotation(mCameraPreviewView.display.rotation)
            .build()
        mFlashLamp.bindImageCapture(mImageCapture)
    }

    private fun buildVideoCapture() {
        val videoBuilder = VideoCapture.Builder()
        videoBuilder.setTargetRotation(mCameraPreviewView.display.rotation)
        val videoFrameRate = CustomCameraConfig.getConfig().videoFrameRate
        val videoBitRate = CustomCameraConfig.getConfig().videoBitRate
        if (videoFrameRate > 0) {
            videoBuilder.setVideoFrameRate(videoFrameRate)
        }
        if (videoBitRate > 0) {
            videoBuilder.setBitRate(videoBitRate)
        }
        mVideoCapture = videoBuilder.build()
    }

    private fun initCameraPreviewListener() {
        val cameraXPreviewViewTouchListener = PreviewViewTouchListener(context, customTouch)
        mCameraPreviewView?.setOnTouchListener(cameraXPreviewViewTouchListener)
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val aspect = max(width, height).toDouble()
        val previewRatio = aspect / min(width, height)
        val ratio = abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)
        return if (ratio) AspectRatio.RATIO_4_3 else AspectRatio.RATIO_16_9
    }

    fun setCameraListener(cameraListener: CameraListener?) {
        mCameraListener = cameraListener
    }

    /**
     * 切换前后摄像头
     */
    fun toggleCamera() {
        lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) CameraSelector.LENS_FACING_BACK else CameraSelector.LENS_FACING_FRONT
        bindCameraUseCases()
    }

    /**
     * 关闭相机界面按钮
     *
     * @param clickListener
     */
    fun setOnCancelClickListener(clickListener: ClickListener?) {
        mCaptureLayout.leftClickListener = clickListener
    }

    fun setImageCallbackListener(mImageCallbackListener: ImageCallbackListener?) {
        this.mImageCallbackListener = mImageCallbackListener
    }

    /**
     * 重置状态
     */
    private fun resetState() {
        if (isImageCaptureEnabled()) {
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
    override fun startVideoPlay(url: String?) {
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
    override fun onCancelMedia() {
        val outputPath = CustomCameraConfig.getOutputPath(getCurrentActivity()?.intent)
        FileUtils.deleteFile(context, outputPath)
        stopVideoPlay()
        resetState()
        orientationEventListener.star()
    }

    override fun isImageCaptureEnabled(): Boolean = customCapture.isImageCaptureEnabled()

    override fun isMergeExternalStorageState(outputPath: String?): String? {
        var newOutputPath = outputPath
        try {
            // 对前置镜头导致的镜像进行一个纠正
            if (isImageCaptureEnabled() && isReversedHorizontal()) {
                val tempFile = FileUtils.createTempFile(getCurrentActivity(), false)
                if (FileUtils.copyPath(getCurrentActivity(), newOutputPath, tempFile.absolutePath)) {
                    newOutputPath = tempFile.absolutePath
                }
            }
            // 当用户未设置存储路径时，相片默认是存在外部公共目录下
            val externalSavedUri: Uri? = if (isImageCaptureEnabled()) {
                val contentValues = CameraUtils.buildImageContentValues(imageFormatForQ)
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            } else {
                val contentValues = CameraUtils.buildVideoContentValues(videoFormatForQ)
                context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
            }
            if (externalSavedUri == null) {
                return newOutputPath
            }
            val outputStream = context.contentResolver.openOutputStream(externalSavedUri)
            val isWriteFileSuccess =
                FileUtils.writeFileFromIS(FileInputStream(newOutputPath), outputStream)
            if (isWriteFileSuccess) {
                FileUtils.deleteFile(context, newOutputPath)
                CustomCameraConfig.putOutputUri(getCurrentActivity()?.intent, externalSavedUri)
                return externalSavedUri.toString()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return newOutputPath
    }

    /**
     * 停止视频播放
     */
    override fun stopVideoPlay() {
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
    public override fun onConfigurationChanged(newConfig: Configuration) = buildUseCameraCases()

    override fun onCreateCustomCameraView(): CustomCameraView?  = this
    override fun onCreateImagePreview(): ImageView? = mImagePreview
    override fun onCreateImagePreviewBg(): View? = mImagePreviewBg
    override fun onCreateCaptureLayout(): CaptureLayout? = mCaptureLayout
    override fun onCreateFlashImageView(): FlashImageView? = mFlashLamp
    override fun onCreateCurrentTimeTv(): TextView? = tvCurrentTime
    override fun onCreatePreviewView(): PreviewView? = mCameraPreviewView
    override fun onCreateFocusImageView(): FocusImageView? = focusImageView
    override fun onCreateSwitchCamera(): ImageView? = mSwitchCamera
    override fun onCreateTextureView(): TextureView? = mTextureView

    override fun onCreateImageCallbackListener(): ImageCallbackListener? = mImageCallbackListener
    override fun onCreateCameraListener(): CameraListener? = mCameraListener
    override fun onCreateOrientationEventListener(): OrientationEventListener? = orientationEventListener
    override fun onCreateImageCapture(): ImageCapture? = mImageCapture
    override fun onCreateCameraInfo(): CameraInfo? = mCameraInfo
    override fun onCreateCameraControl(): CameraControl? = mCameraControl
    override fun onCreateImageAnalysis(): ImageAnalysis? = mImageAnalyzer
    override fun onCreateVideoCapture(): VideoCapture? = mVideoCapture
    override fun onCreateExecutor(): Executor? = mainExecutor
    override fun onCreateProcessCameraProvider(): ProcessCameraProvider? = mCameraProvider
    override fun onCreateDisplayId(): Int? = this.display?.displayId?:0
    override fun isReversedHorizontal() = lensFacing == CameraSelector.LENS_FACING_FRONT
    override fun getCurrentContext(): Context = context
    override fun getCurrentActivity(): Activity? {
        val context = context
        return if (context is Activity) context else null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        displayManager.unregisterDisplayListener(customDisplay)
        orientationEventListener.stop()
    }

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

    }
}