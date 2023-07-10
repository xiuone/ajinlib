package xy.xy.base.widget.shadow.impl

import android.view.View
import xy.xy.base.widget.shadow.ShadowBuilder
import kotlin.math.max

open class OnSizeChangeImpl(protected val view: View, protected var builderImpl: ShadowBuilderImpl) :
    OnSizeChangeListener {
    protected val builder: ShadowBuilder by lazy {builderImpl.builder  }
    override fun initView() {
        view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)
        view.setBackgroundColor(0X00000000)
        view.postInvalidate()
    }


    override fun getPaddingLeft(): Int = max(builderImpl.arrowLeft(),builderImpl.shadowLeft()).toInt()
    override fun getPaddingRight(): Int = max(builderImpl.arrowRight(),builderImpl.shadowRight()).toInt()
    override fun getPaddingTop(): Int = max(builderImpl.arrowTop(),builderImpl.shadowTop()).toInt()
    override fun getPaddingBottom(): Int = max(builderImpl.arrowBottom(),builderImpl.shadowBottom()).toInt()
}
