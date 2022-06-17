package com.xy.baselib.softkey

import android.app.Activity
import android.content.Context
import com.xy.baselib.config.BaseObject
import com.xy.baselib.softkey.SoftKeyBoardDetector.register
import com.xy.baselib.softkey.SoftKeyBoardDetector.removeListener

class SoftKeyBoardDetectorHeightController : OnSoftKeyBoardChangeListener {
    private val KEY_SP_KEYBORD_HEIGHT = "KEY_SP_KEYBORD_HEIGHT"
    private var sKeybordHeight = 0

    override fun keyBoardShow(context: Context, height: Int) {
        if (height > 0) {
            BaseObject.spHelperUtils.setInt(context,KEY_SP_KEYBORD_HEIGHT, height)
            sKeybordHeight = height
            BaseObject.actController.currentActivity?.run {
                removeListener(this, BaseObject.keyHeightController)
            }
        }
    }

    /**
     * 监听润键盘的高度
     */
    fun detectKeyBord(activity:Activity) {
        if (sKeybordHeight == 0) {
            register(activity, BaseObject.keyHeightController)
        }
    }

    fun getKeyBordHeight(context: Context):Int{
        var keyWordHeight: Int =  BaseObject.spHelperUtils.getInt(context,KEY_SP_KEYBORD_HEIGHT, 0)
        if (keyWordHeight == 0) {
            keyWordHeight = 833
        }
        return keyWordHeight
    }
}