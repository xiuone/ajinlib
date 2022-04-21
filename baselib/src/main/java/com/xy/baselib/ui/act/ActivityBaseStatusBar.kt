package com.xy.baselib.ui.act

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.gyf.immersionbar.ImmersionBar
import com.xy.baselib.R
import com.xy.utils.getResString

/**
 * 状态栏  和标题拦
 */
abstract class ActivityBaseStatusBar : ActivityBaseStatus() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBar()
    }

    fun setKeyboardEnable(enable: Boolean) {
        val immersionBar = ImmersionBar.with(this)
        setKeyboardEnable(enable, immersionBar.barParams.keyboardMode)
    }

    fun setKeyboardEnable(enable: Boolean, keyboardMode: Int) {
        setKeyboardEnable(enable, keyboardMode,statusBarBgColor())
    }

    fun setKeyboardEnable(statusBarBgColor:Int) {
        val immersionBar = ImmersionBar.with(this)
        setKeyboardEnable(immersionBar.barParams.keyboardEnable, immersionBar.barParams.keyboardMode,statusBarBgColor)
    }

    fun setKeyboardEnable(enable: Boolean, keyboardMode: Int,statusBarBgColor :Int) {
        val bar = ImmersionBar.with(this)
            .supportActionBar(false)
            .navigationBarEnable(false)
            .statusBarDarkFont(statusBarDarkFont())
        bar.transparentBar()
        bar.statusBarColorInt(statusBarBgColor)
        bar.titleBar(statusBarView())
        bar.keyboardEnable(enable, keyboardMode)
        bar.init()
    }

    fun adjustResize() {
        setKeyboardEnable(
            false, WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                    or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                    or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }

    fun initBar() {
        setKeyboardEnable(true, WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    /**
     * 是否显示黑色状态拦
     * @return
     */
    protected fun statusBarDarkFont(): Boolean {
        return true
    }

    /**
     * 状态拦颜色修改
     * @return
     */
    open fun statusBarBgColor(): Int = Color.TRANSPARENT

    /**
     * 是否隐藏状态拦
     * @return
     */
    open fun transparentBar(): Boolean = false

    open fun statusBarView():View? = titleFrameLayout


    /**
     * 初始化标题拦
     */
    fun initToolBarAndRightButton(titleRes: Int, rightRes: Int) {
        initToolBar(titleRes)
        initRightButton(rightRes)
    }

    /**
     * 初始化标题拦
     */
    fun initToolBarAndRightIvButton(titleRes: Int, rightRes: Int) {
        initToolBar(titleRes)
        initRightIvButton(rightRes)
    }

    fun initToolBar(title: Int) {
        initToolBar(getResString(title))
    }

    fun initToolBar(title: String?) {
        initBlack()
        val titleTv = findViewById<TextView>(R.id.middle_title)
        titleTv.text = title
    }

    fun initRightButton(rightRes: Int) {
        initRightButton(resources.getString(rightRes))
    }

    fun initRightButton(rightStr: String?) {
        val rightButton = findViewById<TextView>(R.id.toolbar_right_button)
        rightButton?.text = rightStr
    }

    fun initRightIvButton(rightRes: Int) {
        val rightIv = findViewById<ImageView>(R.id.right_iv_button)
        rightIv?.setImageResource(rightRes)
    }

    fun initBlack() {
        val backIv: View = findViewById<View>(R.id.back_iv_button)
        backIv?.setOnClickListener { v: View? -> finish() }
    }

}