package com.xy.hk.testapp.tab

import android.os.Bundle
import com.xy.baselib.ui.act.ActivityBaseSwipeBack
import com.xy.baselib.widget.tab.TabLayout
import com.xy.baselib.widget.tab.TabScrollerLayout
import com.xy.baselib.widget.tab.listener.OnTabTextItemListener
import com.xy.hk.testapp.R

class TabViewPagerAct : ActivityBaseSwipeBack() {

    override fun contentLayoutRes(): Int = R.layout.activity_tab

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        initEquallyNoNoTabLayout()
        initScrollerNoNoTabLayout()
    }

    private fun initEquallyNoNoTabLayout(){
        val tabLayout = findViewById<TabLayout<OnTabTextItemListener>>(R.id.equally_nono_tabLayout)
        val data = ArrayList<OnTabTextItemListener>()
        for (index in 0..3){
            data.add(TabMode())
        }
        tabLayout.setTabData(data)
    }

    private fun initScrollerNoNoTabLayout(){
        val tabLayout = findViewById<TabScrollerLayout<OnTabTextItemListener>>(R.id.scroller_nono_tabLayout)
        val data = ArrayList<OnTabTextItemListener>()
        for (index in 0..40){
            data.add(TabMode())
        }
        tabLayout.setTabData(data)
    }
}