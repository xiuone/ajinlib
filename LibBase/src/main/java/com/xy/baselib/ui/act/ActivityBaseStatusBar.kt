package com.xy.baselib.ui.act

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.gyf.immersionbar.ImmersionBar
import com.xy.baselib.MoveKeyBoardController
import com.xy.baselib.R
import com.xy.baselib.exp.getResString
import com.xy.baselib.softkey.SoftKeyBoardDetector

/**
 * 状态栏  和标题拦
 */
abstract class ActivityBaseStatusBar : ActivityBaseStatus() {
    protected val moveKeyBoardController: MoveKeyBoardController by lazy { MoveKeyBoardController(this) }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        initBar()
        if (registerKeyBoard()) {
            SoftKeyBoardDetector.register(this, moveKeyBoardController)
            moveKeyBoardController.keyBoardHide(this, 0)
        }
    }

    fun setKeyboardEnable(enable: Boolean) {
        val immersionBar = ImmersionBar.with(this)
        setKeyboardEnable(enable, immersionBar.barParams.keyboardMode)
    }

    fun setKeyboardEnable(enable: Boolean, keyboardMode: Int) {
        setKeyboardEnable(enable, keyboardMode,statusBarBgColor(),statusBarDarkFont())
    }

    fun setKeyboardEnable(statusBarBgColor:Int) {
        val immersionBar = ImmersionBar.with(this)
        setKeyboardEnable(immersionBar.barParams.keyboardEnable, immersionBar.barParams.keyboardMode,
            statusBarBgColor,immersionBar.barParams.statusBarDarkFont)
    }

    fun setStatusBarDarkFont(statusBarDarkFont:Boolean) {
        val immersionBar = ImmersionBar.with(this)
        setKeyboardEnable(immersionBar.barParams.keyboardEnable, immersionBar.barParams.keyboardMode,
            immersionBar.barParams.statusBarColor,statusBarDarkFont)
    }


    fun setKeyboardEnable(enable: Boolean, keyboardMode: Int,statusBarBgColor :Int,statusBarDarkFont:Boolean) {
        ImmersionBar.with(this)
            .supportActionBar(false)
            .navigationBarEnable(false)
            .statusBarDarkFont(statusBarDarkFont)
            .transparentBar()
            .statusBarColorInt(statusBarBgColor)
            .titleBar(statusBarView())
            .keyboardEnable(enable, keyboardMode)
            .init()
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
    open fun statusBarDarkFont(): Boolean = true

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

    open fun registerKeyBoard():Boolean = false

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
        backIv.setOnClickListener { v: View? -> finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (registerKeyBoard())
            SoftKeyBoardDetector.removeListener(this,moveKeyBoardController)
    }
}