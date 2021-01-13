package com.xy.baselib.mvp.impl

import android.view.View

interface PickerListener<T> {
    fun onSelect(view:View,data:T)
}