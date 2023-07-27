package xy.xy.base.utils.exp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import xy.xy.base.utils.Logger
import xy.xy.base.utils.runBackThread
import xy.xy.base.utils.runMain

fun View.setBar() {
    setPadding(paddingLeft, paddingTop + context.getSystemBarHeight(), paddingRight, paddingBottom)
}

fun View.retBar() {
    setPadding(paddingLeft, paddingTop - context.getSystemBarHeight(), paddingRight, paddingBottom)
}

fun View.removeParent(){
    val parent = this.parent
    if (parent is ViewGroup)
        parent.removeView(this)
}

fun View.addNewParent(newGroup:ViewGroup){
    val parent = this.parent
    if (parent == newGroup)return
    if (parent is ViewGroup) parent.removeView(this)
    newGroup.addView(this)
}

var clickedTime = 0L
fun View.setOnClick(listener:View.OnClickListener) {
    this.setOnClickListener {
        if (System.currentTimeMillis() > (clickedTime +500)){
            clickedTime = System.currentTimeMillis()
            listener.onClick(this)
        }
    }
}

fun View.checkDoubleDown(method:()->Unit) {
    if (System.currentTimeMillis() > (clickedTime +500)){
        clickedTime = System.currentTimeMillis()
        method()
    }
}



fun View.shakeAnimation(){
    val translateAnimation = TranslateAnimation(0F, 10F, 0F, 0F)
    translateAnimation.interpolator = CycleInterpolator(3F)
    translateAnimation.duration = 500
    startAnimation(translateAnimation)
}

fun View.getViewPosRect(): Rect {
    val arr = IntArray(2)
    getLocationInWindow(arr)
    return Rect(arr[0], arr[1], arr[0] + width, arr[1] + height)
}

fun View.getViewBitmap(): Bitmap? {
    if (width <= 0 || height <= 0) {
        return null
    }
    val screenshot: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(screenshot)
    canvas.translate(-scrollX.toFloat(), -scrollY.toFloat())
    //我们在用滑动View获得它的Bitmap时候，获得的是整个View的区域（包括隐藏的），如果想得到当前区域，需要重新定位到当前可显示的区域
    draw(canvas) // 将 view 画到画布上
    return screenshot
}

/**
 * 保存图片到指定路径
 * @param context
 * @param *bitmap   要保存的图片
 */
fun View.saveImageToGallery(str:String,suc:String,errorHint:String,sucMethod:(String)->Unit = {},errorMethod:()->Unit = {}){
    val context = context
    runBackThread({
        val bitmap = getViewBitmap()
        if (context != null && bitmap != null) {
            val path = bitmap.saveImageToGallery(context, str)
            runMain({
                context.showToast(suc)
                sucMethod(path)
            })
        }else{
            runMain({
                errorMethod()
                context.showToast(errorHint)
                Logger.e("=======saveImageToGallery=======Error")
            })
        }
    })
}

fun View.saveImage(errorHint:String,sucMethod:(String)->Unit,errorMethod:()->Unit){
    val context = context
    runBackThread({
        val bitmap = getViewBitmap()
        if (context != null && bitmap != null) {
            val path = bitmap.saveBitmap(context)
            if (path != null){
                runMain({
                    sucMethod(path)
                })
                return@runBackThread
            }
        }
        errorMethod()
        context.showToast(errorHint)
        xy.xy.base.utils.Logger.e("=======saveImage=======Error")
    })
}


fun View.measureViewHeight(): Int {
    var p = layoutParams
    if (p == null) {
        p = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    val childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width)
    val childHeightSpec: Int = if (p.height > 0) {
        View.MeasureSpec.makeMeasureSpec(p.height, View.MeasureSpec.EXACTLY)
    } else {
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    }
    measure(childWidthSpec, childHeightSpec)
    return measuredHeight
}

