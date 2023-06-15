package com.xy.base.widget.navi.tool

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.xy.base.utils.exp.getResColor
import com.xy.base.utils.exp.getResDimension
import com.xy.base.utils.exp.getUnReadNumber
import com.xy.base.utils.exp.setOnClick
import com.xy.base.utils.exp.setUnReadVisibility
import com.xy.base.R

open class ToolView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, )
    : LinearLayout(context, attrs, defStyleAttr) {
    var listener: ToolClickedListener? = null
    private val data by lazy { ArrayList<ToolMode>() }
    var hSize = 0
    var vSize = 0
    var commonMargin = 0
    var itemSize = 0
    var iconSize = 0
    var textSize = 0
    var unReadTextSize = 0
    var unReadTextMineWidth = 0
    var unReadTextMineHeight = 0
    var textColor = 0
    var showText = true
    private val unRead = "unRead"
    private val title = "title"
    private val icon = "icon"
    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ToolMineView)
            hSize =
                typedArray.getDimensionPixelSize(R.styleable.ToolMineView_tool_mine_space_h_size, hSize)
            vSize =
                typedArray.getDimensionPixelSize(R.styleable.ToolMineView_tool_mine_space_v_size, vSize)
            commonMargin =
                typedArray.getDimensionPixelSize(R.styleable.ToolMineView_tool_mine_space_content_margin, commonMargin)
            itemSize =
                typedArray.getDimensionPixelSize(R.styleable.ToolMineView_tool_mine_item_size, itemSize)
            iconSize =
                typedArray.getDimensionPixelSize(R.styleable.ToolMineView_tool_mine_icon_size, iconSize)
            textSize =
                typedArray.getDimensionPixelSize(R.styleable.ToolMineView_tool_mine_text_size, textSize)
            unReadTextSize =
                typedArray.getDimensionPixelSize(R.styleable.ToolMineView_tool_mine_unread_text_size, unReadTextSize)
            unReadTextMineWidth =
                typedArray.getDimensionPixelSize(R.styleable.ToolMineView_tool_mine_unread_mine_width, unReadTextMineWidth)
            unReadTextMineHeight =
                typedArray.getDimensionPixelSize(R.styleable.ToolMineView_tool_mine_unread_mine_height, unReadTextMineHeight)
            textColor =
                typedArray.getColor(R.styleable.ToolMineView_tool_mine_text_color, context.getResColor(R.color.gray_3333))
            showText =
                typedArray.getBoolean(R.styleable.ToolMineView_tool_mine_show_text,true)
            typedArray.recycle()
        }
    }

    fun setData(newData:MutableList<ToolMode>){
        synchronized(data){
            this.removeAllViews()
            this.data.clear()
            this.data.addAll(newData)
            addView()
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == 0 || w == oldw)return
        post {
            addView()
        }
    }


    private fun getFatherView(cow:Int,total:Int):LinearLayout{
        if (total<=cow){
            this.orientation = HORIZONTAL
            return this
        }
        this.orientation = VERTICAL
        if (childCount > 0 ){
            val linear = getChildAt(childCount-1)
            if (linear is LinearLayout && ((linear.childCount+1)/2) < cow){
                return  linear
            }
        }
        val linear = LinearLayout(context)
        val params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        if (childCount > 0 )
        params.topMargin = vSize
        linear.layoutParams = params
        this.addView(linear)
        return linear
    }

    private fun getSpaceH(width: Int,cow:Int):Int = (width - cow * itemSize)/(cow - 1)

    private fun addView(){
        val width = width - paddingLeft - paddingRight
        val total = this.data.size
        if (width <=0 || total <=0)return
        val cow = (width + hSize)/(hSize + itemSize)
        val spaceH = getSpaceH(width, cow)
        synchronized(data){
            for (item in data){
                val linear = getFatherView(cow, total)
                if (linear.childCount > 0){
                    addSpace(linear,spaceH)
                }
                addItemView(linear,item)
            }
        }
    }

    private fun addSpace(linear:LinearLayout,spaceH: Int){
        val space = Space(context)
        space.layoutParams = LayoutParams(spaceH,ViewGroup.LayoutParams.MATCH_PARENT)
        linear.addView(space)
    }

    private fun addItemView(linear:LinearLayout,item: ToolMode){
        val itemView = RelativeLayout(context)
        val params = LayoutParams(itemSize,ViewGroup.LayoutParams.WRAP_CONTENT)
        itemView.layoutParams = params
        linear.addView(itemView)
        addIcon(itemView,item)
        addTitle(itemView, item)
        addUnRead(itemView, item)
        itemView.setOnClick{
            listener?.onToolClicked(item)
        }
    }

    private fun addIcon(itemView:RelativeLayout,item: ToolMode){
        val iconImageView = ImageView(context)
        iconImageView.id = R.id.icon
        val iconParams = RelativeLayout.LayoutParams(iconSize,iconSize)
        iconParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        if (!showText){
            iconParams.addRule(RelativeLayout.CENTER_VERTICAL)
        }
        iconParams.topMargin = unReadTextMineHeight/4
        iconImageView.layoutParams = iconParams
        iconImageView.setImageResource(item.iconRes)
        itemView.addView(iconImageView)
        iconImageView.tag = item.type+icon
    }

    private fun addTitle(itemView:RelativeLayout,item: ToolMode){
        if (showText) {
            val titleView = TextView(context)
            val titleParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
            titleParams.addRule(RelativeLayout.BELOW, R.id.icon)
            titleParams.topMargin = commonMargin
            titleView.layoutParams = titleParams
            titleView.isSingleLine = true
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            titleView.setTextColor(textColor)
            titleView.text = item.str
            titleView.gravity = Gravity.CENTER
            itemView.addView(titleView)
            titleView.tag = item.type + title
        }
    }

    private fun addUnRead(itemView:RelativeLayout,item: ToolMode){
        val unReadView = TextView(context)
        unReadView.setBackgroundResource(item.unReadNumberBackGround)
        val titleParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        titleParams.addRule(RelativeLayout.RIGHT_OF,R.id.icon)
        titleParams.leftMargin = -unReadTextMineWidth/4
        unReadView.layoutParams = titleParams
        unReadView.isSingleLine = true
        unReadView.setTextSize(TypedValue.COMPLEX_UNIT_PX,unReadTextSize.toFloat())
        unReadView.setTextColor(context.getResColor(R.color.white))
        unReadView.text = item.unReadNumber.getUnReadNumber()
        unReadView.gravity = Gravity.CENTER
        unReadView.minWidth = unReadTextMineWidth
        unReadView.minHeight = unReadTextMineHeight
        val padding = context.getResDimension(R.dimen.dp_2)
        unReadView.setPadding(padding,0,padding,0)
        itemView.addView(unReadView)
        item.unReadNumber.setUnReadVisibility(unReadView)
        unReadView.tag = item.type+unRead
    }

    fun setUnReadNumber(type:String,number:Int){
        val unReadView = findViewWithTag<View>(type+unRead)
        if (unReadView != null && unReadView is TextView){
            number.setUnReadVisibility(unReadView)
            unReadView.text = "${number.getUnReadNumber()}"
        }
    }

    fun setTitle(type:String,titleStr:String){
        val titleView = findViewWithTag<View>(type+title)
        if (titleView != null && titleView is TextView){
            titleView.text  = titleStr
        }
    }

    fun setIcon(type:String,icon:Int){
        val titleView = findViewWithTag<View>(type+icon)
        if (titleView != null && titleView is ImageView){
            titleView.setImageResource(icon)
        }
    }

}