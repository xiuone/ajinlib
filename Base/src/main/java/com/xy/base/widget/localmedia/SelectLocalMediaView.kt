package com.xy.base.widget.localmedia

import android.animation.AnimatorSet
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.*
import com.luck.picture.lib.entity.LocalMedia
import com.xy.base.R
import com.xy.base.listener.ContextListener
import com.xy.base.utils.anim.ViewAnimHelper
import com.xy.base.utils.exp.loadImageWithCenter
import com.xy.base.utils.exp.setOnClick
import com.xy.base.utils.picture.PictureSelectCallBack


class SelectLocalMediaView(context: Context, attributeSet: AttributeSet) : FrameLayout(context,attributeSet),
    PictureSelectCallBack,ResetPositionListener{
    private var selectLocalMediaListener: SelectLocalMediaListener?=null
    private val resetPositionController by lazy { ResetPositionController(this,null,this) }
    private val maxNUmber:Int
    private val rowNumber :Int    //横像
    private val delSize :Int
    private val delPadding :Int
    private val delRes :Int
    private val selectRes:Int
    private val mediaPadding:Int
    private val itemSize:Int
    private var animatorSet: AnimatorSet? = null

    private var rowSize = 0

    fun bindWindow(selectLocalMediaListener: SelectLocalMediaListener,contextListener: ContextListener? = null){
        resetPositionController.contextListener = contextListener
        this.selectLocalMediaListener = selectLocalMediaListener
    }

    init {
        val array = context.obtainStyledAttributes(attributeSet, R.styleable.SelectLocalMediaView)
        maxNUmber = array.getInteger(R.styleable.SelectLocalMediaView_select_media_max,0)
        rowNumber = array.getInteger(R.styleable.SelectLocalMediaView_select_media_row,3)
        delSize = array.getInteger(R.styleable.SelectLocalMediaView_select_media_del_size,0)
        delPadding = array.getInteger(R.styleable.SelectLocalMediaView_select_media_del_padding,0)
        delRes = array.getResourceId(R.styleable.SelectLocalMediaView_select_media_del_res,R.drawable.bg_transparent)
        mediaPadding = array.getResourceId(R.styleable.SelectLocalMediaView_select_media_padding_size,R.drawable.bg_transparent)
        itemSize = array.getResourceId(R.styleable.SelectLocalMediaView_select_media_item_size,R.drawable.bg_transparent)
        selectRes = array.getResourceId(R.styleable.SelectLocalMediaView_select_media_select_res,R.drawable.bg_transparent)
        addMoreView()
    }


    override fun onResult(result: ArrayList<LocalMedia>) {
        val showSize = width - paddingLeft - paddingRight
        val allItemSize = this.itemSize + this.delSize/2
        rowSize = (showSize - allItemSize*rowNumber) /(rowNumber - 1)
        removeAllViews()
        for ((index,item) in result.withIndex()){
            if (index >= maxNUmber)return
            val frameLayout = FrameLayout(context)
            val frameParams = LayoutParams(allItemSize,allItemSize)
            val positionArray = onPosition(index)
            frameParams.leftMargin = positionArray[0]
            frameParams.topMargin = positionArray[1]
            this.addView(frameLayout)
            frameLayout.tag = item


            val imageView = selectLocalMediaListener?.onCreateIconView()
            val imgParams = LayoutParams(this.itemSize,this.itemSize)
            imgParams.topMargin = this.delSize / 2
            imageView?.layoutParams = imgParams
            imageView?.tag = resetPositionController.contentTag
            frameLayout.addView(imageView)

            val delView  = ImageView(context)
            val delParams = LayoutParams(delSize,delSize)
            delParams.leftMargin = allItemSize - delSize
            delView.layoutParams = delParams
            delView.setImageResource(delRes)
            imageView?.loadImageWithCenter(item.availablePath)
            delView.tag = resetPositionController.delTag
            frameLayout.addView(delView)
        }
        ViewAnimHelper.getAnimation()
        addMoreView()
    }


    fun getData():ArrayList<LocalMedia>{
        val data = ArrayList<LocalMedia>()
        for (index in 0 until childCount){
            val childView = getChildAt(index)
            val tag = childView.tag
            if (tag is LocalMedia){
                data.add(tag)
            }
        }
        return data
    }


    private fun addMoreView(){
        if (childCount >= maxNUmber)return
        val showSize = width - paddingLeft - paddingRight
        val allItemSize = this.itemSize + this.delSize/2
        val rowSize = (showSize - allItemSize*rowNumber) /(rowNumber - 1)


        val frameLayout = FrameLayout(context)
        val frameParams = LayoutParams(allItemSize,allItemSize)
        frameParams.leftMargin = (childCount % rowNumber) * (allItemSize + rowSize)
        frameParams.topMargin = (childCount / rowNumber) * (mediaPadding + rowSize)
        this.addView(frameLayout)

        val imageView = selectLocalMediaListener?.onCreateIconView()
        val imgParams = LayoutParams(this.itemSize,this.itemSize)
        imgParams.topMargin = this.delSize / 2
        imageView?.layoutParams = imgParams
        frameLayout.addView(imageView)

        imageView?.setImageResource(selectRes)
        imageView?.setOnClick{
            this.selectLocalMediaListener?.onCreateIconView()
        }
    }

    /**
     * view大小改变的时候
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == oldw || w <= 0)return
        onResult(ArrayList(getData()))
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val status = resetPositionController.dispatchTouchEvent(ev)
        return if (ev?.action != MotionEvent.ACTION_UP || ev.action != MotionEvent.ACTION_CANCEL){
            status || super.dispatchTouchEvent(ev)
        }else{
            if (status){
                ev.action = MotionEvent.ACTION_CANCEL
            }
            super.dispatchTouchEvent(ev)
        }
    }


    /**
     * view被移除的时候
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ViewAnimHelper.cancel(animatorSet)
        resetPositionController.onDestroy()
    }

    /**
     * 获取当前位置
     */
    override fun onPosition(index: Int): IntArray {
        val allItemSize = this.itemSize + this.delSize/2
        val leftMargin = (index % rowNumber) * (allItemSize + rowSize)
        val topMargin = (index / rowNumber) * (mediaPadding + rowSize)
        return intArrayOf(leftMargin,topMargin)
    }

    /**
     * 获取每一个item的横向间距
     */
    override fun onRowSize(): Int = rowSize
}