package com.xy.baselib.utils

import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView

class CodeTimeTools( private val view: View?, millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

    private var finish: String ?=null
    private var startIng: String ?=null
    private var endIng: String ?=null


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
        setView(str)
        view?.isClickable = false //设置不可点击
    }

    override fun onFinish() {
        setView(finish)
        view?.isClickable = true //重新获得点击
    }

    private fun setView(str: String?) {
        val view = view?:return
        if (view is Button) {
            view.text = str
        }
        if (view is TextView) {
            view.text = str
        }
    }
}
