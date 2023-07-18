package camerax.luck.lib.camerax

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import camerax.luck.lib.camerax.utils.FileUtils
import xy.xy.base.BuildConfig

/**
 * @author：luck
 * @date：2021/11/29 7:52 下午
 * @describe：SimpleCameraX
 */
class SimpleCameraX {
    private val mCameraIntent: Intent = Intent()
    private val mCameraBundle: Bundle = Bundle()

    /**
     * Send the camera Intent from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    fun start(activity: Activity, requestCode: Int) {
        activity.startActivityForResult(getIntent(activity), requestCode)
    }

    /**
     * Send the crop Intent with a custom request code
     *
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    fun start(context: Context, fragment: Fragment, requestCode: Int) {
        fragment.startActivityForResult(getIntent(context), requestCode)
    }

    /**
     * Get Intent to start [PictureCameraActivity]
     *
     * @return Intent for [PictureCameraActivity]
     */
    fun getIntent(context: Context): Intent {
        mCameraIntent.setClass(context, PictureCameraActivity::class.java)
        mCameraIntent.putExtras(mCameraBundle)
        return mCameraIntent
    }

    /**
     * 相机模式
     *
     * @param cameraMode Use [CustomCameraConfig]
     * @return
     */
    fun setCameraMode(cameraMode: Int): SimpleCameraX {
        mCameraBundle.putInt(EXTRA_CAMERA_MODE, cameraMode)
        return this
    }

    /**
     * 视频帧率，越高视频体积越大
     *
     * @param videoFrameRate 0~100
     * @return
     */
    fun setVideoFrameRate(videoFrameRate: Int): SimpleCameraX {
        mCameraBundle.putInt(EXTRA_VIDEO_FRAME_RATE, videoFrameRate)
        return this
    }

    /**
     * bit率， 越大视频体积越大
     *
     * @param bitRate example 3 * 1024 * 1024
     * @return
     */
    fun setVideoBitRate(bitRate: Int): SimpleCameraX {
        mCameraBundle.putInt(EXTRA_VIDEO_BIT_RATE, bitRate)
        return this
    }

    /**
     * 相机前置或后置
     *
     * @param isCameraAroundState true 前置,默认false后置
     * @return
     */
    fun setCameraAroundState(isCameraAroundState: Boolean): SimpleCameraX {
        mCameraBundle.putBoolean(EXTRA_CAMERA_AROUND_STATE, isCameraAroundState)
        return this
    }

    /**
     * 拍照自定义输出路径
     *
     * @param outputPath
     * @return
     */
    fun setOutputPathDir(outputPath: String?): SimpleCameraX {
        mCameraBundle.putString(EXTRA_OUTPUT_PATH_DIR, outputPath)
        return this
    }

    /**
     * 拍照输出文件名
     *
     * @param fileName
     * @return
     */
    fun setCameraOutputFileName(fileName: String?): SimpleCameraX {
        mCameraBundle.putString(EXTRA_CAMERA_FILE_NAME, fileName)
        return this
    }

    /**
     * 视频最大录制时长 单位：秒
     *
     * @param maxSecond
     * @return
     */
    fun setRecordVideoMaxSecond(maxSecond: Int): SimpleCameraX {
        mCameraBundle.putInt(EXTRA_RECORD_VIDEO_MAX_SECOND, maxSecond * 1000 + 500)
        return this
    }

    /**
     * 视频最小录制时长 单位：秒
     *
     * @param minSecond
     * @return
     */
    fun setRecordVideoMinSecond(minSecond: Int): SimpleCameraX {
        mCameraBundle.putInt(EXTRA_RECORD_VIDEO_MIN_SECOND, minSecond * 1000)
        return this
    }

    /**
     * 图片输出类型
     *
     *
     * 比如 xxx.jpg or xxx.png
     *
     *
     * @param format
     * @return
     */
    fun setCameraImageFormat(format: String?): SimpleCameraX {
        mCameraBundle.putString(EXTRA_CAMERA_IMAGE_FORMAT, format)
        return this
    }

    /**
     * Android Q 以上 图片输出类型
     *
     *
     * 比如 "image/jpeg"
     *
     *
     * @param format
     * @return
     */
    fun setCameraImageFormatForQ(format: String?): SimpleCameraX {
        mCameraBundle.putString(EXTRA_CAMERA_IMAGE_FORMAT_FOR_Q, format)
        return this
    }

    /**
     * 视频输出类型
     *
     *
     * 比如 xxx.mp4
     *
     *
     * @param format
     * @return
     */
    fun setCameraVideoFormat(format: String?): SimpleCameraX {
        mCameraBundle.putString(EXTRA_CAMERA_VIDEO_FORMAT, format)
        return this
    }

    /**
     * Android Q 以上 视频输出类型
     *
     *
     * 比如 "video/mp4"
     *
     *
     * @param format
     * @return
     */
    fun setCameraVideoFormatForQ(format: String?): SimpleCameraX {
        mCameraBundle.putString(EXTRA_CAMERA_VIDEO_FORMAT_FOR_Q, format)
        return this
    }

    /**
     * 拍照Loading的色值
     *
     * @param color
     * @return
     */
    fun setCaptureLoadingColor(color: Int): SimpleCameraX {
        mCameraBundle.putInt(EXTRA_CAPTURE_LOADING_COLOR, color)
        return this
    }

    /**
     * 是否显示录制时间
     *
     * @param isDisplayRecordTime
     * @return
     */
    fun isDisplayRecordChangeTime(isDisplayRecordTime: Boolean): SimpleCameraX {
        mCameraBundle.putBoolean(EXTRA_DISPLAY_RECORD_CHANGE_TIME, isDisplayRecordTime)
        return this
    }

    /**
     * 是否手动点击对焦
     *
     * @param isManualFocus
     * @return
     */
    fun isManualFocusCameraPreview(isManualFocus: Boolean): SimpleCameraX {
        mCameraBundle.putBoolean(EXTRA_MANUAL_FOCUS, isManualFocus)
        return this
    }

    /**
     * 是否可缩放相机
     *
     * @param isZoom
     * @return
     */
    fun isZoomCameraPreview(isZoom: Boolean): SimpleCameraX {
        mCameraBundle.putBoolean(EXTRA_ZOOM_PREVIEW, isZoom)
        return this
    }

    /**
     * 是否自动纠偏
     *
     * @param isAutoRotation
     * @return
     */
    fun isAutoRotation(isAutoRotation: Boolean): SimpleCameraX {
        mCameraBundle.putBoolean(EXTRA_AUTO_ROTATION, isAutoRotation)
        return this
    }

    companion object {
        private const val EXTRA_PREFIX = BuildConfig.LIBRARY_PACKAGE_NAME
        const val EXTRA_OUTPUT_PATH_DIR = EXTRA_PREFIX + ".OutputPathDir"
        const val EXTRA_CAMERA_FILE_NAME = EXTRA_PREFIX + ".CameraFileName"
        const val EXTRA_CAMERA_MODE = EXTRA_PREFIX + ".CameraMode"
        const val EXTRA_VIDEO_FRAME_RATE = EXTRA_PREFIX + ".VideoFrameRate"
        const val EXTRA_VIDEO_BIT_RATE = EXTRA_PREFIX + ".VideoBitRate"
        const val EXTRA_CAMERA_AROUND_STATE = EXTRA_PREFIX + ".CameraAroundState"
        const val EXTRA_RECORD_VIDEO_MAX_SECOND = EXTRA_PREFIX + ".RecordVideoMaxSecond"
        const val EXTRA_RECORD_VIDEO_MIN_SECOND = EXTRA_PREFIX + ".RecordVideoMinSecond"
        const val EXTRA_CAMERA_IMAGE_FORMAT = EXTRA_PREFIX + ".CameraImageFormat"
        const val EXTRA_CAMERA_IMAGE_FORMAT_FOR_Q = EXTRA_PREFIX + ".CameraImageFormatForQ"
        const val EXTRA_CAMERA_VIDEO_FORMAT = EXTRA_PREFIX + ".CameraVideoFormat"
        const val EXTRA_CAMERA_VIDEO_FORMAT_FOR_Q = EXTRA_PREFIX + ".CameraVideoFormatForQ"
        const val EXTRA_CAPTURE_LOADING_COLOR = EXTRA_PREFIX + ".CaptureLoadingColor"
        const val EXTRA_DISPLAY_RECORD_CHANGE_TIME = EXTRA_PREFIX + ".DisplayRecordChangeTime"
        const val EXTRA_MANUAL_FOCUS = EXTRA_PREFIX + ".isManualFocus"
        const val EXTRA_ZOOM_PREVIEW = EXTRA_PREFIX + ".isZoomPreview"
        const val EXTRA_AUTO_ROTATION = EXTRA_PREFIX + ".isAutoRotation"
        fun of(): SimpleCameraX {
            return SimpleCameraX()
        }

        /**
         * 保存相机输出的路径
         *
         * @param intent
         * @param uri
         */
        fun putOutputUri(intent: Intent, uri: Uri?) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }

        /**
         * 获取保存相机输出的路径
         *
         * @param intent
         * @return
         */
        fun getOutputPath(intent: Intent?): String? {
            val uri = intent?.getParcelableExtra<Uri>(MediaStore.EXTRA_OUTPUT) ?: return ""
            return if (FileUtils.isContent(uri.toString())) uri.toString() else uri.path
        }
    }
}