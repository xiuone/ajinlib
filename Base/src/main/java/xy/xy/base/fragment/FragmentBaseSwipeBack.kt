package xy.xy.base.fragment

import android.os.Bundle
import android.view.View
import xy.xy.base.widget.swipe.listener.SwipeHelperListener

/**
 * 策划返回的activity
 */
abstract class FragmentBaseSwipeBack : FragmentBaseStatusAssembly() {
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