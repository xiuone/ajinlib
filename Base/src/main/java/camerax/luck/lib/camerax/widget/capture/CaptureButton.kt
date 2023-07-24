package camerax.luck.lib.camerax.widget.capture

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Canvas
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import camerax.luck.lib.camerax.CustomCameraConfig
import camerax.luck.lib.camerax.listener.CaptureListener
import com.hjq.permissions.XXPermissions

/**
 * @author：luck
 * @date：2019-01-04 13:41
 * @describe：CaptureLayout
 */
class CaptureButton(context: Context,private val buttonSize: Int) : View(context),
    CaptureButtonRecordCountDownTimer.CaptureButtonRecordListener {
    //绘制
    private val drawImpl by lazy { CaptureButtonDrawImpl(this,buttonSize) }
    //计时器
    private val timer by lazy { CaptureButtonRecordCountDownTimer(this) }
    private val animatorAdapter by lazy { AnimatorAdapter() }

    private var state = CaptureStatus.STATE_IDLE//当前按钮状态
    private var progress = 0f//录制视频的进度
    private var currentRecordedTime = 0//记录当前录制的时间


    private val longPressRunnable by lazy { LongPressRunnable() }

    //按钮回调接口
    var captureListener: CaptureListener? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val buttonSize = drawImpl.getButtonSize()
        setMeasuredDimension(buttonSize, buttonSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawImpl.onDraw(state, canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!CustomCameraConfig.isTakeCamera)return true
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.pointerCount > 1 || state != CaptureStatus.STATE_IDLE) return true
                state = CaptureStatus.STATE_PRESS
                if (!CustomCameraConfig.isOnlyCapture()) {
                    postDelayed(longPressRunnable, 500)
                }
            }
            MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL -> handlerPressByState()
        }
        return true
    }

    private fun handlerPressByState() {
        removeCallbacks(longPressRunnable)
        when (state) {
            CaptureStatus.STATE_PRESS ->{
                if (CustomCameraConfig.haveCapture()) {
                    drawImpl.startCaptureAnimation(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            super.onAnimationStart(animation)
                            captureListener?.takePictures()
                            state = CaptureStatus.STATE_BAN
                        }
                    })
                }
            }
            CaptureStatus.STATE_LONG_PRESS, CaptureStatus.STATE_RECORDER_ING -> {
                if (XXPermissions.isGranted(context, Manifest.permission.RECORD_AUDIO)) {
                    timer.cancel()
                    recordEnd()
                }
            }
        }
        state = CaptureStatus.STATE_IDLE
    }

    /**
     * 录制进度
     */
    override fun updateProgress(millisUntilFinished: Long) {
        currentRecordedTime = (CustomCameraConfig.maxDuration - millisUntilFinished).toInt()
        progress = 360f - millisUntilFinished / CustomCameraConfig.maxDuration.toFloat() * 360f
        invalidate()
        captureListener?.changeTime(millisUntilFinished)
    }

    /**
     * 录制结束
     */
    override fun recordEnd() {
        if (currentRecordedTime < CustomCameraConfig.minDuration) {
            captureListener?.recordShort(currentRecordedTime.toLong())
        } else {
            captureListener?.recordEnd(currentRecordedTime.toLong())
        }
        resetRecordAnim()
    }


    private fun resetRecordAnim() {
        state = CaptureStatus.STATE_BAN
        progress = 0f
        invalidate()
        drawImpl.startRecordAnimation(false,animatorAdapter)
    }


    fun resetState() {
        state = CaptureStatus.STATE_IDLE
    }

    private inner class AnimatorAdapter :AnimatorListenerAdapter(){
        private val lastClickTime: Long = 0
        private val TIME: Long = 800
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            val time = SystemClock.elapsedRealtime()
            if (time - lastClickTime < TIME) {
                return 
            }
            //设置为录制状态
            if (state == CaptureStatus.STATE_LONG_PRESS) {
                captureListener?.recordStart()
                state = CaptureStatus.STATE_RECORDER_ING
                timer.start()
            } else {
                state = CaptureStatus.STATE_IDLE
            }
        }
    }

    private inner class LongPressRunnable : Runnable {
        override fun run() {
            state = CaptureStatus.STATE_LONG_PRESS
            if (XXPermissions.isGranted(context, Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA)) {
                drawImpl.startRecordAnimation(true,animatorAdapter)
            } else {
                handlerPressByState()
                XXPermissions.with(context)
                    .permission(Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA)
                    .interceptor(CustomCameraConfig.interceptor?.onCreateIPermissionInterceptor())
                    .request { _, _ -> postDelayed(longPressRunnable, 500) }
            }
        }
    }
}