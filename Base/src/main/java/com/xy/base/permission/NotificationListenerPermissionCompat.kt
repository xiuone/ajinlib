package com.xy.base.permission

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import com.xy.base.permission.PermissionUtils.areActivityIntent
import com.xy.base.permission.PermissionUtils.getAndroidManifestInfo
import com.xy.base.utils.AndroidVersion

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2023/03/12
 * desc   : 通知栏监听权限兼容类
 */
internal object NotificationListenerPermissionCompat {
    /** Settings.Secure.ENABLED_NOTIFICATION_LISTENERS  */
    private const val SETTING_ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"

    fun isGrantedPermission(context: Context): Boolean {
        // 经过实践得出，通知监听权限是在 Android 4.3 才出现的，所以前面的版本统一返回 true
        if (!AndroidVersion.isAndroid4_3()) return true
        val enabledNotificationListeners = Settings.Secure.getString(context.contentResolver, SETTING_ENABLED_NOTIFICATION_LISTENERS)
        if (TextUtils.isEmpty(enabledNotificationListeners)) {
            return false
        }
        val components = enabledNotificationListeners.split(":").toTypedArray()
        for (component in components) {
            val componentName = ComponentName.unflattenFromString(component)
            if (!TextUtils.equals(componentName!!.packageName, context.packageName)) {
                continue
            }
            val className = componentName.className
            try {
                Class.forName(className)
                return true
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        return false
    }

    fun getPermissionIntent(context: Context): Intent? {
        var intent: Intent? = null
        if (AndroidVersion.isAndroid11()) {
            val androidManifestInfo = getAndroidManifestInfo(context)
            var serviceInfo: AndroidManifestInfo.ServiceInfo? = null
            if (androidManifestInfo != null) {
                for (info in androidManifestInfo.serviceInfoList) {
                    if (!TextUtils.equals(info.permission, Permission.BIND_NOTIFICATION_LISTENER_SERVICE)) {
                        continue
                    }
                    if (serviceInfo != null) {
                        serviceInfo = null
                        break
                    }
                    serviceInfo = info
                }
            }
            if (serviceInfo != null) {
                intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)
                intent.putExtra(Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME, ComponentName(context, serviceInfo.name!!).flattenToString())
                if (!areActivityIntent(context, intent)) {
                    intent = null
                }
            }
        }
        if (intent == null) {
            intent = if (AndroidVersion.isAndroid5_1()) Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            else Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
        }
        if (!areActivityIntent(context, intent)) {
            intent = PermissionIntentManager.getApplicationDetailsIntent(context)
        }
        return intent
    }
}