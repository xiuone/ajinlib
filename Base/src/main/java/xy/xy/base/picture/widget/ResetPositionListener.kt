package xy.xy.base.picture.widget

import android.graphics.RectF

interface ResetPositionListener {
    fun onPosition(index:Int):IntArray
    fun onPositionRecF(index:Int):RectF
    fun onRowSize():Int
}