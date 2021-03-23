package com.latrun.ajinlib

import android.view.View
import com.xy.baselib.ui.act.BaseAct

class MainActivity :BaseAct() {
    private val defind_img_url = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1346711909,1199089208&fm=26&gp=0.jpg"
    override fun initView() {
        setContentLayout(R.layout.activity_main)
//        GlideUtils.show(defind_img_url,loading,GlideUtils.getOption())
    }

    override fun statusBarView(): View? = getTitleFrameLayout()
}