package camerax.luck.lib.camerax.widget.capture

enum class CaptureStatus(val des:String){
    STATE_IDLE("空闲状态"),
    STATE_PRESS("按下状态"),
    STATE_LONG_PRESS("长按状态"),
    STATE_RECORDER_ING("录制状态"),
    STATE_BAN("禁止状态"),
}