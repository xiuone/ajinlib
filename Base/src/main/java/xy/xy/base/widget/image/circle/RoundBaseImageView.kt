package xy.xy.base.widget.image.circle

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import xy.xy.base.utils.glide.GlideProgressListener
import xy.xy.base.widget.bar.progress.ProgressBuild
import xy.xy.base.widget.image.RoundBuild
import xy.xy.base.widget.shadow.ShadowBuilder
import xy.xy.base.widget.shadow.impl.OnDrawImpl
import xy.xy.base.widget.shadow.impl.ShadowBuilderImpl
import kotlin.math.max
import kotlin.math.min

abstract class RoundBaseImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatImageView(context, attrs, defStyleAttr),GlideProgressListener{
    val builder by lazy { RoundBuild(this,attrs) }
    private val mPaintBitmap by lazy { Paint(Paint.ANTI_ALIAS_FLAG)  }
    private val mPaintFrame by lazy { Paint(Paint.ANTI_ALIAS_FLAG)  }
    protected var mRawBitmap: Bitmap? = null
    protected var mShader: BitmapShader? = null
    protected val mMatrix by lazy { Matrix() }

    val shadowBuilderImpl: ShadowBuilderImpl by lazy { ShadowBuilderImpl(ShadowBuilder(this, attrs)) }
    private val onDrawImpl: OnDrawImpl by lazy { OnDrawImpl(this, shadowBuilderImpl) }

    private val progressBuild = ProgressBuild(this, attrs)



    init {
        builder.init()
        onDrawImpl.initView()
        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        if (builder.showShaow) {
            val newLeft = onDrawImpl.getPaddingLeft() + left
            val newRight = onDrawImpl.getPaddingRight() + right
            val newTop = onDrawImpl.getPaddingTop() + top
            val newBottom = onDrawImpl.getPaddingBottom() + bottom
            super.setPadding(newLeft, newTop, newRight, newBottom)
        }else{
            super.setPadding(left, top, right, bottom)
        }
    }


    override fun onDraw(canvas: Canvas?) {
        if (builder.showShaow) onDrawImpl.onDraw(canvas)
        var viewMinSize = builder.getViewMinSize()
        val rawBitmap = getCurrentBitmap()
        if (rawBitmap != null) {
            drawBitmap(canvas,rawBitmap,mPaintBitmap)
        } else {
            super.onDraw(canvas)
        }
        mPaintFrame.color = builder.frameColor
        mPaintFrame.style = Paint.Style.STROKE
        viewMinSize = max(0,min(viewMinSize/2,builder.frameSize))
        if (viewMinSize >0){
            mPaintFrame.strokeWidth = viewMinSize.toFloat()
            drawFrame(canvas,mPaintFrame)
        }
        if (builder.showShaow) onDrawImpl.onDrawStoke(canvas)
        onDrawLoadProgress(canvas)
    }



    private fun getProgressRectF():RectF{
        val radius = min(width, height) / 6
        val left = width/2F - radius + progressBuild.stokeWidth/2
        val right = width/2F + radius - progressBuild.stokeWidth/2
        val top = height/2F - radius + progressBuild.stokeWidth/2
        val bottom = height/2F + radius - progressBuild.stokeWidth/2
        return RectF(left,top,right,bottom)
    }

    private fun getProgressPaint(color:Int):Paint{
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE    // 只描边，不填充
        paint.strokeCap = Paint.Cap.ROUND   // 设置圆角
        paint.isAntiAlias = true              // 设置抗锯齿
        paint.isDither = true                 // 设置抖动
        paint.strokeWidth = progressBuild.stokeWidth
        paint.color = color
        return paint
    }


    private fun onDrawLoadProgress(canvas: Canvas?){
        if (progressBuild.progress <= 0 || progressBuild.progress >= 100)return
        val backPaint = getProgressPaint(progressBuild.backgroundColor)
        val backRect = getProgressRectF()
        canvas?.drawArc(backRect, 0F, 360F, false, backPaint)
        val progressPaint = getProgressPaint(progressBuild.backgroundColor)
        val progressRect = getProgressRectF()
        canvas?.drawArc(progressRect, 275F, 360 * progressBuild.progress / 100F, false, progressPaint)
    }

    /**
     * 绘制图片
     */
    abstract fun drawBitmap(canvas: Canvas?,bitmap: Bitmap,paintBitmap:Paint)

    /**
     * 绘制边框
     */
    abstract fun drawFrame(canvas: Canvas?,paint: Paint)

    /**
     * 当前得背景
     */
    protected fun getCurrentBitmap(): Bitmap? {
        var bitmap: Bitmap?= null
        var drawable = this.drawable
        when (drawable) {
            is BitmapDrawable -> {
                bitmap = drawable.bitmap
            }
            is ColorDrawable -> {
                val rect = drawable.getBounds()
                val width = rect.right - rect.left
                val height = rect.bottom - rect.top
                val color = drawable.color
                val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

                val canvas = Canvas(newBitmap)


                val alpha = Color.alpha(color)
                val red = Color.red(color)
                val green = Color.green(color)
                val blue = Color.blue(color)
                canvas.drawARGB(alpha,red,green,blue)
                bitmap = newBitmap
            }
        }
        return bitmap
    }




    override fun onProgress(url: String?, progress: Int, bytesRead: Long, totalBytes: Long) {
        progressBuild.progress = progress
        invalidate()
    }

 }