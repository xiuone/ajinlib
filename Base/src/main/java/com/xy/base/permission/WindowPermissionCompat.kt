package com.xy.base.permission

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.xy.base.permission.PermissionIntentManager.getApplicationDetailsIntent
import com.xy.base.permission.PermissionIntentManager.getColorOsWindowPermissionPageIntent
import com.xy.base.permission.PermissionIntentManager.getEmuiWindowPermissionPageIntent
import com.xy.base.permission.PermissionIntentManager.getMiuiPermissionPageIntent
import com.xy.base.permission.PermissionIntentManager.getMiuiWindowPermissionPageIntent
import com.xy.base.permission.PermissionIntentManager.getOneUiWindowPermissionPageIntent
import com.xy.base.permission.PermissionIntentManager.getOriginOsWindowPermissionPageIntent
import com.xy.base.permission.PermissionUtils.areActivityIntent
import com.xy.base.permission.PermissionUtils.checkOpNoThrow
import com.xy.base.permission.PermissionUtils.getPackageNameUri
import com.xy.base.permission.StartActivityManager.addSubIntentToMainIntent
import com.xy.base.utils.AndroidVersion
import com.xy.base.utils.PhoneRomUtils.isColorOs
import com.xy.base.utils.PhoneRomUtils.isEmui
import com.xy.base.utils.PhoneRomUtils.isMiui
import com.xy.base.utils.PhoneRomUtils.isMiuiOptimization
import com.xy.base.utils.PhoneRomUtils.isOneUi
import com.xy.base.utils.PhoneRomUtils.isOriginOs

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2023/03/11
 * desc   : 悬浮窗权限兼容类
 */
object WindowPermissionCompat {
    private const val OP_SYSTEM_ALERT_WINDOW_FIELD_NAME = "OP_SYSTEM_ALERT_WINDOW"
    private const val OP_SYSTEM_ALERT_WINDOW_DEFAULT_VALUE = 24
    fun isGrantedPermission(context: Context): Boolean {
        if (AndroidVersion.isAndroid6()) {
            return Settings.canDrawOverlays(context)
        }
        return if (AndroidVersion.isAndroid4_4()) {
            // 经过测试在 vivo x7 Plus（Android 5.1）和 OPPO A53 （Android 5.1 ColorOs 2.1）的机子上面判断不准确
            // 经过 debug 发现并不是 vivo 和 oppo 修改了 OP_SYSTEM_ALERT_WINDOW 的赋值导致的
            // 估计是 vivo 和 oppo 的机子修改了整个悬浮窗机制，这种就没有办法了
            checkOpNoThrow(context,
                OP_SYSTEM_ALERT_WINDOW_FIELD_NAME,
                OP_SYSTEM_ALERT_WINDOW_DEFAULT_VALUE)
        } else true
    }

    fun getPermissionIntent(context: Context): Intent? {
        if (AndroidVersion.isAndroid6()) {
            if (AndroidVersion.isAndroid11() && isMiui() && isMiuiOptimization()) {
                // 因为 Android 11 及后面的版本无法直接跳转到具体权限设置页面，只能跳转到悬浮窗权限应用列表，十分地麻烦的，这里做了一下简化
                // miui 做得比较人性化的，不会出现跳转不过去的问题，其他厂商就不一定了，就是不想让你跳转过去
                var intent = getMiuiPermissionPageIntent(context)
                // 另外跳转到应用详情页也可以开启悬浮窗权限
                intent = addSubIntentToMainIntent(intent, getApplicationDetailsIntent(context))
                return intent
            }
            var intent: Intent? = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            // 在 Android 11 加包名跳转也是没有效果的，官方文档链接：
            // https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
            intent!!.data = getPackageNameUri(context)
            if (areActivityIntent(context, intent)) {
                return intent
            }
            intent = getApplicationDetailsIntent(context)
            return intent
        }

        // 需要注意的是，这里不需要判断鸿蒙，因为鸿蒙 2.0 用代码判断是 API 等级是 29（Android 10）会直接走上面的逻辑，而不会走到下面来
        if (isEmui()) {
            var intent = getEmuiWindowPermissionPageIntent(context)
            intent = addSubIntentToMainIntent(intent, getApplicationDetailsIntent(context))
            return intent
        }
        if (isMiui()) {
            var intent: Intent? = null
            if (isMiuiOptimization()) {
                // 假设关闭了 miui 优化，就不走这里的逻辑
                intent = getMiuiWindowPermissionPageIntent(context)
            }

            // 小米手机也可以通过应用详情页开启悬浮窗权限（只不过会多一步操作）
            intent = addSubIntentToMainIntent(intent, getApplicationDetailsIntent(context))
            return intent
        }
        if (isColorOs()) {
            var intent = getColorOsWindowPermissionPageIntent(context)
            intent = addSubIntentToMainIntent(intent, getApplicationDetailsIntent(context))
            return intent
        }
        if (isOriginOs()) {
            var intent = getOriginOsWindowPermissionPageIntent(context)
            intent = addSubIntentToMainIntent(intent, getApplicationDetailsIntent(context))
            return intent
        }
        if (isOneUi()) {
            var intent = getOneUiWindowPermissionPageIntent(context)
            intent = addSubIntentToMainIntent(intent, getApplicationDetailsIntent(context))
            return intent
        }

        // 360 第一部发布的手机是 360 N4，Android 版本是 6.0 了，所以根本不需要跳转到指定的页面开启悬浮窗权限
        // 经过测试，锤子手机 6.0 以下手机的可以直接通过直接跳转到应用详情开启悬浮窗权限
        // 经过测试，魅族手机 6.0 可以直接通过直接跳转到应用详情开启悬浮窗权限
        return getApplicationDetailsIntent(context)
    }
}