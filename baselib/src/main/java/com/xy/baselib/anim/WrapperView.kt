package com.xy.baselib.anim

import android.view.View
import android.view.ViewGroup.MarginLayoutParams

class WrapperView(private val mTarget: View?) {
    //必须调用，否则宽度改变但UI没有刷新
    var width: Int
        get() = mTarget?.layoutParams?.width?:0
        set(width) {
            var width = width
            if (width <= 0) width = 0
            mTarget?.layoutParams?.width = width
            mTarget?.requestLayout() //必须调用，否则宽度改变但UI没有刷新
        }

    //必须调用，否则宽度改变但UI没有刷新
    var height: Int
        get() = mTarget?.layoutParams?.height?:0
        set(height) {
            var height = height
            if (height <= 0) height = 0
            mTarget?.layoutParams?.height = height
            mTarget?.requestLayout() //必须调用，否则宽度改变但UI没有刷新
        }

    //必须调用，否则宽度改变但UI没有刷新
    var marginTop: Int
        get() {
            val params = mTarget?.layoutParams
            return if (params is MarginLayoutParams) {
                params.topMargin
            } else 0
        }
        set(marginTop) {
            val params = mTarget?.layoutParams
            if (params is MarginLayoutParams) {
                params.topMargin = marginTop
                mTarget?.requestLayout() //必须调用，否则宽度改变但UI没有刷新
            }
        }

    //必须调用，否则宽度改变但UI没有刷新
    var marginLeft: Int
        get() {
            val params = mTarget?.layoutParams
            return if (params is MarginLayoutParams) {
                params.leftMargin
            } else 0
        }
        set(marginLeft) {
            val params = mTarget?.layoutParams
            if (params is MarginLayoutParams) {
                params.leftMargin = marginLeft
                mTarget?.requestLayout() //必须调用，否则宽度改变但UI没有刷新
            }
        }

    //必须调用，否则宽度改变但UI没有刷新
    var marginRight: Int
        get() {
            val params = mTarget?.layoutParams
            return if (params is MarginLayoutParams) {
                params.rightMargin
            } else 0
        }
        set(marginRight) {
            val params = mTarget?.layoutParams
            if (params is MarginLayoutParams) {
                params.rightMargin = marginRight
                mTarget?.requestLayout() //必须调用，否则宽度改变但UI没有刷新
            }
        }

    //必须调用，否则宽度改变但UI没有刷新
    var marginBottom: Int
        get() {
            val params = mTarget?.layoutParams
            return if (params is MarginLayoutParams) {
                params.bottomMargin
            } else 0
        }
        set(marginBottom) {
            val params = mTarget?.layoutParams
            if (params is MarginLayoutParams) {
                params.bottomMargin = marginBottom
                mTarget?.requestLayout() //必须调用，否则宽度改变但UI没有刷新
            }
        }

    //必须调用，否则宽度改变但UI没有刷新
    var paddingLeft: Int
        get() = mTarget?.paddingLeft?:0
        set(paddingLeft) {
            val paddingTop = mTarget?.paddingTop?:0
            val paddingRight = mTarget?.paddingRight?:0
            val paddingBottom = mTarget?.paddingBottom?:0
            mTarget?.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
            mTarget?.requestLayout() //必须调用，否则宽度改变但UI没有刷新
        }

    //必须调用，否则宽度改变但UI没有刷新
    var paddingTop: Int
        get() = mTarget?.paddingTop?:0
        set(paddingTop) {
            val paddingLeft = mTarget?.paddingLeft?:0
            val paddingRight = mTarget?.paddingRight?:0
            val paddingBottom = mTarget?.paddingBottom?:0
            mTarget?.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
            mTarget?.requestLayout() //必须调用，否则宽度改变但UI没有刷新
        }

    //必须调用，否则宽度改变但UI没有刷新
    var paddingRight: Int
        get() = mTarget?.paddingRight?:0
        set(paddingRight) {
            val paddingLeft = mTarget?.paddingLeft?:0
            val paddingTop = mTarget?.paddingTop?:0
            val paddingBottom = mTarget?.paddingBottom?:0
            mTarget?.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
            mTarget?.requestLayout() //必须调用，否则宽度改变但UI没有刷新
        }

    //必须调用，否则宽度改变但UI没有刷新
    var paddingBottom: Int
        get() = mTarget?.paddingBottom?:0
        set(paddingBottom) {
            val paddingLeft = mTarget?.paddingLeft?:0
            val paddingTop = mTarget?.paddingTop?:0
            val paddingRight = mTarget?.paddingRight?:0
            mTarget?.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
            mTarget?.requestLayout() //必须调用，否则宽度改变但UI没有刷新
        }

}