package com.xy.baselib.softkey

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener

class GlobalCallBack(private val mRootView: View, mOnSoftKeyBoardChangeListener: OnSoftKeyBoardChangeListener?) : OnGlobalLayoutListener {
    private var mRootViewVisibleHeight = 0//纪录根视图的显示高度
    private val mOnSoftKeyBoardChangeListenerList: MutableList<OnSoftKeyBoardChangeListener> = ArrayList()

    fun setOnSoftKeyBoardChangeListener(mOnSoftKeyBoardChangeListener: OnSoftKeyBoardChangeListener?) {
        synchronized(this){
            for (listener in mOnSoftKeyBoardChangeListenerList) {
                if (listener == mOnSoftKeyBoardChangeListener) return
            }
            if (mOnSoftKeyBoardChangeListener == null) return
            mOnSoftKeyBoardChangeListenerList.add(mOnSoftKeyBoardChangeListener)
        }
    }

    fun removeListener(listener: OnSoftKeyBoardChangeListener) {
        synchronized(this){
            mOnSoftKeyBoardChangeListenerList.remove(listener)
        }
    }

    override fun onGlobalLayout() {
        //获取当前根视图在屏幕上显示的大小
        synchronized(this){
            val mOnSoftKeyBoardChangeListenerList: MutableList<OnSoftKeyBoardChangeListener> = ArrayList()
            mOnSoftKeyBoardChangeListenerList.addAll(this.mOnSoftKeyBoardChangeListenerList)
            val r = Rect()
            mRootView.getWindowVisibleDisplayFrame(r)
            val visibleHeight = r.height()
            if (mRootViewVisibleHeight == 0) {
                mRootViewVisibleHeight = visibleHeight
                return
            }

            //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
            if (mRootViewVisibleHeight == visibleHeight) {
                return
            }


            //根视图显示高度变小超过200，可以看作软键盘显示了
            if (mRootViewVisibleHeight - visibleHeight > 200) {
                for (listener in mOnSoftKeyBoardChangeListenerList) {
                    listener.keyBoardShow(mRootView.context,mRootViewVisibleHeight - visibleHeight)
                }
                mRootViewVisibleHeight = visibleHeight
                return
            }
            if (visibleHeight - mRootViewVisibleHeight > 200) {
                for (listener in mOnSoftKeyBoardChangeListenerList) {
                    listener.keyBoardHide(mRootView.context,visibleHeight - mRootViewVisibleHeight)
                }
                mRootViewVisibleHeight = visibleHeight
                return
            }
        }
    }

    init {
        setOnSoftKeyBoardChangeListener(mOnSoftKeyBoardChangeListener)
    }
}