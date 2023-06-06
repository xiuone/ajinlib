package com.xy.base.widget.label.listener

import android.view.View

interface LabelViewClickedListener<T> {
    fun onLabelClicked(childView:View,data:T){}
}