package com.xy.base.utils.softkey

import android.app.Activity
import android.content.Context
import com.xy.base.utils.exp.getSpInt
import com.xy.base.utils.exp.setSpInt
import com.xy.base.utils.lift.ActivityController
import com.xy.base.utils.softkey.SoftKeyBoardDetector.register
import com.xy.base.utils.softkey.SoftKeyBoardDetector.removeListener

class SoftKeyBoardDetectorHeightController : OnSoftKeyBoardChangeListener {
    private val KEY_SP_KEYBORD_HEIGHT = "KEY_SP_KEYBORD_HEIGHT"
    private var sKeybordHeight = 0

    override fun keyBoardShow(context: Context, height: Int) {
        if (height > 0) {
            context.setSpInt(KEY_SP_KEYBORD_HEIGHT, height)
            sKeybordHeight = height
            ActivityController.instance.currentActivity?.run {
                removeListener(this, instant)
            }
        }
    }

    /**
     * 监听润键盘的高度
     */
    fun detectKeyBord(activity:Activity) {
        if (sKeybordHeight == 0) {
            register(activity, instant)
        }
    }

    fun getKeyBordHeight(context: Context):Int{
        var keyWordHeight: Int =  context.getSpInt(KEY_SP_KEYBORD_HEIGHT, 0)
        if (keyWordHeight == 0) {
            keyWordHeight = 833
        }
        return keyWordHeight
    }

    companion object{
        val instant = SoftKeyBoardDetectorHeightController()
    }
}