package com.xy.base.widget.tag

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.xy.base.R
import com.xy.base.utils.anim.AppAnimatorListener
import com.xy.base.utils.anim.ViewAnimHelper
import kotlin.math.max

class TagView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    LinearLayout(context, attrs, defStyleAttr),Runnable{
    private var itemPaddingH = 0
    private var itemPaddingV = 0
    private var adapter:TagAdapter ? = null
    private val checkPositionHandler by lazy { Handler(Looper.getMainLooper()) }
    private val headLayout by lazy { LinearLayout(context) }
    private val footLayout by lazy { LinearLayout(context) }
    private val contentLayout by lazy { FrameLayout(context) }
    private val holderHashMap by lazy { HashMap<Int,TagViewHolder>() }
    private val animationList by lazy { ArrayList<AnimatorSet>() }
    private val notifyPositionList by lazy { ArrayList<Int>() }

    init {
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TagView)
            itemPaddingH = typedArray.getDimensionPixelSize(R.styleable.TagView_tag_padding_item_h,itemPaddingH)
            itemPaddingV = typedArray.getDimensionPixelSize(R.styleable.TagView_tag_padding_item_v,itemPaddingV)
            typedArray.recycle()
        }
        this.orientation = VERTICAL
        headLayout.orientation = VERTICAL
        footLayout.orientation = VERTICAL
        this.addView(headLayout)
        this.addView(contentLayout)
        this.addView(footLayout)
    }

    fun setAdapter(adapter: TagAdapter){
        this.adapter = adapter
        adapter.tagView = this
    }

    override fun run() {
        val contentWith = width - paddingLeft - paddingRight
        if (contentWith <= 0)return
        val animatorHashMap = HashMap<Int,CowData>()


        for (index in 0 until  contentLayout.childCount){
            val itemView = contentLayout.getChildAt(index)
            val itemWidth = itemView.width
            val itemHeight = itemView.height
            var isNewCow = true
            for (entries in animatorHashMap.entries){
                val cowData = entries.value
                val cowAllWidth = cowData.width + itemWidth + itemPaddingH
                if (cowAllWidth <= contentWith){
                    cowData.views.add(itemView)
                    cowData.width = cowAllWidth
                    cowData.height = max(itemHeight,cowData.height)
                    isNewCow = false
                    break
                }
            }
            if (isNewCow){
                val viewList = ArrayList<View>()
                animatorHashMap[animatorHashMap.size] = CowData(viewList,itemWidth,itemHeight)
            }
        }

        val animatorSet = ViewAnimHelper.getAnimation()
        val builder = ViewAnimHelper.getBuilder(animatorSet)

        for (entries in animatorHashMap.entries){
            val cowData = entries.value
            var useWidth = 0
            val currentCow = entries.key

            var userHeight = itemPaddingV*currentCow

            for (index in 0 until currentCow){
                userHeight += (animatorHashMap[index]?.height ?: 0)
            }

            for ((index,itemView) in cowData.views.withIndex()){
                val itemWidth = itemView.width
                val itemHeight = itemView.height
                ViewAnimHelper.setMarginLeft(builder,useWidth+itemPaddingH,itemView)
                ViewAnimHelper.setMarginTop(builder,userHeight+ (cowData.height - itemHeight)/2,itemView)
                useWidth += itemWidth + itemPaddingH
            }
        }
        animatorSet.addListener(object :AppAnimatorListener(){
            override fun onAnimationEnd(p0: Animator) {
                super.onAnimationEnd(p0)
                animationList.remove(animatorSet)
                checkPositionHandler.removeCallbacksAndMessages(null)
                checkPositionHandler.postDelayed(this@TagView,200)
            }
        })
        animationList.add(animatorSet)
    }


    private fun notifyItemSetChanged(index:Int):Boolean{
        if (index < contentLayout.childCount){
            val itemView = contentLayout.getChildAt(index)
            for (notifyPosition in notifyPositionList){
                if (notifyPosition == index){
                    notifyPositionList.remove(notifyPosition)
                    contentLayout.removeView(itemView)
                    return true
                }
            }
        }
        return false
    }

    private fun notifyDataSetChanged(){
        val adapter = this.adapter?:return
        synchronized(this){
            for (index in 0 until adapter.getItemCount()){
                val type = adapter.getItemViewType(index)
                var holder = holderHashMap[index]?:adapter.onCreateViewHolder(this,type)
                if (index < contentLayout.childCount){
                    var isNew = notifyItemSetChanged(index)
                    if (isNew) holder = adapter.onCreateViewHolder(this,type)
                    adapter.onBindViewHolder(holder,index)
                    if (isNew) addView(holder.itemView)
                }else{
                    adapter.onBindViewHolder(holder,index)
                    addView(holder.itemView)
                }
                holderHashMap[index] = holder
            }
            for (index in adapter.getItemCount() until contentLayout.childCount){
                val itemView = contentLayout.getChildAt(index)
                startRemoveAnim(itemView)
            }
            notifyPositionList.clear()
            checkPositionHandler.removeCallbacksAndMessages(null)
            checkPositionHandler.postDelayed(this,10)
        }
    }


    private fun addNotifyPosition(startPosition:Int,endPosition:Int){
        synchronized(this){
            notifyPositionList.clear()
            for (index in startPosition.. endPosition){
                notifyPositionList.add(index)
            }
        }
        notifyDataSetChanged()
    }

    private fun notifyItemRemove(startPosition:Int,endPosition: Int){
        val adapter = this.adapter?:return
        synchronized(this){
            for (index in startPosition.. endPosition){
                if (index < contentLayout.childCount) {
                    val itemView = contentLayout.getChildAt(index)
                    startRemoveAnim(itemView)
                }
            }
            val range = endPosition - startPosition
            for (index in (adapter.getItemCount() + range - 1) until contentLayout.childCount){
                val itemView = contentLayout.getChildAt(index)
                startRemoveAnim(itemView)
            }
            notifyPositionList.clear()
            checkPositionHandler.removeCallbacksAndMessages(null)
            checkPositionHandler.postDelayed(this,100)
        }
    }

    fun addHeadView(view:View) = headLayout.addView(view)

    fun addFootView(view: View) = footLayout.addView(view)

    fun removeHeadView(view: View) = startRemoveAnim(view)

    fun removeFootView(view: View) = startRemoveAnim(view)

    private fun startRemoveAnim(view: View){
        if (view.parent is ViewGroup)return
        val animation = ViewAnimHelper.getAnimation()
        val builder = ViewAnimHelper.getBuilder(animation)
        ViewAnimHelper.setHeight(builder,0,view)
        ViewAnimHelper.setWidth(builder,0,view)
        ViewAnimHelper.setMarginBottom(builder,0,view)
        ViewAnimHelper.setMarginRight(builder,0,view)
        ViewAnimHelper.setMarginLeft(builder,0,view)
        ViewAnimHelper.setMarginTop(builder,0,view)
        animation.addListener(object :AppAnimatorListener(){
            override fun onAnimationEnd(p0: Animator) {
                super.onAnimationEnd(p0)
                val parent = view.parent
                if (parent is ViewGroup){
                    parent.removeView(view)
                }
                animationList.remove(animation)
            }
        })
        animationList.add(animation)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        checkPositionHandler.removeCallbacksAndMessages(null)
        for (animationSet in animationList)
            ViewAnimHelper.cancel(animationSet)
    }


    abstract class TagAdapter {
        var tagView:TagView?=null

        abstract fun onCreateViewHolder(arent: ViewGroup, viewType: Int): TagViewHolder

        abstract fun onBindViewHolder(holder: TagViewHolder, position: Int)

        abstract fun getItemViewType(position: Int): Int

        abstract fun getItemCount(): Int

        fun notifyDataSetChanged() = tagView?.notifyDataSetChanged()

        fun notifyDataSetChanged(startPosition:Int,endPosition:Int) = tagView?.addNotifyPosition(startPosition, endPosition)

        fun notifyDataSetChangedRange(startPosition:Int,itemCount:Int) = notifyDataSetChanged(startPosition, startPosition+itemCount)

        fun notifyDataSetChanged(startPosition:Int) = notifyDataSetChangedRange(startPosition, 1)

        fun notifyItemRemove(startPosition:Int,endPosition:Int) = tagView?.notifyItemRemove(startPosition, endPosition)

        fun notifyItemRemoveRange(startPosition:Int,itemCount:Int) = tagView?.notifyItemRemove(startPosition, itemCount+startPosition)
    }


    data class CowData(val views:ArrayList<View>, var width:Int, var height:Int)

}