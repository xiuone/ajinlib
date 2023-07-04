package com.xy.base.utils.exp

import android.app.Activity
import android.content.*
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Space
import android.widget.Toast
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.xy.base.R
import com.xy.base.utils.Logger
import com.xy.base.utils.runMain
import java.io.File


private fun Context.getScaledDensity() = resources.displayMetrics.scaledDensity
private fun Context.getDensity() = resources.displayMetrics.density

fun Context?.startMark(packageName: String?){
    val marker = "market://details?id=%s"
    val uri = Uri.parse(String.format(marker,packageName))
    val intent = Intent(Intent.ACTION_VIEW, uri)
    startAppActivity(intent)
}

/**
 * 寻找上下文中的 Activity 对象
 */
fun Context?.findActivity(): Activity? {
    var newContext :Context?  = this?:return null
    while (newContext != null){
        if (newContext is Activity) return newContext
        else if (newContext is ContextWrapper) newContext = newContext.baseContext
    }
    return null
}


fun Context?.startAppActivity(intent: Intent?){
    runMain({
        try {
            if (intent == null)return@runMain
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this?.startActivity(intent)
        }catch (e: Exception){
            Logger.e("======${e.message}")
        }
    })
}



fun Context?.startSetting(){
    val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
    intent.data = Uri.parse("package:${this?.packageName}")
    startAppActivity(intent)
}
/**
 * 安装apk
 */
fun Context.installAPK(path:String?){
    path?.run {
        if (!isFileExist())return@run
        setPermission()
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
        startAppActivity(intent)
    }

}

/**
 * 判断是否安装了什么apk
 */
fun Context.isInstall(packageName: String): Boolean {
    val packageManager = packageManager
    //获取所有已安装程序的包信息
    val packageInfos = packageManager.getInstalledPackages(0)
    //用于存储所有已安装程序的包名
    val packageNames: MutableList<String> = ArrayList()
    //从pinfo中将包名字逐一取出，压入pName list中
    for (i in packageInfos.indices) {
        val packName = packageInfos[i].packageName
        packageNames.add(packName)
    }
    //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
    return packageNames.contains(packageName)
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
fun Context.getResBool(@BoolRes res: Int)  = resources.getBoolean(res)

fun Context.getResInt(@IntegerRes res: Int)  = resources.getInteger(res)

/**
 * 获取颜色
 */
fun Context.getResColor(@ColorRes colorRes: Int) =ContextCompat.getColor(this, colorRes)

/**
 * 获取drawable
 */
fun Context.getResDrawable(colorRes: Int) = ContextCompat.getDrawable(this, colorRes)

fun Context.getResBitmap(colorRes: Int) = BitmapFactory.decodeResource(this.resources, colorRes)

/**
 * 获取字符串
 */
fun Context.getResString(res: Int) = resources.getString(res)

/**
 * 获取尺寸
 */
fun Context.getResDimension( @DimenRes resId: Int) = resources.getDimensionPixelSize(resId)

fun Context.getResDimensionFloat( @DimenRes resId: Int) = resources.getDimension(resId)

/**
 * 获取bitmap
 */
fun Context.getBitmapFromRes(res: Int) = BitmapFactory.decodeResource(resources, res)

/**
 * 将突变变成bitmap并设置大小
 */
fun Context?.getBitmapFromRes(@DrawableRes res: Int, newWidth: Int, newHeight: Int): Bitmap? {
    if (this == null)return null
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
fun Context.dp2px(dpValue: Float): Int =(dpValue * getDensity() ).toInt()

/**
 * px转dp
 */
fun Context.px2dp(pxValue: Float): Int = (pxValue / getDensity()).toInt()

/**
 * sp 转px
 */
fun Context.sp2px(spValue: Float)= (spValue * getScaledDensity()).toInt()

/**
 * px转sp
 */
fun Context.px2sp( pxValue: Float)= (pxValue / getScaledDensity() ).toInt()

/**
 * 获取屏幕
 */
fun Context.getScreenHeight(): Int  = resources.displayMetrics.heightPixels

/**
 * 获取屏幕宽度
 */
fun Context.getScreenWidth(): Int = resources.displayMetrics.widthPixels



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
fun Context.getSpace(): View = getSpace(R.dimen.dp_20)

fun Context.getSpace(@DimenRes heightRes: Int): View {
    val space = Space(this)
    val params: ViewGroup.LayoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResDimension(heightRes))
    space.layoutParams = params
    return space
}


fun Context?.copy(text: String) {
    if (this == null)return
    val mClipData = ClipData.newPlainText("xy", text)
    (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(mClipData)
}


fun Context?.isFullScreen(): Boolean  {
    if (this == null)return true
    return resources.configuration.orientation != Configuration.ORIENTATION_PORTRAIT
}

/**
 * 获取 targetSdk 版本码
 */
fun Context?.getTargetSdkVersionCode(): Int {
    return this?.applicationInfo?.targetSdkVersion?:0
}

/**
 * 显示toast
 */
fun Context.showToast(title: String?) {
    if (title.isNullOrEmpty())
        return
    runMain({
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
    })
}



