package camerax.luck.lib.camerax.widget.type

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.View

/**
 * @author：luck
 * @date：2019-01-04 13:41
 * @describe：TypeButton
 */
abstract class TypeBaseButton(context: Context?, protected val buttonSize: Int) : View(context) {
    protected val centerX by lazy { buttonSize/2.0F }
    protected val centerY by lazy { buttonSize/2.0F }
    protected val buttonRadius by lazy { buttonSize/2.0F }

    protected val mPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    protected val path by lazy { Path() }

    protected val strokeWidth by lazy { buttonSize / 50f }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(buttonSize, buttonSize)
    }
}