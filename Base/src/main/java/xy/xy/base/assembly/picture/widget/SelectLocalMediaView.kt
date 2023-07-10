package xy.xy.base.assembly.picture.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.*
import xy.xy.base.R
import picture.luck.picture.lib.entity.LocalMedia
import xy.xy.base.utils.anim.AppAnimatorListener
import xy.xy.base.utils.anim.ViewAnimHelper
import xy.xy.base.utils.exp.loadImageWithCenter
import xy.xy.base.utils.exp.setOnClick
import com.xy.picture.select.PictureSelectCallBack
import com.xy.picture.widget.ResetPositionController
import com.xy.picture.widget.ResetPositionListener
import com.xy.picture.widget.SelectLocalMediaListener


class SelectLocalMediaView @JvmOverloads  constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context,attrs),
    PictureSelectCallBack, ResetPositionListener {
    private var selectLocalMediaListener: SelectLocalMediaListener?=null
    private val resetPositionController by lazy { ResetPositionController(this,this) }
    private val maxNUmber:Int
    private val rowNumber :Int    //横像
    private val delSize :Int
    private val delPadding :Int
    private val delRes :Int
    private val selectRes:Int
    private val mediaPadding:Int
    private val itemSize:Int
    private val animatorSetList by lazy { ArrayList<AnimatorSet>() }

    private var rowSize = 0


    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.SelectLocalMediaView)
        maxNUmber = array.getInteger(R.styleable.SelectLocalMediaView_select_media_max,0)
        rowNumber = array.getInteger(R.styleable.SelectLocalMediaView_select_media_row,3)
        delSize = array.getDimensionPixelSize(R.styleable.SelectLocalMediaView_select_media_del_size,0)
        delPadding = array.getDimensionPixelSize(R.styleable.SelectLocalMediaView_select_media_del_padding,0)
        delRes = array.getResourceId(R.styleable.SelectLocalMediaView_select_media_del_res,R.drawable.bg_transparent)
        mediaPadding = array.getDimensionPixelSize(R.styleable.SelectLocalMediaView_select_media_padding_size,0)
        itemSize = array.getDimensionPixelSize(R.styleable.SelectLocalMediaView_select_media_item_size,0)
        selectRes = array.getResourceId(R.styleable.SelectLocalMediaView_select_media_select_res,R.drawable.bg_transparent)
        array.recycle()
    }

    fun bindWindow(selectLocalMediaListener: SelectLocalMediaListener){
        this.selectLocalMediaListener = selectLocalMediaListener
        addMoreView()
    }

    fun getMaxNumber() = maxNUmber

    override fun onResult(result: ArrayList<LocalMedia>) {
        val showSize = width - paddingLeft - paddingRight
        val allItemSize = this.itemSize + this.delSize/2
        rowSize = (showSize - allItemSize*rowNumber) /(rowNumber - 1)
        removeAllViews()
        for ((index,item) in result.withIndex()){
            if (index >= maxNUmber)return
            val frameLayout = FrameLayout(context)
            val frameParams = LayoutParams(0,0)
            val positionArray = onPosition(index)
            frameParams.leftMargin = positionArray[0]
            frameParams.topMargin = positionArray[1]
            frameLayout.layoutParams = frameParams
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
            delView.setOnClick{
                this.removeView(frameLayout)
                addMoreView()
                resetPositionController.startMoveAnim()
            }
            imageView?.setOnClick{
                selectLocalMediaListener?.onMediaClicked(getData(),index)
            }
            showAddAnimator(frameLayout,allItemSize)
        }
        resetPositionController.startMoveAnim()
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
        if (childCount >0){
            val lastView = getChildAt(childCount-1)
            if (lastView.tag == null){
                return
            }
        }
        if (childCount >= maxNUmber)return
        val showSize = width - paddingLeft - paddingRight
        val allItemSize = this.itemSize + this.delSize/2
        val rowSize = (showSize - allItemSize*rowNumber) /(rowNumber - 1)

        val imageView = selectLocalMediaListener?.onCreateMoreIconView()?:return


        val frameLayout = FrameLayout(context)
        val frameParams = LayoutParams(0,0)
        frameParams.leftMargin = (childCount % rowNumber) * (allItemSize + rowSize)
        frameParams.topMargin = (childCount / rowNumber) * (allItemSize + mediaPadding)
        frameLayout.layoutParams = frameParams
        this.addView(frameLayout)

        val imgParams = LayoutParams(this.itemSize,this.itemSize)
        imgParams.topMargin = this.delSize / 2
        imageView.layoutParams = imgParams
        frameLayout.addView(imageView)

        imageView.setImageResource(selectRes)
        imageView.setOnClick{
            this.selectLocalMediaListener?.onStartSelectMedia()
        }
        showAddAnimator(frameLayout,allItemSize)
    }

    private fun showAddAnimator(view:View,allItemSize:Int){
        val animatorSet = ViewAnimHelper.getAnimation()
        val builder = ViewAnimHelper.getBuilder(animatorSet)
        view.alpha = 0F
        ViewAnimHelper.setWidth(builder,allItemSize,view)
        ViewAnimHelper.setHeight(builder,allItemSize,view)
        ViewAnimHelper.setAlpha(builder,1F,view)
        animatorSet.start()
        animatorSetList.add(animatorSet)
        animatorSet.addListener(object :AppAnimatorListener(){
            override fun onAnimationEnd(p0: Animator) {
                super.onAnimationEnd(p0)
                synchronized(animatorSetList){
                    animatorSetList.remove(animatorSet)
                }
            }
        })
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
        synchronized(animatorSetList){
            for (item in animatorSetList){
                ViewAnimHelper.cancel(item)
            }
        }
        resetPositionController.onDestroy()
    }

    /**
     * 获取当前位置
     */
    override fun onPosition(index: Int): IntArray {
        val allItemSize = this.itemSize + this.delSize/2
        val leftMargin = (index % rowNumber) * (allItemSize + rowSize)
        val topMargin = (index / rowNumber) * (allItemSize + mediaPadding)
        return intArrayOf(leftMargin,topMargin)
    }


    override fun onPositionRecF(index: Int): RectF {
        val allItemSize = this.itemSize + this.delSize/2
        val leftMargin = (index % rowNumber) * (allItemSize + rowSize).toFloat()
        val topMargin = (index / rowNumber) * (allItemSize + mediaPadding).toFloat()
        return RectF(leftMargin,topMargin,leftMargin+allItemSize,topMargin+allItemSize)
    }

    /**
     * 获取每一个item的横向间距
     */
    override fun onRowSize(): Int = rowSize
}