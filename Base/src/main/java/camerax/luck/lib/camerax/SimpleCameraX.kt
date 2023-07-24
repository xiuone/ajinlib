package camerax.luck.lib.camerax

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import camerax.luck.lib.camerax.type.CameraImageFormat
import camerax.luck.lib.camerax.type.CameraImageFormatQ
import camerax.luck.lib.camerax.type.CameraVideoFormat
import camerax.luck.lib.camerax.type.CameraVideoFormatQ
import camerax.luck.lib.camerax.type.CustomCameraType
import camerax.luck.lib.camerax.utils.FileUtils
import xy.xy.base.BuildConfig

/**
 * @author：luck
 * @date：2021/11/29 7:52 下午
 * @describe：SimpleCameraX
 */
class SimpleCameraX {
    //视频帧率，越高视频体积越大
    var videoFrameRate = 0
    //相机模式
    var buttonFeatures = CustomCameraType.BUTTON_STATE_BOTH
    //bit率， 越大视频体积越大
    var videoBitRate = 0
    //相机前置或后置
    var isCameraAroundState :Boolean = false
    //默认最大录制时间
    var maxDuration = 60 * 1000 + 500L
    //默认最小录制时间
    var minDuration = 1500L
    //是否显示录制时间
    var isDisplayRecordTime = false
    //是否显示录制时间
    var imageFormat = CameraImageFormat.PNG
    var imageFormatQ = CameraImageFormatQ.JPEG
    var videoFormat = CameraVideoFormat.VIDEO
    var videoFormatQ = CameraVideoFormatQ.VIDEO

    /**
     * 视频帧率，越高视频体积越大
     */
    fun setVideoFrameRate(videoFrameRate: Int): SimpleCameraX {
        this.videoFrameRate = videoFrameRate
        return this
    }

    /**
     * 相机模式
     */
    fun setCameraMode(cameraMode: CustomCameraType): SimpleCameraX {
        buttonFeatures = cameraMode
        return this
    }
    /**
     * bit率， 越大视频体积越大
     * @param bitRate example 3 * 1024 * 1024
     * @return
     */
    fun setVideoBitRate(bitRate: Int): SimpleCameraX {
        this.videoBitRate = bitRate
        return this
    }
    /**
     * 相机前置或后置
     * @param isCameraAroundState true 前置,默认false后置
     * @return
     */
    fun setCameraAroundState(isCameraAroundState: Boolean): SimpleCameraX {
        this.isCameraAroundState = isCameraAroundState
        return this
    }
    /**
     * 视频最大录制时长 单位：秒
     * @param maxSecond
     * @return
     */
    fun setRecordVideoMaxSecond(maxSecond: Long): SimpleCameraX {
        this.maxDuration = maxSecond * 1000L + 500
        return this
    }
    /**
     * 视频最小录制时长 单位：秒
     * @param minSecond
     * @return
     */
    fun setRecordVideoMinSecond(minSecond: Int): SimpleCameraX {
        this.minDuration = minSecond * 1000L
        return this
    }

    /**
     * 是否显示录制时间
     * @param isDisplayRecordTime
     * @return
     */
    fun isDisplayRecordChangeTime(isDisplayRecordTime: Boolean): SimpleCameraX {
        this.isDisplayRecordTime = isDisplayRecordTime
        return this
    }

    /**
     * 图片输出类型
     * 比如 xxx.jpg or xxx.png
     * @param format
     * @return
     */
    fun setCameraImageFormat(format: CameraImageFormat): SimpleCameraX {
        imageFormat = format
        return this
    }

    /**
     * Android Q 以上 图片输出类型
     * @param format 比如 "image/jpeg"
     * @return
     */
    fun setCameraImageFormatForQ(format: CameraImageFormatQ): SimpleCameraX {
        imageFormatQ = format
        return this
    }

    /**
     * 视频输出类型
     * @param format 比如 xxx.mp4
     * @return
     */
    fun setCameraVideoFormat(format: CameraVideoFormat): SimpleCameraX {
        videoFormat = format
        return this
    }

    /**
     * Android Q 以上 视频输出类型
     * @param format 比如 "video/mp4"
     * @return
     */
    fun setCameraVideoFormatForQ(format: CameraVideoFormatQ): SimpleCameraX {
        videoFormatQ = format
        return this
    }


    private val mCameraIntent: Intent = Intent()
    private val mCameraBundle: Bundle = Bundle()

    /**
     * Send the camera Intent from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    fun start(activity: Activity, requestCode: Int) {
        CustomCameraConfig.setConfig(this)
        activity.startActivityForResult(getIntent(activity), requestCode)
    }

    /**
     * Send the crop Intent with a custom request code
     *
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    fun start(context: Context, fragment: Fragment, requestCode: Int) {
        CustomCameraConfig.setConfig(this)
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

    companion object {
        fun of(): SimpleCameraX {
            return SimpleCameraX()
        }
    }
}