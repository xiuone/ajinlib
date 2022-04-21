package com.xy.baselib.softkey

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.xy.utils.getScreenAndStatusHeight
import com.xy.utils.Logger

object SoftKeyBoardDetector {
    private val hashMap = HashMap<Activity, GlobalCallBack>()

    fun isSoftShowing(activity: Activity): Boolean {
        val r = Rect()
        val mRootView = activity.window.decorView
        mRootView.getWindowVisibleDisplayFrame(r)
        val visibleHeight = r.height()
        val mRootViewVisibleHeight: Int = activity.getScreenAndStatusHeight()
        return mRootViewVisibleHeight - visibleHeight > 200
    }

    fun register(activity: Activity, listener: OnSoftKeyBoardChangeListener?) {
        val mRootView = activity.window.decorView
        var globalCallBack = hashMap[activity]
        if (globalCallBack == null) {
            globalCallBack = GlobalCallBack(mRootView, listener)
            mRootView.viewTreeObserver.addOnGlobalLayoutListener(globalCallBack)
            hashMap[activity] = globalCallBack
        } else {
            globalCallBack.setOnSoftKeyBoardChangeListener(listener)
        }
    }

    fun unregister(activity: Activity) {
//        sIsRegistered = false;
        val globalCallBack = hashMap[activity]
        val mRootView = activity.window.decorView
        if (globalCallBack != null) {
            mRootView.viewTreeObserver.removeOnGlobalLayoutListener(globalCallBack)
            hashMap.remove(activity)
        }
    }

    fun removeListener(activity: Activity, listener: OnSoftKeyBoardChangeListener?) {
        val globalCallBack = hashMap[activity]
        globalCallBack!!.removeListener(listener!!)
    }

    fun showKeyBord(activity: Activity?) {
        showKeyBord(activity?.window?.decorView as View)
    }

    fun setClickCloseView(view: View?) {
        view?.setOnClickListener { view1: View? ->
            com.xy.utils.Logger.e("==``setClickCloseView")
            closeKeyBord(view1)
        }
    }

    fun closeKeyBord(view: View?) {
        if (view == null) return
        Handler(Looper.getMainLooper()).post {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showKeyBord(et: View) {
        Handler(Looper.getMainLooper()).post {
            et.isFocusable = true
            et.isFocusableInTouchMode = true
            //请求获得焦点
            et.requestFocus()
            //调用系统输入法
            val inputManager = et.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}