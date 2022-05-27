package com.xy.baselib.widget.tab

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.xy.baselib.widget.tab.listener.OnTabSelectListener
import com.xy.baselib.widget.tab.type.TabShowType

class TabScrollerLayout<T> @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0)
    : HorizontalScrollView(context, attrs, defStyleAttr){

        private val childView by lazy { TabLayout<T>(context, attrs, defStyleAttr) }

    init {
        val matchParent = ViewGroup.LayoutParams.MATCH_PARENT
        childView.layoutParams = LayoutParams(matchParent,matchParent)
        addView(childView)
        childView.setPadding(0,0,0,0)
        childView.tabShowType = TabShowType.SCROLLER
    }

    fun setSelectionPosition(selectPosition:Int){
        childView.setCurrentSelectPosition(selectPosition)
    }

    fun setTabSelectListener(selectListener: OnTabSelectListener){
        childView.listener = selectListener
    }

    /** 关联数据支持同时切换fragments  */
    fun setTabData(tabEntitys: ArrayList<T>?, manager: FragmentManager?
                   , @IdRes containerViewId: Int, fragments: ArrayList<Fragment>?) {
        childView.setTabData(tabEntitys, manager, containerViewId, fragments)
    }

    @Synchronized
    fun setTabData(tabEntitys: ArrayList<T>?){
        childView.setTabData(tabEntitys)
    }
}