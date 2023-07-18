package camerax.luck.lib.camerax

/**
 * @author：luck
 * @date：2021/11/29 7:14 下午
 * @describe：CustomCameraConfig
 */
enum class CustomCameraType(val des:String) {
    BUTTON_STATE_BOTH("两者都可以"),
    BUTTON_STATE_ONLY_CAPTURE("只能拍照"),
    BUTTON_STATE_ONLY_RECORDER("只能录像"),
}