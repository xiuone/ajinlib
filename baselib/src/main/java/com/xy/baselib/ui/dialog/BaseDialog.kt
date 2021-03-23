package com.xy.baselib.ui.dialog

import android.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import com.xy.baselib.utils.AppUtil

abstract class BaseDialog(context: Context) : Dialog(context) {
    var view: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = LayoutInflater.from(context).inflate(LayoutRes(), null)
        window?.decorView?.setPadding(15, 0, 15, 0)
        val lp = window?.attributes
        lp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        val width = WindowManager.LayoutParams.FILL_PARENT
        val proportion: Double = proportion()
        if (proportion != 0.0)
            lp?.width = ((AppUtil.getScreenWidth(context) * proportion).toInt())
        else
            lp?.width = width
        window?.attributes = lp
        window?.setBackgroundDrawableResource(R.color.transparent)
        window?.setGravity(Gravity())
        setContentView(view!!)
        initView()
    }
    @LayoutRes
    abstract fun LayoutRes(): Int
    abstract fun initView()
    abstract fun proportion(): Double
    abstract fun Gravity(): Int
}