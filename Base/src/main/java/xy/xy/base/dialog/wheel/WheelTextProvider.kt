package xy.xy.base.dialog.wheel

import com.github.gzuliyujiang.wheelview.contract.TextProvider

interface WheelTextProvider :TextProvider {
    fun onType():Any
}