package com.xy.baselib.widget.tab.draw

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.xy.baselib.R
import com.xy.baselib.widget.tab.type.SelectType
import com.xy.baselib.exp.getResColor
import kotlin.math.abs
import kotlin.math.min

class TabDraw (private val view:View, private val attrs: AttributeSet?){
    private val context by lazy { view.context }
    private var blockHeight = 10F
    private var blockWidth = 10F
    var valueAnimCenterX = 0F
    private var valueAnimCenterY = 0F
    private var valueAnimWidth = 0F
    private var valueAnimHeight = 0F
    private var selectPaintColor = 0x000000
    private var selectType = SelectType.BLOCK

    fun init() {
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabLayout)
            selectPaintColor = typedArray.getColor(R.styleable.TabLayout_tab_select_block_color,context.getResColor(R.color.blue_078a))
            blockHeight = typedArray.getDimension(R.styleable.TabLayout_tab_select_block_height,-1F)
            blockWidth = typedArray.getDimension(R.styleable.TabLayout_tab_select_block_width,-1F)
            val blockType = typedArray.getInteger(R.styleable.TabLayout_tab_block_type,1)
            selectType = when(blockType){
                1->SelectType.BLOCK
                2->SelectType.ANGLE
                else->SelectType.FOLLOW
            }
        }
    }

    fun onDraw(canvas:Canvas?){
        canvas?:return
        when(selectType){
            SelectType.ANGLE->{}
            SelectType.BLOCK->{
                val blockWidth = if (this.blockWidth < 0) valueAnimWidth else blockWidth;
                if (blockHeight <=0){
                    onDrawBlock(canvas,valueAnimCenterY,blockWidth,valueAnimHeight)
                }else{
                    onDrawBlock(canvas,view.height - blockHeight/2 - view.paddingBottom,blockWidth,blockHeight)
                }
            }
            SelectType.FOLLOW->onDrawBlock(canvas,valueAnimCenterY,valueAnimWidth,valueAnimHeight)
        }
    }

    private fun onDrawBlock(canvas:Canvas,blockCenterY:Float,blockWidth:Float,blockHeight:Float){
        val left = valueAnimCenterX - blockWidth/2F
        val right = valueAnimCenterX + blockWidth/2F
        val top = blockCenterY - blockHeight/2F
        val bottom = blockCenterY + blockHeight/2F
        val rect = RectF(left,top,right,bottom)
        val radius = min(abs(bottom-top),abs(right-left))/2
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = selectPaintColor
        canvas.drawRoundRect(rect,radius,radius,paint)
    }

    open fun onTabAnimationUpdate(valueAnimCenterX: Float, valueAnimCenterY: Float, valueAnimWidth: Float, valueAnimHeight: Float) {
         this.valueAnimCenterX = valueAnimCenterX
         this.valueAnimCenterY = valueAnimCenterY
         this.valueAnimWidth = valueAnimWidth
         this.valueAnimHeight = valueAnimHeight
         view.invalidate()
    }

    fun isInit():Boolean{
        return valueAnimCenterX == 0F && valueAnimCenterY == 0F && valueAnimWidth == 0F && valueAnimHeight == 0F
    }
 }