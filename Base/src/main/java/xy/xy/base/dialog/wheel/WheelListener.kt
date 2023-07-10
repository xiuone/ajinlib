package xy.xy.base.dialog.wheel

import com.github.gzuliyujiang.wheelview.widget.WheelView
import xy.xy.base.dialog.base.BaseDialog
import xy.xy.base.dialog.listener.DialogCancelSureView

interface WheelListener<T:WheelTextProvider> :DialogCancelSureView{
    fun onCreateWheelView(dialog: BaseDialog): WheelView
    fun onCreateWheelLayoutRes():Int
    fun onCreateDataList():MutableList<T>
    fun onSelectCallBack(item:T)
}