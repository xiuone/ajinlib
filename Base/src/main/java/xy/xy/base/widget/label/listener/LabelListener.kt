package xy.xy.base.widget.label.listener

import android.view.View

interface LabelListener<T> {
    fun onCreateLabelView(item:T):View?
}