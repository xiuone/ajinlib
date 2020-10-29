package com.latrun.ajinlib

import android.view.View
import com.jianbian.baselib.ui.act.BaseAct

class MainActivity :BaseAct() {
    override fun initView() {
        setContentLayout(R.layout.activity_main)
    }

    override fun statusBarView(): View? = getTitleFrameLayout()
}