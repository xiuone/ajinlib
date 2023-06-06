package com.xy.base.widget.tag

import android.view.LayoutInflater
import android.view.ViewGroup

abstract class TagSingleAdapter<T>(private val layoutRes:Int) :TagBaseAdapter<T>() {

    override fun onCreateViewHolder(arent: ViewGroup, viewType: Int): TagViewHolder {
        val context = arent.context
        val view = LayoutInflater.from(context).inflate(layoutRes, arent, false)
        return TagViewHolder(view)
    }

}