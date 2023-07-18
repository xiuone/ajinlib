package camerax.luck.lib.camerax.widget

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import camerax.luck.lib.camerax.CustomCameraConfig
import camerax.luck.lib.camerax.CustomCameraType
import camerax.luck.lib.camerax.listener.CaptureListener
import com.hjq.permissions.XXPermissions
import xy.xy.base.permission.IPermissionInterceptorCreateListener

/**
 * @author：luck
 * @date：2019-01-04 13:41
 * @describe：CaptureLayout
 */
class CaptureButton(context: Context?,private val buttonSize: Int) : View(context) {
    var interceptor: IPermissionInterceptorCreateListener? = null
    private val animatorAdapter by lazy { AnimatorAdapter() }

    var buttonFeatures = CustomCameraType.BUTTON_STATE_BOTH//按钮可执行的功能状态（拍照,录制,两者）

    private var state = Status.STATE_IDLE//当前按钮状态
    private var progress = 0f//录制视频的进度
    private var currentRecordedTime = 0//记录当前录制的时间
    var maxDuration = CustomCameraConfig.DEFAULT_MAX_RECORD_VIDEO//录制视频最大时间长度
    var minDuration = CustomCameraConfig.DEFAULT_MIN_RECORD_VIDEO//最短录制时间限制

    private var eventY = 0f

    private val mPaint by lazy { Paint() }

    private val strokeWidth by lazy { (buttonSize / 15).toFloat() } //进度条宽度
    private val outSideAddSize by lazy { buttonSize / 8 }//长按外圆半径变大的Size
    private val insideReduceSize by lazy { buttonSize / 8 }//长安内圆缩小的Size
    private val centerX by lazy {  ((buttonSize + outSideAddSize * 2) / 2).toFloat() }
    private val centerY by lazy {  ((buttonSize + outSideAddSize * 2) / 2).toFloat() }

    private val buttonRadius by lazy { buttonSize / 2.0f }//按钮半径
    private var outSideRadius = buttonRadius//外圆半径
    private var inSideRadius = buttonRadius * 0.75f //内圆半径

    private val rectF by lazy { RectF(
        centerX - (buttonRadius + outSideAddSize - strokeWidth / 2),
        centerY - (buttonRadius + outSideAddSize - strokeWidth / 2),
        centerX + (buttonRadius + outSideAddSize - strokeWidth / 2),
        centerY + (buttonRadius + outSideAddSize - strokeWidth / 2)
    ) }

    var progressColor = -0x11e951ea
    private val longPressRunnable by lazy { LongPressRunnable() }

    //按钮回调接口
    var captureListener: CaptureListener? = null
    //计时器
    private val timer by lazy { RecordCountDownTimer(maxDuration.toLong(), (maxDuration / 360).toLong()) }
    var isTakeCamera = true

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(buttonSize + outSideAddSize * 2, buttonSize + outSideAddSize * 2)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.color = -0x11232324
        canvas.drawCircle(centerX, centerY, outSideRadius, mPaint)
        mPaint.color = -0x1
        canvas.drawCircle(centerX, centerY, inSideRadius, mPaint)
        if (state == Status.STATE_RECORDER_ING) {
            mPaint.color = progressColor
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = strokeWidth
            canvas.drawArc(rectF, -90f, progress, false, mPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isTakeCamera) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (event.pointerCount > 1 || state != Status.STATE_IDLE)
                    eventY = event.y
                    state = Status.STATE_PRESS
                    if (buttonFeatures != CustomCameraType.BUTTON_STATE_ONLY_CAPTURE) {
                        postDelayed(longPressRunnable, 500)
                    }
                }

                MotionEvent.ACTION_MOVE ->{
                    if (state == Status.STATE_RECORDER_ING &&
                        (buttonFeatures == CustomCameraType.BUTTON_STATE_ONLY_RECORDER
                                || buttonFeatures == CustomCameraType.BUTTON_STATE_BOTH)) {
                        captureListener?.recordZoom(eventY - event.y)
                    }
                }
                MotionEvent.ACTION_UP -> handlerPressByState()
            }
        }
        return true
    }

    private fun handlerPressByState() {
        removeCallbacks(longPressRunnable)
        when (state) {
            Status.STATE_PRESS ->{
                if (captureListener != null &&
                    (buttonFeatures == CustomCameraType.BUTTON_STATE_ONLY_CAPTURE ||
                            buttonFeatures == CustomCameraType.BUTTON_STATE_BOTH)) {
                    startCaptureAnimation(inSideRadius)
                }
            }
            Status.STATE_LONG_PRESS, Status.STATE_RECORDER_ING -> {
                if (XXPermissions.isGranted(context, Manifest.permission.RECORD_AUDIO)) {
                    timer.cancel()
                    recordEnd()
                }
            }
        }
        state = Status.STATE_IDLE
    }

    fun recordEnd() {
        if (currentRecordedTime < minDuration) {
            captureListener?.recordShort(currentRecordedTime.toLong())
        } else {
            captureListener?.recordEnd(currentRecordedTime.toLong())
        }
        resetRecordAnim()
    }

    private fun resetRecordAnim() {
        state = Status.STATE_BAN
        progress = 0f
        invalidate()
        startRecordAnimation(outSideRadius, buttonRadius, inSideRadius, buttonRadius * 0.75f)
    }

    private fun startCaptureAnimation(inSideStart: Float) {
        val inSideAnim = ValueAnimator.ofFloat(inSideStart, inSideStart * 0.75f, inSideStart)
        inSideAnim.addUpdateListener { animation: ValueAnimator ->
            inSideRadius = animation.animatedValue as Float
            invalidate()
        }
        inSideAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                captureListener?.takePictures()
                state = Status.STATE_BAN
            }
        })
        inSideAnim.duration = 50
        inSideAnim.start()
    }

    private fun startRecordAnimation(outsideStart: Float, outsideEnd: Float,
                                     inSideStart: Float, inSideEnd: Float) {

        val outSideAnim = ValueAnimator.ofFloat(outsideStart, outsideEnd)
        val inSideAnim = ValueAnimator.ofFloat(inSideStart, inSideEnd)
        //外圆动画监听
        outSideAnim.addUpdateListener { animation: ValueAnimator ->
            outSideRadius = animation.animatedValue as Float
            invalidate()
        }
        inSideAnim.addUpdateListener { animation: ValueAnimator ->
            inSideRadius = animation.animatedValue as Float
            invalidate()
        }
        val set = AnimatorSet()
        set.addListener(animatorAdapter)
        set.playTogether(outSideAnim, inSideAnim)
        set.duration = 100
        set.start()
    }

    private fun updateProgress(millisUntilFinished: Long) {
        currentRecordedTime = (maxDuration - millisUntilFinished).toInt()
        progress = 360f - millisUntilFinished / maxDuration.toFloat() * 360f
        invalidate()
        captureListener?.changeTime(millisUntilFinished)
    }

    fun resetState() {
        state = Status.STATE_IDLE
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
            if (state == Status.STATE_LONG_PRESS) {
                captureListener?.recordStart()
                state = Status.STATE_RECORDER_ING
                timer.start()
            } else {
                state = Status.STATE_IDLE
            }
        }
    }

    private inner class RecordCountDownTimer(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUntilFinished: Long) = updateProgress(millisUntilFinished)

        override fun onFinish() = recordEnd()
    }

    private inner class LongPressRunnable : Runnable {
        override fun run() {
            state = Status.STATE_LONG_PRESS
            if (XXPermissions.isGranted(context, Manifest.permission.RECORD_AUDIO)) {
                startRecordAnimation(
                    outSideRadius, outSideRadius + outSideAddSize,
                    inSideRadius, inSideRadius - insideReduceSize
                )
            } else {
                handlerPressByState()
                XXPermissions.with(context)
                    .permission(Manifest.permission.RECORD_AUDIO)
                    .interceptor(interceptor?.onCreateIPermissionInterceptor())
                    .request { _, _ -> postDelayed(longPressRunnable, 500) }
            }
        }
    }


    enum class Status(val des:String){
        STATE_IDLE("空闲状态"),
        STATE_PRESS("按下状态"),
        STATE_LONG_PRESS("长按状态"),
        STATE_RECORDER_ING("录制状态"),
        STATE_BAN("禁止状态"),
    }
}