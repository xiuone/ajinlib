package com.xy.base.widget.tag

import android.animation.AnimatorSet
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.xy.base.R
import com.xy.base.utils.Logger
import com.xy.base.utils.anim.ViewAnimHelper
import kotlin.math.max

class TagView<T> @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    FrameLayout(context, attrs, defStyleAttr){
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }
    private var itemPaddingH = 0
    private var itemPaddingV = 0
    var bindTagListener :TagCreateListener<T> ?= null

    private var animatorSet:AnimatorSet?=null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TagView)
        itemPaddingH = typedArray.getDimensionPixelSize(R.styleable.TagView_tag_padding_item_h,itemPaddingH)
        itemPaddingV = typedArray.getDimensionPixelSize(R.styleable.TagView_tag_padding_item_v,itemPaddingV)
        typedArray.recycle()
    }

    fun setNewData(data:MutableList<T>){
        removeAllViews()
        for (item in data){
            val view = bindTagListener?.onCreateTag(this,item)
            if (view != null){
                view.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
                    override fun onGlobalLayout() {
                        startMoveAnimWithDelay(200)
                        view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }

                })
                this.addView(view)
                view.tag = item
            }
        }
        startMoveAnimWithDelay()
    }

    fun removeItem(item:T?){
        for (index in 0 until childCount){
            val itemView = getChildAt(index)
            if (itemView.tag == item){
                removeView(itemView)
                startMoveAnimWithDelay()
                return
            }
        }
    }

    fun addItem(item: T?){
        if (item == null)return
        val view = bindTagListener?.onCreateTag(this,item)
        if (view != null){
            view.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            this.addView(view)
            view.tag = item
        }
        startMoveAnimWithDelay()
    }
    private fun startMoveAnimWithDelay(delay:Long = 100){
        mainHandler.removeCallbacksAndMessages(null)
        if (delay <=0){
            startMoveAnim()
        }else{
            mainHandler.postDelayed({startMoveAnim()},delay)
        }
    }


    private fun startMoveAnim(){

        synchronized(this){
            ViewAnimHelper.cancel(animatorSet)
            animatorSet = ViewAnimHelper.getAnimation()
            val builder = ViewAnimHelper.getBuilder(animatorSet)
            val userLineList = startCow(builder)
            startRow(builder,userLineList)
            animatorSet?.start()
        }
    }

    private fun startCow(builder:AnimatorSet.Builder?):MutableList<CowData>{
        val userLineList = ArrayList<CowData>()
        val allWidth = width-paddingLeft-paddingRight
        for (index in 0 until  childCount){
            val itemView = getChildAt(index)
            val currentWidth = itemView.width
            val currentHeight = itemView.height
            var isMove = false
            for (itemCow in userLineList){
                val lineUseWidth = itemCow.width
                if ((lineUseWidth + currentWidth + itemPaddingH) <= allWidth){
                    itemCow.width = itemCow.width + itemPaddingH
                    ViewAnimHelper.setMarginLeft(builder,itemCow.width,itemView)
                    itemCow.height = max(itemCow.height,currentHeight)
                    itemCow.width = itemCow.width + currentWidth
                    itemCow.views.add(itemView)
                    isMove = true
                    break
                }
            }
            if (!isMove) {
                val itemViewList = ArrayList<View>()
                itemViewList.add(itemView)
                userLineList.add(CowData(itemViewList, currentWidth, currentHeight))
                ViewAnimHelper.setMarginLeft(builder, 0, itemView)
            }
        }
        return userLineList
    }

    private fun startRow(builder:AnimatorSet.Builder?,cowList:MutableList<CowData>){
        var userHeight = 0
        for ((index,item) in cowList.withIndex()){
            val lineHeight = item.height
            for (view in item.views){
                val itemHeight = view.height
                if (itemHeight < lineHeight){
                    ViewAnimHelper.setMarginTop(builder,userHeight + (lineHeight - itemHeight)/2,view)
                }else{
                    ViewAnimHelper.setMarginTop(builder,userHeight,view)
                }
            }
            userHeight += itemPaddingV+lineHeight
        }
    }



    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw)startMoveAnimWithDelay()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mainHandler.removeCallbacksAndMessages(null)
        ViewAnimHelper.cancel(animatorSet)
    }


    data class CowData(val views:ArrayList<View>, var width:Int, var height:Int)

    interface TagCreateListener<T>{
        fun onCreateTag(view: TagView<T>,item:T):View
    }
}