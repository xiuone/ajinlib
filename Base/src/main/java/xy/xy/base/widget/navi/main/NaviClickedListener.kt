package xy.xy.base.widget.navi.main

import android.view.View

interface NaviClickedListener<T> {
    fun onClickedNavi(view:View,position:Int,item:T):Boolean
}