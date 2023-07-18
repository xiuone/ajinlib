package camerax.luck.lib.camerax

/**
 * @author：luck
 * @date：2021/11/29 7:14 下午
 * @describe：CustomCameraConfig
 */
object CustomCameraConfig {
    /**
     * 两者都可以
     */
    const val BUTTON_STATE_BOTH = 0

    /**
     * 只能拍照
     */
    const val BUTTON_STATE_ONLY_CAPTURE = 1

    /**
     * 只能录像
     */
    const val BUTTON_STATE_ONLY_RECORDER = 2

    /**
     * 默认最大录制时间
     */
    const val DEFAULT_MAX_RECORD_VIDEO = 60 * 1000 + 500

    /**
     * 默认最小录制时间
     */
    const val DEFAULT_MIN_RECORD_VIDEO = 1500
    const val SP_NAME = "PictureSpUtils"
}