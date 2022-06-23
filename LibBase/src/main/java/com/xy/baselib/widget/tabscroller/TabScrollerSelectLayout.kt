package com.xy.baselib.widget.tabscroller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewpager.widget.ViewPager
import com.xy.baselib.R
import com.xy.baselib.adapter.AppFragementPagerAdapter
import com.xy.baselib.exp.getResColor
import com.xy.baselib.exp.getResDimension
import kotlin.math.min

class TabScrollerSelectLayout<T: TabScrollerEntry> @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    HorizontalScrollView(context, attrs, defStyleAttr) , ViewPager.OnPageChangeListener,Runnable ,LifecycleObserver{
    private val containerLayout by lazy { LinearLayout(context) }
    private var selectListener:TabSelectListener<T>?=null

    private var isCenter = false
    private var lineColor = context.getResColor(R.color.transparent)
    private var lineRadius = context.getResDimension(R.dimen.dp_0).toFloat()
    private var lineWidth = context.getResDimension(R.dimen.dp_0).toFloat()
    private var lineHeight = context.getResDimension(R.dimen.dp_0).toFloat()

    private var itemPaddingLeft = context.getResDimension(R.dimen.dp_0)
    private var itemPaddinRight = context.getResDimension(R.dimen.dp_0)
    private var itemPaddinTop = context.getResDimension(R.dimen.dp_0)
    private var itemPaddinBottom = context.getResDimension(R.dimen.dp_0)
    private var itemMarginLeft = context.getResDimension(R.dimen.dp_0)
    private var itemMarginRight = context.getResDimension(R.dimen.dp_0)
    private var itemSpace = context.getResDimension(R.dimen.dp_0)

    private var itemTextSelectSize = context.getResDimension(R.dimen.dp_0).toFloat()
    private var itemTextSelectColor = context.getResColor(R.color.black)
    private var isSelectBold = false

    private var itemTextCommonSize = context.getResDimension(R.dimen.dp_0).toFloat()
    private var itemTextCommonColor = context.getResColor(R.color.black)
    private var isCommonBold = false
    private var viewPager:ViewPager?=null
    private val scrollerHandler by lazy { Handler(Looper.getMainLooper()) }
    private var itemLeft = 0F
    var selectPosition = 0
        set(value) {
            field = value
            resetView()
            invalidate()
            scrollerHandler.removeCallbacksAndMessages(null)
            scrollerHandler.post(this)
        }
    init {
        setBackgroundColor(context.getResColor(R.color.transparent))
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabScrollerSelectLayout)
            lineColor = typedArray.getColor(R.styleable.TabScrollerSelectLayout_tab_scroller_line_color,lineColor)
            lineRadius = typedArray.getDimension(R.styleable.TabScrollerSelectLayout_tab_scroller_line_radius,lineRadius)
            lineWidth = typedArray.getDimension(R.styleable.TabScrollerSelectLayout_tab_scroller_line_width,lineWidth)
            lineHeight = typedArray.getDimension(R.styleable.TabScrollerSelectLayout_tab_scroller_line_height,lineHeight)
            isCenter = typedArray.getBoolean(R.styleable.TabScrollerSelectLayout_tab_scroller_line_center,false)
            itemPaddingLeft = typedArray.getDimensionPixelOffset(R.styleable.TabScrollerSelectLayout_tab_scroller_item_padding_left,itemPaddingLeft)
            itemPaddinRight = typedArray.getDimensionPixelOffset(R.styleable.TabScrollerSelectLayout_tab_scroller_item_padding_right,itemPaddinRight)
            itemPaddinTop = typedArray.getDimensionPixelOffset(R.styleable.TabScrollerSelectLayout_tab_scroller_item_padding_top,itemPaddinTop)
            itemPaddinBottom = typedArray.getDimensionPixelOffset(R.styleable.TabScrollerSelectLayout_tab_scroller_item_padding_bottom,itemPaddinBottom)
            itemMarginLeft = typedArray.getDimensionPixelOffset(R.styleable.TabScrollerSelectLayout_tab_scroller_item_margin_left,itemMarginLeft)
            itemMarginRight = typedArray.getDimensionPixelOffset(R.styleable.TabScrollerSelectLayout_tab_scroller_item_margin_right,itemMarginRight)
            itemSpace = typedArray.getDimensionPixelOffset(R.styleable.TabScrollerSelectLayout_tab_scroller_item_space,itemSpace)

            itemTextSelectSize = typedArray.getDimension(R.styleable.TabScrollerSelectLayout_tab_scroller_item_select_size,itemTextSelectSize)
            itemTextSelectColor = typedArray.getColor(R.styleable.TabScrollerSelectLayout_tab_scroller_item_select_color,itemTextSelectColor)
            isSelectBold = typedArray.getBoolean(R.styleable.TabScrollerSelectLayout_tab_scroller_item_select_is_bold,isSelectBold)
            itemTextCommonSize = typedArray.getDimension(R.styleable.TabScrollerSelectLayout_tab_scroller_item_common_size,itemTextCommonSize)
            itemTextCommonColor = typedArray.getColor(R.styleable.TabScrollerSelectLayout_tab_scroller_item_common_color,itemTextCommonColor)
            isCommonBold = typedArray.getBoolean(R.styleable.TabScrollerSelectLayout_tab_scroller_item_common_is_bold,isCommonBold)
        }
        scrollerHandler.post(this)
        containerLayout.gravity = Gravity.CENTER
        this.addView(containerLayout)

    }

    fun setNewData(data:MutableList<T>){
        containerLayout.removeAllViews()
        for (item in data){
            addData(item)
        }
        selectPosition = 0
    }

    fun setNewData(data: MutableList<T>, viewPager: ViewPager, fm: FragmentManager, framentList: List<Fragment>){
        this.viewPager = viewPager;
        viewPager.adapter = AppFragementPagerAdapter(fm,framentList)
        setNewData(data)
        viewPager.addOnPageChangeListener(this)
    }

    fun addData(data:T){
        val view = LayoutInflater.from(context).inflate(R.layout.layout_tab,null)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        val marginLeft = itemMarginLeft +( if (containerLayout.childCount>0) itemSpace else 0 )
        params.setMargins(marginLeft,0,itemMarginRight,0)
        view.layoutParams = params
        view.setPadding(itemPaddingLeft,itemPaddinTop,itemPaddinRight,itemPaddinBottom)

        val textView = view.findViewById<View>(R.id.title_tv)
        if (textView is TextView){
            textView.setTextColor(itemTextCommonColor)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,itemTextCommonSize)
            if (isCommonBold)
                textView.setTypeface(null,Typeface.BOLD)
            else
                textView.setTypeface(null,Typeface.NORMAL)
        }
        setData(view,data)
        containerLayout.addView(view)
    }

    fun setPositionData(position:Int,data:T){
        if (position < childCount) {
            val view = containerLayout.getChildAt(position)
            setData(view,data)
        }
    }

    private fun setData(view: View,data:T){
        val textView = view.findViewById<View>(R.id.title_tv)
        val messageView = view.findViewById<View>(R.id.message_tv)
        if (textView is TextView){
            textView.text = data.onLabel()
        }
        if (messageView is TextView){
            if (data.unReadNumber()<=0){
                messageView.visibility = View.GONE
                messageView.text = "0"
            }else{
                messageView.visibility = View.VISIBLE
                messageView.text = "${data.unReadNumber()}"
                if (data.unReadNumber() >99){
                    messageView.text = "99+"
                }
            }
        }

        view.setOnClickListener {
            for (index in 0 until containerLayout.childCount){
                val childView = containerLayout.getChildAt(index)
                if (childView == view){
                    selectListener?.onTabSelectCallBack(data,index)
                    selectPosition = index
                    viewPager?.currentItem = selectPosition
                }
            }
        }
    }

    private fun resetView(){
        for (index in 0 until containerLayout.childCount){
            val childView = containerLayout.getChildAt(index)
            val textView = childView.findViewById<View>(R.id.title_tv)
            if (textView is TextView) {
                if (index == selectPosition) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,itemTextSelectSize)
                    textView.setTextColor(itemTextSelectColor)
                    if (isSelectBold)
                        textView.setTypeface(null,Typeface.BOLD)
                    else{
                        textView.setTypeface(null,Typeface.NORMAL)
                    }
                }else{
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,itemTextCommonSize)
                    textView.setTextColor(itemTextCommonColor)
                    if (isCommonBold)
                        textView.setTypeface(null,Typeface.BOLD)
                    else{
                        textView.setTypeface(null,Typeface.NORMAL)
                    }
                }
            }
        }
    }


    open fun getItemLeft():Float{
        var useWidth = 0F
        for (index in 0 until containerLayout.childCount){
            val childView = containerLayout.getChildAt(index)
            val params = childView.layoutParams
            if (params is MarginLayoutParams){
                useWidth += params.leftMargin
            }
            if (index  == selectPosition){
                return useWidth + if (isCenter) ((childView.width-lineWidth)/2) else 0F
            }else if (params is MarginLayoutParams){
                useWidth += params.rightMargin+childView.width
            }
        }
        return useWidth
    }

    override fun onDraw(canvas: Canvas?) {
        var useWidth = getItemLeft()
        val rectF = RectF(useWidth,height-lineHeight,useWidth+lineWidth,height.toFloat())
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = lineColor
        val radius = min(min(lineRadius,lineWidth/2F),lineHeight/2F)
        canvas?.drawRoundRect(rectF,radius,radius,paint)
        super.onDraw(canvas)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        selectPosition = position
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun run() {
        val itemLeft = getItemLeft()
        if (itemLeft != this.itemLeft) {
            this.itemLeft = itemLeft
            scrollTo(itemLeft.toInt(), 0)
            scrollerHandler.postDelayed(this, 100)
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroyed(owner: LifecycleOwner) {
        scrollerHandler.removeCallbacksAndMessages(null)
    }
}