package com.latrun.ajinlib

import android.view.View
import com.xy.baselib.ui.act.BaseAct

class MainActivity :BaseAct() {
    override fun initView() {
        setContentLayout(R.layout.activity_main)
        var dialog :TestDialog  = TestDialog(this);
        dialog.show()

    }

    override fun statusBarView(): View? = contentView
}