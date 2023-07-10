package xy.xy.base.widget.recycler.listener

import android.view.MotionEvent
import android.view.View

interface OnItemTouchListener<T> {
    fun onTouch(v: View,data:T, event: MotionEvent): Boolean
}