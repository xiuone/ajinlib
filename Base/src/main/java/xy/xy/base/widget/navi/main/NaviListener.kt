package xy.xy.base.widget.navi.main

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.annotation.DrawableRes
import xy.xy.base.R

interface NaviListener {
    @DrawableRes
    fun drawRes():Int = R.drawable.bg_transparent
    fun textColorRes():Int = Color.TRANSPARENT
    fun textColorStateList(): ColorStateList? = null
    fun titleStr():String
}