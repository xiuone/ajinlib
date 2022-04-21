package com.xy.baselib.widget.tab

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.xy.baselib.widget.tab.listener.CustomTabEntity
import com.xy.baselib.widget.tab.type.LayoutType
import com.xy.baselib.widget.tab.utils.FragmentChangeManager

abstract class TabBaseLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :FrameLayout(context, attrs, defStyleAttr) {
    protected var mFragmentChangeManager :FragmentChangeManager?=null
    private val mTabEntitys = ArrayList<CustomTabEntity>()
    private val matchParent = ViewGroup.LayoutParams.MATCH_PARENT
    private val wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT
    private val itemTabList by lazy { HashMap<Int,TabItemView>() }
    // 显示模式
    var layoutType:LayoutType = LayoutType.equally
        set(value) {
            field = value
            initLayoutType()
        }
    // 容器
    private var mTabsContainer: ViewGroup? = null

    /** 关联数据支持同时切换fragments  */
    open fun setTabData(tabEntitys: ArrayList<CustomTabEntity>?, manager: FragmentManager?, @IdRes containerViewId: Int, fragments: ArrayList<Fragment>?) {
        if (tabEntitys == null || manager == null || fragments == null)return
        mFragmentChangeManager = FragmentChangeManager(manager, containerViewId, fragments)
        setTabData(tabEntitys)
    }

    open fun setTabData(tabEntitys: ArrayList<CustomTabEntity>?){
        if (tabEntitys == null)return
        this.mTabEntitys.clear()
        this.mTabEntitys.addAll(tabEntitys)
        notifyDataSetChanged()
    }


    /** 更新数据  */
    open fun notifyDataSetChanged() {
        removeAllViews()
        initLayoutType()
        addTabs()
        updateTabStyles()
    }

    private fun initLayoutType(){
        when(layoutType){
            LayoutType.equally->{
                addContainer(this,LinearLayout(context))
            }
            LayoutType.warpperScroller->{
                val scroller = HorizontalScrollView(context)
                this.addView(scroller)
                addContainer(scroller,LinearLayout(context))
            }
            LayoutType.warpperMoreLine->{
                addContainer(this,TabContainerMoreLayout(context))
            }
        }

    }

    /**
     * 重制容器
     */
    private fun addContainer(viewGroup: ViewGroup,mTabsContainer:LinearLayout){
        this.mTabsContainer = mTabsContainer
        mTabsContainer.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        viewGroup.addView(mTabsContainer)
    }

    /**
     * 添加tab
     */
    private fun addTabs(){
        for ((index,value) in mTabEntitys.withIndex()) {
            val itemView = TabItemView(context)
            itemView.tabEntity = value
            when(layoutType){
                LayoutType.equally->{//等分
                    itemView.layoutParams = LinearLayout.LayoutParams(matchParent,matchParent,1F)
                    mTabsContainer?.addView(itemView)
                }
                LayoutType.warpperScroller->{//可以滑动
                    itemView.layoutParams = LinearLayout.LayoutParams(wrapContent,matchParent)
                    mTabsContainer?.addView(itemView)
                }
                LayoutType.warpperMoreLine->{//多行
                    itemView.layoutParams = LinearLayout.LayoutParams(wrapContent,wrapContent)
                    mTabsContainer?.addView(itemView)
                }
            }
            itemTabList[index] = itemView
        }
    }

    abstract fun updateTabStyles();


}