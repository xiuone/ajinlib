package com.xy.base.assembly.common.mbuffer

import com.xy.base.assembly.base.BaseAssembly
import com.xy.base.R
import com.xy.base.utils.exp.clearAllCache
import com.xy.base.utils.exp.getFormatSize
import com.xy.base.utils.exp.getResString
import com.xy.base.utils.exp.getTotalCacheSize
import com.xy.base.utils.exp.setOnClick
import com.xy.base.utils.exp.showToast

class BufferAssembly(view: BufferAssemblyView) : BaseAssembly<BufferAssemblyView>(view){
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