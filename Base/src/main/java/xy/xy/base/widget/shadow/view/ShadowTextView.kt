package xy.xy.base.widget.shadow.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import xy.xy.base.widget.shadow.impl.ShadowBuilderImpl
import xy.xy.base.widget.shadow.ShadowBuilder
import xy.xy.base.widget.shadow.impl.OnDrawExpListener
import xy.xy.base.widget.shadow.impl.OnDrawImpl

open class ShadowTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0 ) :
    AppCompatTextView(context, attrs, defStyleAttr), OnDrawExpListener {
    val shadowBuilderImpl: ShadowBuilderImpl by lazy { ShadowBuilderImpl(ShadowBuilder(this, attrs)) }
    private val onDrawImpl: OnDrawImpl by lazy { OnDrawImpl(this, shadowBuilderImpl,this) }

    init {
        onDrawImpl.initView()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        val newLeft = onDrawImpl.getPaddingLeft()+left
        val newRight = onDrawImpl.getPaddingRight()+right
        val newTop = onDrawImpl.getPaddingTop()+top
        val newBottom = onDrawImpl.getPaddingBottom()+bottom
        super.setPadding(newLeft, newTop, newRight, newBottom)
    }

    override fun onDraw(canvas: Canvas) {
        onDrawImpl.onDraw(canvas)
        super.onDraw(canvas)
        onDrawImpl.onDrawStoke(canvas)
    }
}