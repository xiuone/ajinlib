package xy.xy.base.widget.image.circle

import android.content.Context
import android.graphics.*
import android.util.AttributeSet

/**
 * 圆角 得imageView
 */
open class RoundImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RoundBaseImageView(context, attrs, defStyleAttr){

    /**
     * 绘制带圆角的图片
     */
    override fun drawBitmap(canvas: Canvas?, rawBitmap: Bitmap,paintBitmap:Paint) {
        val dstWidth = width.toFloat()
        val dstHeight = height.toFloat()
        if (mShader == null || rawBitmap != mRawBitmap) {
            mRawBitmap = rawBitmap
            mShader = BitmapShader(rawBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }
        if (mShader != null) {
            mMatrix.setScale(dstWidth / rawBitmap.width, dstHeight / rawBitmap.height)
            mShader?.setLocalMatrix(mMatrix)
        }
        paintBitmap.shader = mShader
        canvas?.drawPath(builder.getPath(builder.getFrameSize()),paintBitmap)
    }

    /**
     * 绘制边框
     */
    override fun drawFrame(canvas: Canvas?, paint: Paint) {
        canvas?.drawPath(builder.getPath(paint.strokeWidth),paint)
    }
}