package com.xy.baselib.ui.fragment

import android.os.Bundle
import android.view.View
import com.xy.baselib.widget.swipe.listener.SwipeHelperListener

/**
 * 策划返回的activity
 */
abstract class FragmentBaseSwipeBack : FragmentBaseStatus() {
    override fun initView(savedInstanceState: Bundle?, rootView: View?) {
        super.initView(savedInstanceState, rootView)
        setWipeRightBack(false)
        setWipeLeftBack(false)
    }

    fun setWipeRightBack(status: Boolean) {
        if (rootView is SwipeHelperListener) {
            (rootView as SwipeHelperListener?)?.getSwipeBackHelper()?.isRightBack = status
        }
    }

    fun setWipeLeftBack(status: Boolean) {
        if (rootView is SwipeHelperListener) {
            (rootView as SwipeHelperListener?)?.getSwipeBackHelper()?.isLeftBack = status
        }
    }
}