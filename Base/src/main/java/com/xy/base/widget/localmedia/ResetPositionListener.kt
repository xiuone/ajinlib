package com.xy.base.widget.localmedia

interface ResetPositionListener {
    fun onPosition(index:Int):IntArray
    fun onRowSize():Int
}