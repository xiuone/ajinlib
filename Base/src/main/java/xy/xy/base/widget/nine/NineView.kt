package xy.xy.base.widget.nine

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import xy.xy.base.utils.exp.getResDimension
import xy.xy.base.utils.exp.loadImageWithCenter
import xy.xy.base.utils.exp.setOnClick
import xy.xy.base.R
import xy.xy.base.widget.image.circle.RoundImageView
import kotlin.math.min

class NineView<T: NineListener> @JvmOverloads  constructor(context: Context, protected val attrs: AttributeSet? = null, defStyleAttr: Int = 0, )
    :FrameLayout(context, attrs, defStyleAttr){
    private val data by lazy { ArrayList<T>() }
    protected val builder by lazy { NineBuild(this,attrs) }
    var listener : NineClickedListener<T>?= null

    init {
        builder.init()
    }


    fun setNewData(data:MutableList<T>){
        this.removeAllViews()
        if (data.isNullOrEmpty()) {
            this.visibility = View.GONE
        }else {
            this.visibility = View.VISIBLE
        }
        synchronized(this.data){
            this.data.clear()
            this.data.addAll(data)
            for ((index,item) in this.data.withIndex()){
                addView(data,item,index)
            }
        }
        resetSize(width)
    }

    open fun addView(data:MutableList<T>,item :T,position:Int){
        val frameLayout = FrameLayout(context)

        this.addView(frameLayout)
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        val imageView = RoundImageView(context,attrs)
        imageView.builder.setAllRadius(builder.radius)
        imageView.layoutParams = layoutParams
        imageView.loadImageWithCenter(item.onThumb(),builder.place)
        frameLayout.addView(imageView)
        if (listener != null){
            frameLayout.setOnClick{
                listener?.onNineClicked(data,item,position)
            }
        }
        addVideoPlayView(frameLayout,item)
        frameLayout.tag = item
        frameLayout.setBackgroundResource(builder.pleaseRes)
        frameLayout.layoutParams = LayoutParams(context.getResDimension(R.dimen.dp_100),context.getResDimension(R.dimen.dp_100))
    }

    /**
     * s-视频的时候需要显示播放按钮
     */
    private fun addVideoPlayView(frameLayout: FrameLayout,item: T){
        if (item.isVideo()){
            val imageView = ImageView(context)
            imageView.setImageResource(builder.mediaPlayRes)
            val layoutParams = LayoutParams(builder.mediaPlaySize,builder.mediaPlaySize)
            layoutParams.gravity = Gravity.CENTER
            imageView.layoutParams = layoutParams
            frameLayout.addView(imageView)
        }
    }

    /**
     * view的大小变化
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == oldw || w <= 0)return
        post {
            resetSize(w)
        }
    }

    private fun resetSize(width:Int){
        val currentTag = tag
        var widthTag = if (currentTag  is NineOldSize) currentTag.width else 0
        var heightTag = if (currentTag  is NineOldSize) currentTag.height else 0

        if (widthTag  > 0 && heightTag > 0 ){
            Log.e("==``","设置 ：${this.layoutParams?.toString()}")
            this.layoutParams?.width = widthTag
            this.layoutParams?.height = heightTag
        }
        var newWidth = if (width <=0 ) widthTag else width
        if (newWidth <= 0) return
        val newHeight = when(childCount){
            1->resetOneSize()
            2,4->resetMoreSize(newWidth,2)
            else->resetMoreSize(newWidth,3)
        }
        tag = NineOldSize(newWidth,newHeight)
        Log.e("==``","${this.layoutParams?.toString()}")
        this.layoutParams?.width = newWidth
        this.layoutParams?.height = newHeight
    }

    /**
     * 变成两个
     */
    private fun resetMoreSize(width: Int,cowSize:Int):Int{
        val newWidth = width - paddingLeft - paddingRight
        val itemSize = (newWidth - builder.space)/cowSize
        val totalSize = childCount
        for (index in 0 until totalSize){
            val childView  = getChildAt(index)
            val params = LayoutParams(itemSize,itemSize)
            params.topMargin = (index/cowSize)*(itemSize+builder.space)
            params.leftMargin = (index%cowSize)*(itemSize+builder.space)
            childView.layoutParams = params
            setChildLayoutParams(childView, params)
        }
        val lineSize = totalSize/cowSize+(if (totalSize%cowSize == 0) 0 else 1)
        return itemSize*lineSize + (builder.space)*(lineSize -1)
    }

    /**
     * 改变成一个
     */
    private fun resetOneSize():Int{
        if (childCount <= 0)return 0
        val childView = getChildAt(0)
        val tag = childView.tag
        if (tag is NineListener){
            val maxSize = builder.maxSize
            val picWidth = tag.onWidth()
            val picHeight = tag.onHeight()
            if (picWidth <= maxSize && picHeight <= maxSize){
                val params = LayoutParams(picWidth,picHeight)
                setChildLayoutParams(childView, params)
                return picHeight
            }else if (picHeight > maxSize){
                var viewWidth = picWidth*maxSize/picHeight
                if (viewWidth < builder.minSize) {
                    viewWidth = builder.minSize
                }
                viewWidth = min(viewWidth,width-paddingLeft-paddingRight)
                val params = LayoutParams(viewWidth,maxSize)
                setChildLayoutParams(childView, params)
                return maxSize
            }else{
                var viewHeight = picHeight*maxSize/picWidth
                if (viewHeight < builder.minSize)
                    viewHeight = builder.minSize
                val params = LayoutParams(maxSize,viewHeight)
                setChildLayoutParams(childView, params)
                return viewHeight
            }
        }
        return  0
    }

    private fun setChildLayoutParams(childView:View,params:LayoutParams){
        childView.layoutParams = params
        if (childView is ViewGroup && childView.childCount > 0 ){
            val newChildView = childView.getChildAt(0)
            newChildView.layoutParams = LayoutParams(params.width,params.height)
            newChildView.requestLayout()
        }
    }
}