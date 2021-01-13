package com.xy.baselib.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

object AppUtil {
    /**
     * 获取颜色
     */
    fun getColor(context: Context, @ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(context, colorRes)
    }
    /**
     * 获取drawable
     */
    fun getDrawable(context: Context,colorRes: Int): Drawable? {
        return ContextCompat.getDrawable(context, colorRes)
    }

    /**
     * 获取字符串
     */
    fun getString(context: Context,res: Int): String? {
        return context.resources.getString(res)
    }

    /**
     * 将颜色变成bitmap并设置大小
     */
    fun getBitmapFromColor(context: Context,colorRes: Int,width:Int,height:Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(colorRes) //填充颜色
        return bitmap
    }

    /**
     * 获取bitmap
     */
    fun getBitmapFromRes(context: Context,res: Int): Bitmap {
        return BitmapFactory.decodeResource(context.resources, res)
    }

    /**
     * 将突变变成bitmap并设置大小
     */
    fun getBitmapFromRes(context: Context, @DrawableRes res: Int,newWidth: Int,newHeight: Int): Bitmap {
        var bitMap = BitmapFactory.decodeResource(context.resources, res)
        val width = bitMap.width
        val height = bitMap.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        bitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true)
        return bitMap
    }


    fun dp2px(sContext: Context?, dpValue: Float): Int {
        if (sContext == null) return 0
        val scale = sContext.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun px2dp(sContext: Context?, pxValue: Float): Int {
        if (sContext == null) return 0
        val scale = sContext.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun sp2px(sContext: Context?, spValue: Float): Int {
        if (sContext == null) return 0
        val fontScale = sContext.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    fun px2sp(sContext: Context?, pxValue: Float): Int {
        if (sContext == null) return 0
        val fontScale = sContext.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    fun dpToPx(dp: Int, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    fun spToPx(sp: Int, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }


    fun getScreenHeight(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.heightPixels
    }

    fun getScreenWidth(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return displayMetrics.widthPixels
    }

    fun initState(view: View) {
        val systemBarHeight = getSystemBarHeight(view.context)
        view.setPadding(0, systemBarHeight, 0, 0)
    }

    /**
     * 获取状态栏高度
     */
    fun getSystemBarHeight(sContext: Context?): Int {
        if (sContext == null) return 0
        var result = 0
        val resourceId =
            sContext.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = sContext.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     * 底部导航栏高度
     *
     * @param mActivity
     * @return
     */
    fun getNavigationBarHeight(mActivity: Activity): Int {
        val resources = mActivity.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }
}