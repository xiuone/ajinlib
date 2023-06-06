package com.xy.chat.chat.input.more

import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import com.xy.base.widget.indicator.IndicatorView
import com.xy.base.widget.viewpager.AppViewPagerAdapter
import com.xy.chat.chat.ChatBaseAssembly

class ChatInputMoreAssembly<T: ChatInputMoreAssembly.ChatInputMoreButtonListener>(view: ChatInputMoreAssemblyView<T>):
    ChatBaseAssembly<ChatInputMoreAssembly.ChatInputMoreAssemblyView<T>>(view){
    //横向数量
    private val cowNumber by lazy { this.view?.onCreateCowNumber()?:0 }
    //纵向数量
    private val rowNumber by lazy { this.view?.onCreateRowNumber()?:0 }

    private val viewPager by lazy { this.view?.onCreateViewPager() }
    private val indicatorView by lazy { this.view?.onCreateIndicatorView() }
    private val moreList by lazy { this.view?.onCreateMoreButtonList()?:ArrayList() }


    override fun onCreateInit() {
        super.onCreateInit()
        indicatorView?.bindViewPager(viewPager)

        val childViewList = ArrayList<View>()
        val pageSize = cowNumber*rowNumber
        var allPage = moreList.size/pageSize
        allPage += if (moreList.size % pageSize == 0) 0 else 1

        getContext()?.run {
            for (index in 0 until allPage){
                val childView = FrameLayout(this)
                addPageItem(index,pageSize,childView)
                childView.viewTreeObserver.addOnPreDrawListener(ChildViewTreeObserver(childView))
                childViewList.add(childView)
            }
        }
        viewPager?.adapter = AppViewPagerAdapter(childViewList)
    }

    /**
     * 添加Item
     */
    private fun addPageItem(page:Int,pageSize:Int,childView:FrameLayout){
        for (index in 0 until  pageSize){
            val pageNumber = index+page*pageSize
            if (pageNumber >= moreList.size){
                return
            }
            val buttonListener = moreList[pageNumber]
            val itemView  = buttonListener.onCreateItemView()
            itemView.visibility = View.INVISIBLE
            childView.addView(itemView)
            itemView.setOnClickListener{
                buttonListener.onChatInputMoreCallBack()
            }
        }
    }

    /**
     * view的变化
     */
    private inner class ChildViewTreeObserver(private val frameLayout: FrameLayout): ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            val height: Int = frameLayout.measuredHeight - frameLayout.paddingTop - frameLayout.paddingBottom
            val width: Int = frameLayout.measuredWidth - frameLayout.paddingLeft - frameLayout.paddingRight

            for (index in 0 until frameLayout.childCount){
                val childView = frameLayout.getChildAt(index)
                val itemWidth = width / cowNumber
                val itemHeight = height / rowNumber
                val layoutParams = FrameLayout.LayoutParams(itemWidth,itemHeight)
                layoutParams.leftMargin = (index % cowNumber) * itemWidth
                layoutParams.topMargin = (index / cowNumber) * itemHeight
                childView.layoutParams = layoutParams
                childView.visibility = View.VISIBLE
            }

            frameLayout.viewTreeObserver.removeOnPreDrawListener(this)
            return true
        }
    }


    interface ChatInputMoreAssemblyView<T: ChatInputMoreButtonListener>:ChatBaseAssemblyView{
        fun onCreateViewPager():ViewPager?=null
        fun onCreateIndicatorView():IndicatorView?=null
        fun onCreateCowNumber():Int
        fun onCreateRowNumber():Int
        fun onCreateMoreButtonList():MutableList<T>
    }


    interface ChatInputMoreButtonListener{
        fun onChatInputMoreCallBack()
        fun onCreateItemView():View
    }

}