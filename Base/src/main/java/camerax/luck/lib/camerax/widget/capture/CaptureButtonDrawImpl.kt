package camerax.luck.lib.camerax.widget.capture

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.SystemClock
import android.view.View
import xy.xy.base.R
import xy.xy.base.utils.exp.getResColor

/**
 * @author：luck
 * @date：2019-01-04 13:41
 * @describe：CaptureLayout
 */
class CaptureButtonDrawImpl(private val view: View, private val buttonSize: Int){
    private var animatorSet :AnimatorSet?=null
    private var inSideAnim :ValueAnimator?=null
    private val context by lazy { view.context }
    private val buttonRadius by lazy { buttonSize / 2.0f }//按钮半径

    private var outSideRadius = buttonRadius//按钮半径
    private var inSideRadius = buttonRadius*0.75F//按钮半径

    private val outSideAddSize by lazy { buttonSize / 8 }//长按外圆半径变大的Size
    private val insideReduceSize by lazy { buttonSize / 8 }//长安内圆缩小的Size
    private val strokeWidth by lazy { buttonSize / 15F }//长按外圆半径变大的Size

    private val outSideColor by lazy { context.getResColor(R.color.camerax_outside_color) }
    private val inSideColor by lazy { context.getResColor(R.color.camerax_inside_color) }
    private val progressColor by lazy { context.getResColor(R.color.camerax_progress_color) }

    private val progress:Float = 0F

    fun getButtonSize() = buttonSize + outSideAddSize * 2

    fun onDraw(state:CaptureStatus,canvas: Canvas?) {
        val center =  ((buttonSize + outSideAddSize * 2) / 2).toFloat()
        drawCircle(canvas,center,outSideRadius,outSideColor)
        drawCircle(canvas,center,inSideRadius,inSideColor)
        drawProgress(canvas,state,center)
    }

    private fun drawCircle(canvas: Canvas?,center:Float,radius:Float,color:Int){
        val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.color = color
        canvas?.drawCircle(center, center, radius, mPaint)
    }

    private fun drawProgress(canvas: Canvas?,state: CaptureStatus,center:Float){
        if (state != CaptureStatus.STATE_RECORDER_ING)return
        val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.color = progressColor
        mPaint.strokeWidth = (buttonSize / 15).toFloat()

        val radius = (buttonRadius + outSideAddSize - strokeWidth / 2)
        val rectFL = center - radius
        val rectFT = center - radius
        val rectFR = center + radius
        val rectFB = center + radius
        val rectF =  RectF(rectFL, rectFT, rectFR, rectFB)
        canvas?.drawArc(rectF, -90f, progress, false, mPaint)
    }

    fun startRecordAnimation(isRecord:Boolean,animatorAdapter: AnimatorListenerAdapter) {
        val outSideValue = buttonRadius + (if (isRecord) outSideAddSize else 0 )
        val inSideValue = buttonRadius * 0.75f - (if (isRecord) insideReduceSize else 0 )
        val outSideAnim = ValueAnimator.ofFloat(outSideRadius, outSideValue)
        val inSideAnim = ValueAnimator.ofFloat(inSideRadius, inSideValue)
        //外圆动画监听
        outSideAnim.addUpdateListener { animation: ValueAnimator ->
            outSideRadius = animation.animatedValue as Float
            view.invalidate()
        }
        inSideAnim.addUpdateListener { animation: ValueAnimator ->
            inSideRadius = animation.animatedValue as Float
            view.invalidate()
        }
        animatorSet?.cancel()
        animatorSet = AnimatorSet()
        animatorSet?.addListener(animatorAdapter)
        animatorSet?.playTogether(outSideAnim, inSideAnim)
        animatorSet?.duration = 100
        animatorSet?.start()
    }


    fun startCaptureAnimation(animatorAdapter: AnimatorListenerAdapter) {
        val tagValue = buttonRadius*0.75F
        inSideAnim?.cancel()
        inSideAnim = ValueAnimator.ofFloat(inSideRadius, tagValue * 0.75f, tagValue)
        inSideAnim?.addUpdateListener { animation: ValueAnimator ->
            inSideRadius = animation.animatedValue as Float
            view.invalidate()
        }
        inSideAnim?.addListener(animatorAdapter)
        inSideAnim?.duration = 50
        inSideAnim?.start()
    }
}