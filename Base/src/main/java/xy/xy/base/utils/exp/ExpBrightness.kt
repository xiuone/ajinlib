package xy.xy.base.utils.exp

import android.app.Activity
import android.content.ContentResolver
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.util.Log

/**
 * 调节屏幕亮度工具类
 * Created by v_liujialu01 on 2020/3/13.
 */
private const val TAG = "BrightnessUtils"

/**
 * 判断是否开启了自动亮度调节
 */
fun Activity.isAutoBrightness(aContentResolver: ContentResolver?): Boolean {
    try {
        return  Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
    } catch (e: SettingNotFoundException) {
        e.printStackTrace()
    }
    return false
}

/**
 * 获取屏幕的亮度
 */
fun Activity.getScreenBrightness(): Int {
    try {
        return Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return 0
}

/**
 * 设置亮度
 */
fun Activity.setBrightness(brightness: Int) {
    val lp = window.attributes
    lp.screenBrightness = java.lang.Float.valueOf(brightness.toFloat()) * (1f / 255f)
    Log.d(TAG, "set  lp.screenBrightness == " + lp.screenBrightness)
    window.attributes = lp
}

/**
 * 停止自动亮度调节
 */
fun Activity.stopAutoBrightness() {
    Settings.System.putInt(contentResolver,
        Settings.System.SCREEN_BRIGHTNESS_MODE,
        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
}

/**
 * 开启亮度自动调节
 */
fun Activity.startAutoBrightness() {
    Settings.System.putInt(contentResolver,
        Settings.System.SCREEN_BRIGHTNESS_MODE,
        Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
}

/**
 * 保存亮度设置状态
 */
fun Activity.saveBrightness(brightness: Int) {
    val uri = Settings.System.getUriFor("screen_brightness")
    Settings.System.putInt(contentResolver, "screen_brightness", brightness)
    contentResolver.notifyChange(uri, null)
}