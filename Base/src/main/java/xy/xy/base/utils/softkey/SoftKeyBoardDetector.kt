package xy.xy.base.utils.softkey

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import xy.xy.base.utils.ContextHolder
import xy.xy.base.utils.exp.getScreenAndStatusHeight
import xy.xy.base.utils.Logger
import xy.xy.base.utils.exp.getSpInt

object SoftKeyBoardDetector {
    private val TAG by lazy { "SoftKeyBoardDetector" }
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }
    fun getKeyHeight():Int = ContextHolder.getContext()?.getSpInt(TAG,715)?:715

    fun saveKeyHeight(height:Int){
        mainHandler.removeCallbacksAndMessages(null)
        mainHandler.postDelayed({
            ContextHolder.getContext()?.getSpInt(TAG,height)
        },150)
    }

    private val hashMap = HashMap<Activity, GlobalCallBack>()

    fun isSoftShowing(activity: Activity): Boolean {
        val r = Rect()
        val mRootView = activity.window.decorView
        mRootView.getWindowVisibleDisplayFrame(r)
        val visibleHeight = r.height()
        val mRootViewVisibleHeight: Int = activity.getScreenAndStatusHeight()
        return mRootViewVisibleHeight - visibleHeight > 200
    }

    fun register(activity: Activity?, listener: OnSoftKeyBoardChangeListener?) {
        synchronized(this){
            if (activity == null)return
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
    }

    fun unregister(activity: Activity?) {
        synchronized(this){
            if (activity == null)return
            val globalCallBack = hashMap[activity]
            val mRootView = activity.window.decorView
            if (globalCallBack != null) {
                mRootView.viewTreeObserver.removeOnGlobalLayoutListener(globalCallBack)
                hashMap.remove(activity)
            }
        }
    }

    fun removeListener(activity: Activity, listener: OnSoftKeyBoardChangeListener?) {
        synchronized(this){
            val globalCallBack = hashMap[activity]
            listener?.run {
                globalCallBack?.removeListener(listener)
            }
        }
    }

    fun setClickCloseView(view: View?) {
        view?.setOnClickListener { view1: View? ->
            Logger.e("==``setClickCloseView")
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

    fun showKeyBord(et: EditText?) {
        if (et == null)return
        Handler(Looper.getMainLooper()).post {
            et.isFocusable = true
            et.isFocusableInTouchMode = true
            //请求获得焦点
            et.requestFocus()
            //调用系统输入法
            val inputManager = et.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
            et.setSelection(et.text.toString().length)
        }
    }
}