package com.xy.baselib.widget.tab

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewpager.widget.ViewPager
import com.xy.baselib.R
import com.xy.baselib.adapter.AppFragementPagerAdapter
import com.xy.baselib.widget.tab.anim.TabAnimHelper
import com.xy.baselib.widget.tab.anim.TabAnimUpdateListener
import com.xy.baselib.widget.tab.child.ChildViewHelper
import com.xy.baselib.widget.tab.draw.TabDraw
import com.xy.baselib.widget.tab.listener.OnTabSelectListener
import com.xy.baselib.widget.tab.type.TabShowType
import com.xy.baselib.widget.tab.utils.FragmentChangeManager
import com.xy.baselib.exp.getViewPosRect

class TabLayout<T> @JvmOverloads constructor(context: Context, private val attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    LinearLayout(context, attrs, defStyleAttr),TabAnimUpdateListener, LifecycleObserver {
    private var mFragmentChangeManager :FragmentChangeManager?=null
    private var viewPager:ViewPager?=null
    private val matchParent = ViewGroup.LayoutParams.MATCH_PARENT
    private val wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT

    private val mTabEntitys by lazy {  ArrayList<T>() }
    private val itemTabList by lazy { HashMap<Int,View>() }
    private val tabAnimHelper by lazy { TabAnimHelper(this) }
    private val childViewHelper by lazy { ChildViewHelper<T>(context,attrs) }
    private val tabDraw : TabDraw by lazy { TabDraw(this,attrs) }
    var tabShowType:TabShowType = TabShowType.EQUALLY
    private var tabSpace = 0

    var listener:OnTabSelectListener?=null

    private var selectPosition = 0
        set(value) {
            val view = itemTabList[value]
            tabAnimHelper.setSelect(view,this)
            if (value == selectPosition)return
            viewPager?.currentItem = value
            listener?.onTabSelect(value)
            mFragmentChangeManager?.setFragments(value)
            field = value;
        }

    init {
        tabDraw.init()
        childViewHelper.init()
        orientation = HORIZONTAL
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabLayout)
            tabSpace = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tab_select_spacing,2)
        }
    }




    /** 关联数据支持同时切换fragments  */
    fun setTabData(tabEntitys: ArrayList<T>?, manager: FragmentManager?
                        , @IdRes containerViewId: Int, fragments: ArrayList<Fragment>?) {
        if (tabEntitys == null || manager == null || fragments == null)return
        mFragmentChangeManager = FragmentChangeManager(manager, containerViewId, fragments)
        setTabData(tabEntitys)
    }

    /** 关联数据支持同时切换fragments  */
    fun setTabData(tabEntitys: ArrayList<T>?, manager: FragmentManager?,
                   viewPager: ViewPager?, fragments: ArrayList<Fragment>?) {
        if (tabEntitys == null || manager == null || fragments == null || viewPager == null)return
        this.viewPager = viewPager
        viewPager.adapter = AppFragementPagerAdapter(manager,fragments)
        setTabData(tabEntitys)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageSelected(position: Int) {
                selectPosition = position
            }
        })
    }



    @Synchronized
    fun setTabData(tabEntitys: ArrayList<T>?){
        if (tabEntitys == null)return
        itemTabList.clear()
        this.mTabEntitys.clear()
        this.mTabEntitys.addAll(tabEntitys)
        notifyDataSetChanged()
        selectPosition = 0;
    }


    /** 更新数据  */
    @Synchronized
    private fun notifyDataSetChanged() {
        removeAllViews()
        for ((index,item) in mTabEntitys.withIndex()){
            val view = childViewHelper.getView(item)
            view.layoutParams = when(tabShowType){
                TabShowType.EQUALLY-> LayoutParams(matchParent,matchParent,1F)
                TabShowType.SCROLLER->LayoutParams(wrapContent,matchParent)
            }
            if (tabShowType == TabShowType.SCROLLER){
                view.setPadding(tabSpace,0,tabSpace,0)
            }
            this.addView(view)
            itemTabList[index] = view
            view.setOnClickListener{ selectPosition = index }
        }
    }

    open fun setCurrentSelectPosition(position:Int){
        selectPosition = position
        for ((index,view) in itemTabList) {
            if (mTabEntitys.size>index){
                childViewHelper.setTabSelect(view,mTabEntitys[index], index == position)
            }
            if (position == index){
                if (tabDraw.isInit()) {
                    tabAnimHelper.setTargetView(view, this)
                }else{
                    tabAnimHelper.setSelect(view, this)
                }
            }
        }
    }

    /**
     * 动画更新
     */
    override fun onTabAnimationUpdate(valueAnimProgress :Float,valueAnimCenterX: Float, valueAnimCenterY: Float, valueAnimWidth: Float, valueAnimHeight: Float) {
        tabDraw?.onTabAnimationUpdate(valueAnimCenterX,valueAnimCenterY,valueAnimWidth,valueAnimHeight)
        for ((index,view) in itemTabList) {
            val rect = view.getViewPosRect()
            val fatherRect = getViewPosRect()

            val centerX = rect.left + rect.width()/2F - fatherRect.left
            val viewWidth = rect.width().toFloat()

            val left = centerX - viewWidth / 2F
            val right = centerX + viewWidth / 2F
            if (mTabEntitys.size>index){
                childViewHelper.setTabSelect(view,mTabEntitys[index], valueAnimCenterX in left..right)
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.run {
            if (tabDraw.isInit()){
                tabAnimHelper?.setTargetView(itemTabList[selectPosition],this@TabLayout)
            }
            tabDraw?.onDraw(this)
        }
        super.dispatchDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        selectPosition = selectPosition
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroyed(owner: LifecycleOwner) {
        tabAnimHelper.cancel()
    }
}