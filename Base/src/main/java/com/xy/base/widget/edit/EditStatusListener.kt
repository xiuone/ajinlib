package com.xy.base.widget.edit

import com.xy.base.listener.AppTextWatcher

interface EditStatusListener:AppTextWatcher {
    fun onEditorAction(actionId :Int,content:String):Boolean = false
}