package com.xy.hk.testapp

import android.os.Bundle
import com.xy.baselib.ui.act.ActivityBaseSwipeBack
import com.xy.hk.testapp.R

class ProgressAct : ActivityBaseSwipeBack() {
    override fun contentLayoutRes(): Int = R.layout.activity_progress

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

    }
}