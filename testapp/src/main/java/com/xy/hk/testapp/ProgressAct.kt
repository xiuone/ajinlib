package com.xy.hk.testapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xy.baselib.ui.act.ActivityBaseSwipeBack

class ProgressAct : ActivityBaseSwipeBack() {
    override fun contentLayoutRes(): Int = R.layout.activity_progress

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

    }
}