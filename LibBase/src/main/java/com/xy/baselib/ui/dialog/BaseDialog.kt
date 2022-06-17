package com.xy.baselib.ui.dialog

import android.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import com.xy.baselib.exp.getScreenWidth

abstract class BaseDialog(context: Context) : Dialog(context) {
    protected var rootView:View?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.decorView?.setPadding(15, 0, 15, 0)
        val lp = window?.attributes
        lp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        val width = WindowManager.LayoutParams.FILL_PARENT
        val proportion: Double = proportion()
        if (proportion != 0.0)
            lp?.width = ((context.getScreenWidth() * proportion).toInt())
        else
            lp?.width = width
        window?.attributes = lp
        window?.setBackgroundDrawableResource(R.color.transparent)
        window?.setGravity(gravity())
        setContent()
        initView()
    }

    open fun setContent(){
        val view = LayoutInflater.from(context).inflate(layoutRes(), null)
        setContentView(view)
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        rootView = view
    }

    override fun show() {
        super.show()
        if (!isShowing)
        showAnimation(rootView)
    }

    /**
     * 显示动画
     */
    abstract fun showAnimation(view: View?)


    @LayoutRes
    open fun layoutRes(): Int = R.layout.select_dialog_item
    abstract fun initView()
    abstract fun proportion(): Double
    abstract fun gravity(): Int
}