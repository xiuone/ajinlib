package com.xy.baselib.widget.bar.switch

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.xy.baselib.R
import com.xy.utils.addAlpha
import com.xy.utils.drawCenterText
import com.xy.utils.getResColor
import com.xy.utils.showToast
import kotlin.math.abs
import kotlin.math.min

class SwitchBarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr) ,LifecycleObserver,ValueAnimator.AnimatorUpdateListener{
    private val defaultGraColor by lazy { context.getResColor(R.color.gray_9999) }
    private val defaultWhiteColor by lazy { context.getResColor(R.color.white) }
    private val defaultBlueColor by lazy { context.getResColor(R.color.blue_078a) }
    //背景颜色
    private var mDefaultBackgroundColor = defaultGraColor
    private var mSelectBackgroundColor = defaultGraColor
    //操作杆颜色
    private var thumbOffColor = defaultWhiteColor
    private var thumbOpenColor = defaultBlueColor
    //是否绘制文字
    private var isDrawText = false;

    private var thumbOffText = ""
    private var thumbOffTextColor = defaultWhiteColor
    private var thumbOffTextSize = 15F

    private var thumbOpenText = ""
    private var thumbOpenTextColor = defaultWhiteColor
    private var thumbOpenTextSize = 15F

    private var thumbShadowColor : Int = defaultGraColor
    private var thumbShadowRadius : Float = 0F
    private var center = -1F
    private var valueAnim : ValueAnimator?= null
    private var isSwitchSelect = false;
    var switchBarListener:SwitchBarListener?=null
    
    init {
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchBarView)
            mDefaultBackgroundColor = typedArray.getColor(R.styleable.SwitchBarView_switch_default_background_color, defaultGraColor)
            mSelectBackgroundColor = typedArray.getColor(R.styleable.SwitchBarView_switch_select_background_color, defaultBlueColor)
            thumbOffColor = typedArray.getColor(R.styleable.SwitchBarView_switch_thumb_off_color, defaultWhiteColor)
            thumbOpenColor = typedArray.getColor(R.styleable.SwitchBarView_switch_thumb_open_color, defaultWhiteColor)
            isDrawText = typedArray.getBoolean(R.styleable.SwitchBarView_switch_draw_text, false)

            thumbOffText = typedArray.getString(R.styleable.SwitchBarView_switch_thumb_off_text)?:""
            thumbOffTextSize = typedArray.getDimension(R.styleable.SwitchBarView_switch_thumb_off_text_size,thumbOpenTextSize)
            thumbOffTextColor = typedArray.getColor(R.styleable.SwitchBarView_switch_thumb_off_text_color,thumbOpenTextColor)

            thumbOpenText = typedArray.getString(R.styleable.SwitchBarView_switch_thumb_open_text)?:""
            thumbOpenTextSize = typedArray.getDimension(R.styleable.SwitchBarView_switch_thumb_open_text_size,thumbOffTextSize)
            thumbOpenTextColor = typedArray.getColor(R.styleable.SwitchBarView_switch_thumb_open_text_color,thumbOpenTextColor)

            thumbShadowColor = typedArray.getColor(R.styleable.SwitchBarView_switch_thumb_shadow_color, defaultGraColor)
            thumbShadowRadius = typedArray.getDimension(R.styleable.SwitchBarView_switch_thumb_shadow_radius, thumbShadowRadius)
            isSwitchSelect = typedArray.getBoolean(R.styleable.SwitchBarView_switch_is_select, false)
        }
    }

    private fun minX ():Int = height/2
    private fun maxX ():Int = width-height/2
    private fun mixY ():Int = width/2
    private fun maxY ():Int = height - width/2
    private fun isH ():Boolean = height < width//是否是横向

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        valueAnim?.cancel()
        val event = event?:return true;
        center = if (isH()) event.x else event.y
        resetCenter()
        startAnim(event)
        invalidate()
        return true
    }


    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        valueAnim?.cancel()
        isSwitchSelect = selected
        if (width <= 0 || height <= 0 ) {
            invalidate()
        }else{
            if (center <=0){
                center = if (isH()) height/2F else width/2F
            }
            var duration = 300L
            when{
                isH() && !isSwitchSelect->{
                    valueAnim = ObjectAnimator.ofFloat(center,height/2F)
                    duration = abs(height/2F-center).toLong()
                }
                isH() && isSwitchSelect->{
                    valueAnim = ObjectAnimator.ofFloat(center,width-height/2F)
                    duration = abs(width-height/2F-center).toLong()
                }
                !isH() && !isSwitchSelect->{
                    valueAnim = ObjectAnimator.ofFloat(center,width/2F)
                    duration = abs(width/2F-center).toLong()
                }
                !isH() && isSwitchSelect->{
                    valueAnim = ObjectAnimator.ofFloat(center,height-width/2F)
                    duration = abs(height-width/2F-center).toLong()
                }
            }
            valueAnim?.addUpdateListener (this)
            valueAnim?.duration = duration
            valueAnim?.start()
        }
    }

    /**
     * 判断并开启动画
     */
    private fun startAnim(event: MotionEvent){
        if (event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP){
            val isSwitchSelect = if (isH()){
                center > width/2
            }else{
                center > height/2
            }
            if (this.isSwitchSelect != isSwitchSelect) {
                context.showToast("当前状态$isSwitchSelect")
                switchBarListener?.onSwitchCallBack(isSwitchSelect)
            }
            isSelected = isSwitchSelect;
        }
    }

    /**
     * 重制中心位置
     */
    private fun resetCenter(){
        center = if (isH()){
            when {
                center < minX() -> minX().toFloat()
                center > maxX() -> maxX().toFloat()
                else -> center
            }
        }else{
            when {
                center < mixY() -> mixY().toFloat()
                center > maxY() -> maxY().toFloat()
                else -> center
            }
        }

    }
    
    

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val canvas = canvas?:return
        drawBackground(canvas)
        drawText(canvas)
        drawThumb(canvas)
    }

    /**
     * 绘制文字
     */
    private fun drawText(canvas: Canvas){
        if (!isDrawText)return
        val  offPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        offPaint.color = thumbOffTextColor
        offPaint.textSize = thumbOffTextSize

        val  openPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        openPaint.color = thumbOpenTextColor
        openPaint.textSize = thumbOpenTextSize

        if (!thumbOffText.isNullOrEmpty()){
            val center = min(height/2,width/2).toFloat()
            canvas.drawCenterText(center,center,thumbOffText,offPaint)
        }

        if (!thumbOpenText.isNullOrEmpty()){
            val center = min(height/2,width/2).toFloat()
            val centerX = if (isH()) (width - center) else center
            val centerY = if (!isH()) (height - center) else center
            canvas.drawCenterText(centerX,centerY,thumbOpenText,openPaint)
        }
    }

    /**
     * 绘制背景
     */
    private fun drawBackground(canvas: Canvas){
        val radius = min(height,width)/2F
        val rectF = RectF(0F,0F,width.toFloat(),height.toFloat())
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        var backgroundColor = if (isH()){
                when{
                    center<=0 && !isSwitchSelect-> mDefaultBackgroundColor
                    center <=0 && isSwitchSelect-> mSelectBackgroundColor
                    center < width/2-> mDefaultBackgroundColor
                    else-> mSelectBackgroundColor
                }
            }else{
                when{
                    center<=0 && !isSwitchSelect-> mDefaultBackgroundColor
                    center <=0 && isSwitchSelect-> mSelectBackgroundColor
                    center < height/2-> mDefaultBackgroundColor
                    else-> mSelectBackgroundColor
                }
            }
        paint.color = backgroundColor
        canvas.drawRoundRect(rectF,radius,radius,paint)
    }

    /**
     * 绘制操作杆
     */
    private fun drawThumb(canvas: Canvas){
        var radius = min(height,width) - thumbShadowRadius*2
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setShadowLayer(thumbShadowRadius,0F,0F,thumbShadowColor.addAlpha("77"))
        if (isH()){
            center = if (center<=0 && !isSwitchSelect) height/2F else if(center <=0 && isSwitchSelect) width- height/2F else center
            paint.color = if (center < width/2) thumbOffColor else thumbOpenColor
            canvas.drawCircle(center,height/2F,radius/2,paint)
        }else{
            center = if (center<=0 && !isSwitchSelect) width/2F else if (center <=0 && isSwitchSelect) height - width/2F else center
            paint.color = if (center < height/2) thumbOffColor else thumbOpenColor
            canvas.drawCircle(width/2F,center,radius/2,paint)
        }
    }

    /**
     * 动画改变
     */
    override fun onAnimationUpdate(animation: ValueAnimator?) {
        animation?.run {
            center = animatedValue as Float
            resetCenter()
            invalidate()
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroyed(owner: LifecycleOwner) {
        animation?.cancel()
    }
}