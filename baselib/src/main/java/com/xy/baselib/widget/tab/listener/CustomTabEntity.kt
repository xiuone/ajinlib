package com.xy.baselib.widget.tab.listener

import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import com.xy.baselib.widget.tab.type.ItemType

interface CustomTabEntity {
    fun getTabTitle(): String?
    @DimenRes
    fun getTabUnSelectTitleSize():Int
    @DimenRes
    fun getTabSelectTitleSize():Int
    @ColorRes
    fun getTabTitleColor():Int
    @ColorRes
    fun getTabTitleStyle():Int

    @DrawableRes
    fun getTabIcon(): Int

    fun layoutType(): ItemType

    @DimenRes
    fun paddingLeft():Int

    @DimenRes
    fun paddingTop():Int

    @DimenRes
    fun paddingRight():Int

    @DimenRes
    fun paddingBottom():Int
}