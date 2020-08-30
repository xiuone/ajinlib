package com.jianbian.baselib.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.Nullable
import com.jianbian.baselib.R
import com.jianbian.baselib.mvp.impl.MultiChoseListener
import com.jianbian.baselib.utils.AppUtil

abstract class MultiView <T>:LinearLayout {
    var intervalUpDown = 0
    var intervalLeftRight = 0
    var ajItemPaddingLeft:Int = 0
    var ajItemPaddingRight:Int = 0
    var ajItemPaddingTop:Int = 0
    var ajItemPaddingBottom:Int = 0
    var ajItemTextSelectColor = 0X000000
    var ajItemTextSelectNotColor = 0X000000
    var ajItemTextSelectSize = 15F
    var ajItemTextSelectNotSize = 15F
    var ajItemSelectBackground: Drawable?= null
    var ajItemSelecNotBackground: Drawable?=null

    var widthSize : Int = 0

    var ajItemAverageNumber : Int = 3
    var ajItemFrameWidth: Int = 0
    var ajItemHaveFrame: Boolean = false
    var ajItemHaveAverage: Boolean = false

    private var firstSelect : Boolean = false
    private var data: MutableList<T> = ArrayList()
    var listener : MultiChoseListener<T>?= null

    private val viewSizeHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: android.os.Message) {
            super.handleMessage(msg)
            removeCallbacksAndMessages(null)
            widthSize = width -paddingLeft-paddingRight
            if (widthSize > 0) {
                resetView()
            } else {
                this.sendEmptyMessageDelayed(1, 100)
            }
        }
    }

    constructor(context: Context?) : super(context) { initView(null) }

    constructor(context: Context?, @Nullable attrs: AttributeSet?) : super(context, attrs) { initView(attrs) }

    constructor(context: Context?, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initView(attrs) }

    open fun initView(attrs: AttributeSet?) {
        orientation = VERTICAL
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.MultiView)

            ajItemAverageNumber = array.getInteger(R.styleable.MultiView_aj_item_average_number, 3)
            ajItemFrameWidth = array.getDimensionPixelSize(R.styleable.MultiView_aj_item_frame_width, 0)
            ajItemHaveFrame = array.getBoolean(R.styleable.MultiView_aj_item_have_frame, false)
            ajItemHaveAverage = array.getBoolean(R.styleable.MultiView_aj_item_have_average, false)

            intervalUpDown = array.getDimensionPixelSize(R.styleable.MultiView_aj_left_right, 0)
            intervalLeftRight = array.getDimensionPixelSize(R.styleable.MultiView_aj_up_down, 0)
            firstSelect = array.getBoolean(R.styleable.MultiView_aj_first_select, false)

            ajItemPaddingLeft = array.getDimensionPixelSize(R.styleable.MultiView_aj_item_padding_left, 0)
            ajItemPaddingRight = array.getDimensionPixelSize(R.styleable.MultiView_aj_item_padding_right, 0)
            ajItemPaddingTop = array.getDimensionPixelSize(R.styleable.MultiView_aj_item_padding_top, 0)
            ajItemPaddingBottom = array.getDimensionPixelSize(R.styleable.MultiView_aj_item_padding_bottom, 0)

            ajItemTextSelectNotColor = array.getColor(R.styleable.MultiView_aj_item_select_not_textSize, AppUtil.getColor(context, R.color.black))
            ajItemTextSelectColor = array.getColor(R.styleable.MultiView_aj_item_select_textSize, ajItemTextSelectNotColor)

            ajItemTextSelectNotSize = array.getDimension(R.styleable.MultiView_aj_item_select_not_textSize, AppUtil.dp2px(context, 15F).toFloat())
            ajItemTextSelectSize = array.getDimension(R.styleable.MultiView_aj_item_select_textSize, ajItemTextSelectNotSize)

            ajItemSelecNotBackground = array.getDrawable(R.styleable.MultiView_aj_item_select_not_background)
            ajItemSelectBackground = array.getDrawable(R.styleable.MultiView_aj_item_select_background)
        }
        if (ajItemSelectBackground == null){
            ajItemSelectBackground = AppUtil.getDrawable(context,R.drawable.bg_transparent)
        }
        if (ajItemSelecNotBackground == null){
            ajItemSelecNotBackground = AppUtil.getDrawable(context,R.drawable.bg_transparent)
        }

    }


    /**
     * 设置数据
     */
    fun setData(data: MutableList<T>?){
        if (data == null)return
        this.data.clear()
        this.data.addAll(data)
        viewSizeHandler.removeCallbacksAndMessages(null)
        viewSizeHandler.sendEmptyMessage(1)
    }

    private fun resetView(){
        this.removeAllViews()
        for (index in data.indices){
            val item:T = data[index]
            val view = actionView(item,index,(index == 0 && firstSelect ))
            var layout :LinearLayout ?= null
            if (childCount<=0){
                layout = addHorizontalItem()
            }else{
                layout = getChildAt(childCount-1) as LinearLayout
            }

            if (layout.childCount <= 0 ){
                addItem(layout,view,index,item)
                return
            }

            var itemViewWidth = 0
            for (viewIndex in 0 until layout.childCount){
                itemViewWidth += measureView(layout.getChildAt(viewIndex))
            }
            itemViewWidth = intervalLeftRight + measureView(view)
            if (itemViewWidth > widthSize){
                layout = addHorizontalItem()
                addItem(layout,view,index,item)
            }else{
                addItem(layout,view,index,item)
            }
        }
    }

    private fun measureView(view: View): Int {
        val spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        view.measure(spec, spec)
        return view.measuredWidth
    }

    /**
     * 添加存放横线布局的布局
     */
    private fun addHorizontalItem():LinearLayout{
        val layout = LinearLayout(context)
        val params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        if (childCount > 0){
            params.setMargins(0,intervalUpDown,0,0)
        }
        this.addView(layout)
        return layout
    }

    /**
     * 添加真正的子布局
     */
    private fun addItem(layout:LinearLayout,view: View,position: Int,item: T){
        val params = view.layoutParams as LayoutParams
        params.setMargins(intervalLeftRight+params.leftMargin,params.topMargin,params.rightMargin,params.bottomMargin)
        layout.addView(view)
        view.setOnClickListener(Clicked(position,item))
    }

    inner class Clicked(private val position: Int, val item: T):OnClickListener{
        override fun onClick(view: View) {
            actionClicked(view,item,position)
        }
    }

    /**
     * 获取所有的子item
     */
    fun getItemList():ArrayList<View>{
        val data = ArrayList<View>()
        for (index in 0 until childCount){
            val itemView = getChildAt(index) as LinearLayout
            for (itemIndex in 0 until itemView.childCount)
                data.add(itemView.getChildAt(itemIndex))
        }
        return data
    }

    /**
     * 设置选中位置
     */
    fun setActionPostion(position:Int){
        val data = getItemList()
        if (position < data.size && position < this.data.size){
            actionClicked(data,data[position],this.data[position],position)
        }
    }

    /**
     * 获取是否被选中
     */
    fun getViewTag(view: View): Boolean {
        val `object` = view.tag ?: return false
        return if (`object` !is Boolean) false else `object`
    }
    fun setTag(view: View,status:Boolean){
        view.tag = status
    }
    fun getData():MutableList<T>{return data}
    abstract fun actionClicked(view: View,item:T,position: Int)
    abstract fun actionClicked(views: ArrayList<View>,view: View,item:T,position: Int)
    abstract fun actionView(item:T,position:Int,selectEd:Boolean):View

}