package xy.xy.base.widget.sidebar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import xy.xy.base.utils.exp.dp2px
import xy.xy.base.utils.exp.drawCenterText
import xy.xy.base.R
import java.util.*


class WaveSideBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val mIndexItems by lazy { ArrayList<String>() }
    private val pointHashMap by lazy { HashMap<String,RectF>() }

    private var isTouch = false

    private var selectItem :String ? = null

    //选中事件监听
    var onSelectIndexItemListener: OnSelectIndexItemListener? = null


    //字体大小
    var mTextSize: Float
    var multiple:Int = 3


    //字体颜色
    var commonTextColor: Int
    //未选中的字体大小
    var commonBackround: Int

    //字体颜色
    var selectTextColor: Int
    //未选中的字体大小
    var selectBackround: Int


    var selectHintMultiple: Int
    var selectHintRadius: Float

    var selectHintTextSize: Float
    //字体颜色
    var selectHintTextColor: Int
    //未选中的字体大小
    var selectHintBackround: Int
    
    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveSideBar)
        multiple = typedArray.getInteger(R.styleable.WaveSideBar_sidebar_multiple, 3)

        mTextSize = typedArray.getDimension(R.styleable.WaveSideBar_sidebar_text_size, context.dp2px(14F).toFloat())
        commonTextColor = typedArray.getColor(R.styleable.WaveSideBar_sidebar_text_common_color, Color.GRAY)
        commonBackround = typedArray.getColor(R.styleable.WaveSideBar_sidebar_text_common_background, Color.TRANSPARENT)

        selectTextColor = typedArray.getColor(R.styleable.WaveSideBar_sidebar_text_select_color, commonTextColor)
        selectBackround = typedArray.getColor(R.styleable.WaveSideBar_sidebar_text_select_background, commonBackround)

        selectHintMultiple = typedArray.getInteger(R.styleable.WaveSideBar_sidebar_select_hint_multiple, 3)
        selectHintRadius = typedArray.getDimension(R.styleable.WaveSideBar_sidebar_text_select_hint_radius, context.dp2px(10F).toFloat())

        selectHintTextSize = typedArray.getDimension(R.styleable.WaveSideBar_sidebar_text_select_hint_size, context.dp2px(16F).toFloat())
        selectHintTextColor = typedArray.getColor(R.styleable.WaveSideBar_sidebar_text_select_hint_color, Color.TRANSPARENT)
        selectHintBackround = typedArray.getColor(R.styleable.WaveSideBar_sidebar_text_select_hint_background, Color.TRANSPARENT)
        typedArray.recycle()
    }


    private fun getUseSize() = multiple*mTextSize
    private fun getSelectHintUseSize() = selectHintMultiple*selectHintTextSize

    /**
     * 获取绘制文字的笔
     */
    private fun getTextPaint(select:Boolean) :Paint{
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = mTextSize
        paint.color = if (select) selectTextColor else commonTextColor
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.CENTER
        return paint
    }

    /**
     * 获取绘制背景的笔
     */
    private fun getBackgroundPaint(select: Boolean):Paint{
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = mTextSize
        paint.color = if (select) selectBackround else commonBackround
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.CENTER
        return paint
    }

    /**
     * 获取选中的提示文字画笔
     */
    private fun getSelectHintTextPaint() :Paint{
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = selectHintTextSize
        paint.color = selectHintTextColor
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.CENTER
        return paint
    }
    /**
     * 获取选中的提示背景画笔
     */
    private fun getSelectHintBackgroundPaint():Paint{
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = mTextSize
        paint.color = selectHintBackround
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.CENTER
        return paint
    }


    /**
     * 获取横向的中心坐标
     */
    private fun hPoint() = width - paddingRight - getUseSize()/2F

    /**
     * 获取竖向的中心坐标
     */
    private fun vPoint(index:Int) :Float{
        val useHeight = mIndexItems.size * getUseSize()
        val startY = (height - useHeight)/2F
        return startY + getUseSize()/2 + getUseSize() * index
    }

    /**
     * 获取触碰区域
     */
    private fun getRectF(index: Int) :RectF{
        val left = hPoint() - getUseSize()/2F
        val right = (width - paddingRight).toFloat()
        val top = vPoint(index) - getUseSize()/2F
        val bottom = vPoint(index) + getUseSize()/2F
        return RectF(left,top,right,bottom)
    }

    /**
     * 绘制
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        synchronized(this){
            pointHashMap.clear()
            for ((index,item) in mIndexItems.withIndex()){
                val isSelect = (item == selectItem) && isTouch
                drawBackGround(canvas, index,isSelect)
                drawText(canvas,index,item,isSelect)
                pointHashMap[item] = getRectF(index)
                if (isSelect){
                    drawSelect(canvas,item)
                }
            }
        }
    }

    /**
     * 绘制文字
     */
    private fun drawText(canvas: Canvas?,index: Int,text:String,isSelect:Boolean){
        val textPaint = getTextPaint(isSelect)
        val centerX = hPoint()
        val centerY = vPoint(index)
        canvas?.drawCenterText(centerX,centerY,text,textPaint)
    }

    /**
     * 绘制背景
     */
    private fun drawBackGround(canvas: Canvas?,index: Int,isSelect:Boolean){
        val backPaint = getBackgroundPaint(isSelect)
        val centerX = hPoint()
        val centerY = vPoint(index)
        canvas?.drawCircle(centerX,centerY,getUseSize()/2,backPaint)
    }

    /**
     * 绘制选中状体
     */
    private fun drawSelect(canvas: Canvas?,selectItem: String){
        val centerX = width/2F
        val centerY = height/2F
        val left = centerX - getSelectHintUseSize()/2
        val right = centerX + getSelectHintUseSize() /2
        val top = centerY - getSelectHintUseSize()/2
        val bottom = centerY + getSelectHintUseSize()/2
        val rectF = RectF(left,top,right,bottom)
        val radius = selectHintRadius
        Log.e("==``","$left====$top====$right=====$bottom====$radius")
        canvas?.drawRoundRect(rectF,radius,radius,getSelectHintBackgroundPaint())
        canvas?.drawCenterText(centerX,centerY,selectItem,getSelectHintTextPaint())
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null)return super.onTouchEvent(event)
        val eventX = event.x
        val eventY = event.y
        when(event.action){
            MotionEvent.ACTION_DOWN->{
                isTouch = checkIsTouch(eventX, eventY)
                postInvalidate()
                return isTouch || super.onTouchEvent(event)
            }
            MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL->{
                if (isTouch) {
                    checkIsTouch(eventX, eventY,true)
                    isTouch = false
                    postInvalidate()
                    selectItem = null
                    return true
                }
            }
            else ->{
                if (isTouch){
                    checkIsTouch(eventX, eventY,true)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun checkIsTouch(eventX:Float,eventY:Float,isTouch:Boolean = false):Boolean{
        for (entry in pointHashMap.entries){
            val rectF = entry.value
            if (isTouch){
                rectF.left = 0F
                rectF.right = height.toFloat()
            }
            if (rectF.contains(eventX,eventY)){
                setSelectString(entry.key)
                return true
            }
        }
        return false
    }


    fun setIndexItems(data:MutableList<String>){
        mIndexItems.clear()
        mIndexItems.addAll(data)
        visibility = if (mIndexItems.size <= 1) GONE else VISIBLE
        invalidate()
    }

    fun addItem(item:String){
        if (!mIndexItems.contains(item)) {
            mIndexItems.add(item)
            mIndexItems.sort()
            invalidate()
        }
    }

    fun removeItem(item: String){
        if (mIndexItems.contains(item)) {
            mIndexItems.remove(item)
            invalidate()
        }
    }

    fun setSelectString(string: String?){
        if (string == selectItem)return
        selectItem = string
        onSelectIndexItemListener?.onSelectIndexItem(string)
        invalidate()
    }

    fun getLatter() = mIndexItems

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.repeatCount == 0 && isTouch) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}