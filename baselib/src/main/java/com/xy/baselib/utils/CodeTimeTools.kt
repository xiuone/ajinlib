package com.xy.baselib.utils

import android.content.Context
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.xy.baselib.R
import com.xy.baselib.utils.AppUtil.getColor

class CodeTimeTools(private val context: Context?, private val view: View?,
    millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

    @ColorRes
    private var colorIng: Int = R.color.gray_9999

    @ColorRes
    private var colorEnd: Int = R.color.white

    @DrawableRes
    private var bgResIng: Int = R.drawable.bg_transparent

    @DrawableRes
    private var bgResEnd: Int = R.drawable.bg_transparent

    private var finish: String ?=null
    private var startIng: String ?=null
    private var endIng: String ?=null

    fun setRes( colorIng: Int, colorEnd: Int, @DrawableRes bgResIng: Int, @DrawableRes bgResEnd: Int) {
        this.colorIng = colorIng
        this.colorEnd = colorEnd
        this.bgResIng = bgResIng
        this.bgResEnd = bgResEnd
    }

    /**
     *
     */
    fun setHintStr(startIng:String,endIng:String,finish:String){
        this.finish = finish
        this.startIng =startIng
        this.endIng = endIng
        if (TextUtils.isEmpty(this.startIng))
            this.startIng =""
        if (TextUtils.isEmpty(this.endIng))
            this.endIng =""
        if (TextUtils.isEmpty(this.finish))
            this.finish =""
    }

    override fun onTick(millisUntilFinished: Long) {
        val str = "$startIng${(millisUntilFinished / 1000)}s$endIng"
        setView(str, colorIng, bgResIng)
        view?.isClickable = false //设置不可点击
    }

    override fun onFinish() {
        setView(finish, colorEnd, bgResEnd)
        view?.isClickable = true //重新获得点击
    }

    private fun setView(str: String?, @ColorRes color: Int, @DrawableRes bgRes: Int) {
        if (view != null && context != null){
            if (view is Button) {
                view.text = str
                view.setTextColor(getColor(context,color))
            }
            if (view is TextView) {
                view.text = str
                view.setTextColor(getColor(context,color))
            }
        }
        view?.setBackgroundResource(bgRes)
    }

}
