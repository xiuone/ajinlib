package xy.xy.base.widget.bar.progress

import android.content.Context
import android.util.AttributeSet

open class ProgressRightStripView  @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ProgressStripView(context, attrs, defStyleAttr) {
    override fun createCenterX(): Int = (startRight() - height ).toInt()
}