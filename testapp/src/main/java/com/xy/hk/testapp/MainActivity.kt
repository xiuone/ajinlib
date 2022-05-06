package com.xy.hk.testapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.xy.baselib.ui.act.ActivityBaseSwipeBack
import com.xy.hk.testapp.tab.TabViewPagerAct

class MainActivity : ActivityBaseSwipeBack() ,View.OnClickListener{

    override fun contentLayoutRes(): Int = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        onBackPressedHome = true;
        findViewById<View>(R.id.glide_button).setOnClickListener(this)
        findViewById<View>(R.id.tab_viewpager_button).setOnClickListener(this)
        findViewById<View>(R.id.tab_fragment_button).setOnClickListener(this)
        findViewById<View>(R.id.recycler_page_act_button).setOnClickListener(this)
        findViewById<View>(R.id.recycler_page_fragemnt_button).setOnClickListener(this)
        findViewById<View>(R.id.pre_load_fragment_button).setOnClickListener(this)
        findViewById<View>(R.id.webview_fragment_button).setOnClickListener(this)
        findViewById<View>(R.id.version_button).setOnClickListener(this)
        findViewById<View>(R.id.select_iv_button).setOnClickListener(this)
        findViewById<View>(R.id.progress_button).setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.glide_button->{
                //glide 演示
            }
            R.id.tab_viewpager_button->{
                startActivity(Intent(this, TabViewPagerAct::class.java))
            }
            R.id.tab_fragment_button->{
                //tab 演示 和fragment的演示
            }
            R.id.recycler_page_act_button->{
                //recyclerView 的act
            }
            R.id.recycler_page_fragemnt_button->{
                //recyclerView 的 fragment
            }
            R.id.pre_load_fragment_button->{
                //预加载处理
            }
            R.id.webview_fragment_button->{
                //webView测试
            }
            R.id.version_button->{
                //版本控制器
            }
            R.id.select_iv_button->{
                //选择图片
            }
            R.id.progress_button->{
                startActivity(Intent(this, ProgressAct::class.java))
            }
        }
    }
}