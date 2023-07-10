package xy.xy.base.widget.navi.main

import android.view.View
import android.widget.ImageView
import android.widget.TextView

interface NaviViewListener<T:NaviListener> {
    fun onResetView(view:NaviView<T>)
    fun createItemView(item:T): View
    fun createItemImageView(index:Int,view: View,item: T): ImageView?
    fun createItemTextView(index: Int,view: View,item: T): TextView?

    fun findItemTextView(index: Int,view: View): TextView?
    fun findItemImageView(index: Int,view: View): ImageView?
}