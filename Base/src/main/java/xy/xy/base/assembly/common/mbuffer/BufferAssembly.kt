package xy.xy.base.assembly.common.mbuffer

import xy.xy.base.R
import xy.xy.base.assembly.base.BaseAssemblyWithContext
import xy.xy.base.utils.exp.clearAllCache
import xy.xy.base.utils.exp.getFormatSize
import xy.xy.base.utils.exp.getResString
import xy.xy.base.utils.exp.getTotalCacheSize
import xy.xy.base.utils.exp.setOnClick
import xy.xy.base.utils.exp.showToast

class BufferAssembly(view: BufferAssemblyView) : BaseAssemblyWithContext<BufferAssemblyView>(view){
    private val bufferTv by lazy { this.view?.onBufferTextView() }
    private val bufferButton by lazy { this.view?.onClearBufferButton() }

    override fun onCreateInit() {
        super.onCreateInit()
        bufferTv?.text = getContext()?.getTotalCacheSize()?.getFormatSize()
        bufferButton?.setOnClick {
            getContext()?.clearAllCache()
            bufferTv?.text = "0.00B"
            getContext()?.showToast(getContext()?.getResString(R.string.clear_cash_suc))
        }
    }
}