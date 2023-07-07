package com.xy.base.widget.random

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.xy.base.utils.*
import java.util.*
import kotlin.math.max

class RandomLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0):
    FrameLayout(context, attrs, defStyleAttr),Runnable{
    private val TAG by lazy { this::class.java.name }
    private val backHandler by lazy { createBackHandler(TAG) }

    override fun addView(child: View?) {
        child?.visibility = INVISIBLE
        super.addView(child)
    }

    override fun run() {
        for (index in 0 until childCount){
            val childView = getChildAt(index)
            if (childView?.isVisible != true && childView.width > 0 && childView.height > 0) {
                Logger.d(TAG,"找到一枚 可以需要添加的数据")
                findPosition(childView)
            }
        }
        backHandler.runBackThread(this,1000)
    }


    private fun findPosition(changeView:View){
        for (randomIndex in 0 until 10){
            var intersects = false
            val createX = createX(changeView.width)
            val createY = createY(changeView.height)

            for (index in 0 until  childCount){
                val childView = getChildAt(index)
                if (childView?.isVisible == true){
                    val childX = childView.x.toInt()
                    val childY = childView.y.toInt()
                    val childRect = Rect(childX, childY, childView.width + childX, childView.height + childY)
                    val changeRect = Rect(createX, createY, changeView.measuredWidth + createX, changeView.measuredHeight + createY)
                    if (Rect.intersects(childRect,changeRect)){
                        intersects = true
                        break
                    }
                }
            }
            if (!intersects){
                changeViewPosition(changeView,createX,createY)
                return
            }else{
                changeView.visibility = View.INVISIBLE
            }
        }
        Logger.d(TAG,"寻找10次还没找到要添加的位置，等一下了再添加")
    }


    private fun changeViewPosition(childView:View,createX:Int,createY:Int){
        runMain({
            Logger.d(TAG,"修改位置")
            childView.visibility = View.VISIBLE
            childView.x = createX.toFloat()
            childView.y = createY.toFloat()
        })
    }

    /**
     * 根据传入的宽和高返回一个随机的坐标!
     */
    private fun createX(childWidth: Int): Int = max( Random().nextInt(width - childWidth - paddingLeft - paddingRight),0)
    private fun createY(childHeight: Int): Int = max(Random().nextInt(height - childHeight - paddingTop - paddingBottom),0)


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        backHandler.runProgress(this,0)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        backHandler.removeCallbacksAndMessages(null)
    }
}