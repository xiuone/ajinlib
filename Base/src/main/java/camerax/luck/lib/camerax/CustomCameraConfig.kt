package camerax.luck.lib.camerax

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import camerax.luck.lib.camerax.type.CustomCameraType
import camerax.luck.lib.camerax.utils.FileUtils
import xy.xy.base.permission.IPermissionInterceptorCreateListener

/**
 * @author：luck
 * @date：2021/11/29 7:14 下午
 * @describe：CustomCameraConfig
 */
object CustomCameraConfig {
    private var simpleCameraX:SimpleCameraX? =null

    fun setConfig(simpleCameraX: SimpleCameraX?){
        this.simpleCameraX = simpleCameraX
    }

    fun getConfig() :SimpleCameraX{
        val simpleCameraX = simpleCameraX?:SimpleCameraX()
        this.simpleCameraX = simpleCameraX
        return simpleCameraX
    }

    /**
     * 按钮可执行的功能状态（拍照,录制,两者）
     */
    var buttonFeatures = CustomCameraType.BUTTON_STATE_BOTH

    /**
     * 权限拦截器
     */
    var interceptor: IPermissionInterceptorCreateListener? = null
    /**
     * 默认最大录制时间
     */
    var maxDuration = 60 * 1000 + 500L
    /**
     * 默认最小录制时间
     */
    var minDuration = 1500L
    /**
     * 相机是否准备好
     */
    var isTakeCamera = true

    /**
     * 是否只拍照
     */
    fun isOnlyCapture() :Boolean{
        return getConfig().buttonFeatures == CustomCameraType.BUTTON_STATE_ONLY_CAPTURE
    }

    /**
     * 是否可以拍照
     */
    fun haveCapture():Boolean {
        val config = getConfig()
        var status = config.buttonFeatures == CustomCameraType.BUTTON_STATE_ONLY_CAPTURE
        return status || config.buttonFeatures == CustomCameraType.BUTTON_STATE_BOTH
    }

    /**
     * 是否可以拍照
     */
    fun haveRecord():Boolean {
        val config = getConfig()
        var status = config.buttonFeatures == CustomCameraType.BUTTON_STATE_ONLY_RECORDER
        return status || config.buttonFeatures == CustomCameraType.BUTTON_STATE_BOTH
    }


    /**
     * 保存相机输出的路径
     *
     * @param intent
     * @param uri
     */
    fun putOutputUri(intent: Intent?, uri: Uri?) {
        intent?.putExtra(MediaStore.EXTRA_OUTPUT, uri)
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