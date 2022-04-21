package com.xy.baselib.widget.msg

import android.widget.TextView

object UnreadMsgUtils {
    fun show(msgView: TextView?, num: Int){
        if (num < 100) {
            msgView?.text = "$num"
        }else{
            msgView?.text = "99+"
        }
    }
}