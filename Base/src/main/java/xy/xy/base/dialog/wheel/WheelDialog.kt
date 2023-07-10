package xy.xy.base.dialog.wheel

import android.content.Context
import xy.xy.base.dialog.base.BaseBottomDialog
import xy.xy.base.utils.exp.setOnClick

class WheelDialog<T:WheelTextProvider>(context: Context,private val wheelListener:WheelListener<T>):BaseBottomDialog(context) {
    private val resView by lazy { this.wheelListener.onCreateWheelView(this) }
    private val dataList by lazy { this.wheelListener.onCreateDataList() }

    override fun layoutRes(): Int = this.wheelListener.onCreateWheelLayoutRes()

    override fun initView() {
        super.initView()
        resView.data = dataList
        this.wheelListener.onCreateDialogSureView(this)?.setOnClick{
            val item = resView.getCurrentItem<T>()
            if (item != null)
                this.wheelListener.onSelectCallBack(item)
            dismiss()
        }
        this.wheelListener.onCreateDialogCancelView(this)?.setOnClick{
            dismiss()
        }
    }

    override fun showDialog(any: Any?) {
        super.showDialog(any)
        for ((index,item) in dataList.withIndex()){
            if (item.onType() == any){
                resView.setDefaultValue(item)
                return
            }
        }
    }
}