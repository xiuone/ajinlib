package com.xy.base.widget.recycler.diver

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View

/**
 * Created by YanZhenjie on 2018/4/20.
 */
internal class Drawer {
    private val mDivider:Drawable
    private val mWidth :Int
    private val mHeight:Int
    constructor(mDivider: Drawable, mWidth: Int,mHeight: Int){
        this.mDivider = mDivider
        this.mWidth = mWidth;
        this.mHeight = mHeight
    }

    constructor(color: Int, mWidth: Int,mHeight: Int){
        this.mDivider = ColorDrawable(color)
        this.mWidth = mWidth;
        this.mHeight = mHeight
    }

    /**
     * Draw the divider on the left side of the Item.
     */
    fun drawLeft(view: View?, c: Canvas?,mWidth: Int = this.mWidth,mHeight: Int = this.mHeight) {
        view?:return
        c?:return
        val left = view.left - mWidth
        val top = view.top - mHeight
        val right = left + mWidth
        val bottom = view.bottom + mHeight
        mDivider.setBounds(left, top, right, bottom)
        mDivider.draw(c)
    }

    /**
     * Draw the divider on the top side of the Item.
     */
    fun drawTop(view: View?, c: Canvas?,mWidth: Int = this.mWidth,mHeight: Int = this.mHeight) {
        view?:return
        c?:return
        val left = view.left - mWidth
        val top = view.top - mHeight
        val right = view.right + mWidth
        val bottom = top + mHeight
        mDivider.setBounds(left, top, right, bottom)
        mDivider.draw(c)
    }

    /**
     * Draw the divider on the top side of the Item.
     */
    fun drawRight(view: View?, c: Canvas?,mWidth: Int = this.mWidth,mHeight: Int = this.mHeight) {
        view?:return
        c?:return
        val left = view.right
        val top = view.top - mHeight
        val right = left + mWidth
        val bottom = view.bottom + mHeight
        mDivider.setBounds(left, top, right, bottom)
        mDivider.draw(c)
    }

    /**
     * Draw the divider on the top side of the Item.
     */
    fun drawBottom(view: View?, c: Canvas?,mWidth: Int = this.mWidth,mHeight: Int = this.mHeight) {
        view?:return
        c?:return
        val left = view.left - mWidth
        val top = view.bottom
        val right = view.right + mWidth
        val bottom = top + mHeight
        mDivider.setBounds(left, top, right, bottom)
        mDivider.draw(c)

    }
}