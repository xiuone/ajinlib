package camerax.luck.lib.camerax.widget.capture

import android.os.CountDownTimer
import camerax.luck.lib.camerax.CustomCameraConfig

class CaptureButtonRecordCountDownTimer(private val listener:CaptureButtonRecordListener):
    CountDownTimer(CustomCameraConfig.maxDuration, CustomCameraConfig.maxDuration / 360) {

    override fun onTick(millisUntilFinished: Long) = listener.updateProgress(millisUntilFinished)

    override fun onFinish() = listener.recordEnd()


    interface CaptureButtonRecordListener{
        fun updateProgress(millisUntilFinished:Long)
        fun recordEnd()
    }
}