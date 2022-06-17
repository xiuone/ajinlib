package com.xy.baselib.viewpager

import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

class ViewPagerListenerImpl(private val viewPager: ViewPager, private val viewPagerChangeListener: AppViewPagerChangeListener?, ) : OnPageChangeListener {
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageSelected(position: Int) {
        viewPagerChangeListener?.onPageSelected(position)
    }

    override fun onPageScrollStateChanged(state: Int) {}

    init {
        viewPager.addOnPageChangeListener(this)
    }
}