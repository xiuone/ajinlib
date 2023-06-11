package com.xy.base.act

import android.os.Bundle
import com.xy.base.R
import com.xy.base.utils.exp.getResString
import com.xy.base.utils.exp.showToast
import com.xy.base.utils.softkey.SoftKeyBoardDetector
import com.xy.base.widget.swipe.listener.OnSwipeBackListener
import com.xy.base.widget.swipe.listener.SwipeHelperListener

/**
 * 策划返回的activity
 */
abstract class ActivityBaseSwipeBack : ActivityBaseStatusBar(),
    OnSwipeBackListener {

    var onBackPressedHome = false
    private var onBackTime = 0L
    private val onBackDelayTime = 1500L





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWipeRightBack(true)
        setWipeLeftBack(true)
        if (rootView is SwipeHelperListener) {
            (rootView as SwipeHelperListener?)?.getSwipeBackHelper()?.setSwipeBackListener(this)
        }
    }

    fun setWipeRightBack(status: Boolean) {
        if (rootView is SwipeHelperListener) {
            (rootView as SwipeHelperListener?)?.getSwipeBackHelper()?.isLeftBack = status
        }
    }

    fun setWipeLeftBack(status: Boolean) {
        if (rootView is SwipeHelperListener) {
            (rootView as SwipeHelperListener?)?.getSwipeBackHelper()?.isRightBack = status
        }
    }

    override fun onBackPressed() {
        if (SoftKeyBoardDetector.isSoftShowing(this)){
            SoftKeyBoardDetector.closeKeyBord(rootView)
            return
        }
        if (onBackPressedHome && (System.currentTimeMillis() - onBackTime) > onBackDelayTime){
            showToast(getResString(R.string.logout_app))
            onBackTime = System.currentTimeMillis()
            return
        }
        super.onBackPressed()

    }
}