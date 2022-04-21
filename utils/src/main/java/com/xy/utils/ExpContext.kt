package com.xy.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Space
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.util.*


/**
 * 安装apk
 */
fun Context.installAPK(path:String?){
    path?.run {
        if (!isFileExist())return@run
        path?.setPermission()
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= 24) {
            val packageName: String = packageName
            val apkUri = FileProvider.getUriForFile(this@installAPK, "$packageName.fileprovider", File(this))
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(File(path)), "application/vnd.android.package-archive")
        }
        startActivity(intent)
    }

}

/**
 * 将文件放入相册
 */
fun Context.insertImage(path: String?){
    path?.run {
        if (!isFileExist())return@run
        sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://$path")))
    }
}
/**
 * 获取颜色
 */
fun Context.getResColor(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}

/**
 * 获取drawable
 */
fun Context.getResDrawable(colorRes: Int): Drawable? {
    return ContextCompat.getDrawable(this, colorRes)
}

/**
 * 获取字符串
 */
fun Context.getResString(res: Int): String? {
    return resources.getString(res)
}

/**
 * 获取尺寸
 */
fun Context.getResDimension( @DimenRes resId: Int): Int {
    return resources.getDimensionPixelSize(resId)
}

/**
 * 获取bitmap
 */
fun Context.getBitmapFromRes(res: Int): Bitmap {
    return BitmapFactory.decodeResource(resources, res)
}

/**
 * 将突变变成bitmap并设置大小
 */
fun Context.getBitmapFromRes(@DrawableRes res: Int, newWidth: Int, newHeight: Int): Bitmap {
    var bitMap = BitmapFactory.decodeResource(resources, res)
    val width = bitMap.width
    val height = bitMap.height
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)
    bitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true)
    return bitMap
}

/**
 * dp转px
 */
fun Context.dp2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale ).toInt()
}

/**
 * px转dp
 */
fun Context.px2dp(pxValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (pxValue / scale).toInt()
}

/**
 * sp 转px
 */
fun Context.sp2px(spValue: Float): Int {
    val fontScale = resources.displayMetrics.scaledDensity
    return (spValue * fontScale).toInt()
}

/**
 * px转sp
 */
fun Context.px2sp( pxValue: Float): Int {
    val fontScale = resources.displayMetrics.scaledDensity
    return (pxValue / fontScale ).toInt()
}

/**
 * 获取屏幕
 */
fun Context.getScreenHeight(): Int {
    val displayMetrics = resources.displayMetrics
    return displayMetrics.heightPixels
}

/**
 * 获取屏幕宽度
 */
fun Context.getScreenWidth(): Int {
    val displayMetrics = resources.displayMetrics
    return displayMetrics.widthPixels
}



/**
 * 获取状态栏高度
 */
fun Context.getSystemBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

/**
 * 底部导航栏高度
 * @return
 */
fun Context.getNavigationBarHeight(): Int {
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

fun Context.getScreenAndStatusHeight(): Int {
    return getScreenHeight() + getSystemBarHeight()
}

/**
 * 获取站位图
 */
fun Context.getSpace(): View {
    return getSpace( R.dimen.dp_20)
}

fun Context.getSpace(@DimenRes heightRes: Int): View {
    val space = Space(this);
    val params: ViewGroup.LayoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResDimension(heightRes))
    space.layoutParams = params
    return space;
}


fun Context.copy(text: String) {
    val mClipData = ClipData.newPlainText(getString(R.string.app_name), text)
    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(mClipData)
    showToast("复制成功")
}


fun Context.isFullScreen(): Boolean  {
    return resources.configuration.orientation != Configuration.ORIENTATION_PORTRAIT
}

/**
 * 显示toast
 */
fun Context.showToast(title: String?) {
    if (title.isNullOrEmpty())
        return
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
    }
}



