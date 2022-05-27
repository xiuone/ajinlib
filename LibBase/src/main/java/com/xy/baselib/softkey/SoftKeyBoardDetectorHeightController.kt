package com.xy.baselib.softkey

import android.content.Context
import com.xy.baselib.config.BaseConfig
import com.xy.baselib.life.ActivityController.Companion.controller
import com.xy.baselib.softkey.SoftKeyBoardDetector.register
import com.xy.baselib.softkey.SoftKeyBoardDetector.removeListener

class SoftKeyBoardDetectorHeightController : OnSoftKeyBoardChangeListener {
    private val KEY_SP_KEYBORD_HEIGHT = "KEY_SP_KEYBORD_HEIGHT"
    private var sKeybordHeight = 0

    override fun keyBoardShow(context: Context, height: Int) {
        if (height > 0) {
            BaseConfig.spHelperUtils.setInt(context,KEY_SP_KEYBORD_HEIGHT, height)
            sKeybordHeight = height
            controller.currentActivity?.run {
                removeListener(this, heightController)
            }
        }
    }

    /**
     * 监听润键盘的高度
     */
    fun detectKeyBord(context: Context) {
        if (sKeybordHeight == 0) {
            controller.currentActivity?.run {
                register(this, heightController)
            }
        }
    }

    fun getKeyBordHeight(context: Context):Int{
        var keyWordHeight: Int =  BaseConfig.spHelperUtils.getInt(context,KEY_SP_KEYBORD_HEIGHT, 0)
        if (keyWordHeight == 0) {
            keyWordHeight = 833
        }
        return keyWordHeight
    }

    companion object {
        val heightController = SoftKeyBoardDetectorHeightController()




    }
}