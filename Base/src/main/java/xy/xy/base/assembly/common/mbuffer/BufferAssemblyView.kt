package xy.xy.base.assembly.common.mbuffer

import android.view.View
import android.widget.TextView
import xy.xy.base.assembly.base.BaseAssemblyViewWithContext

interface BufferAssemblyView : BaseAssemblyViewWithContext {
    /**
     * 显示缓存的TextView
     */
    fun onBufferTextView():TextView?

    /**
     * 清楚缓存的View
     */
    fun onClearBufferButton():View?
}