package com.xy.base.permission.impl

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.xy.base.permission.Permission
import com.xy.base.permission.PermissionIntentManager.getApplicationDetailsIntent
import com.xy.base.permission.PermissionUtils.areActivityIntent
import com.xy.base.permission.PermissionUtils.checkSelfPermission
import com.xy.base.permission.PermissionUtils.getPackageNameUri
import com.xy.base.permission.PermissionUtils.isSpecialPermission
import com.xy.base.permission.PermissionUtils.shouldShowRequestPermissionRationale
import com.xy.base.utils.AndroidVersion
import com.xy.base.utils.AndroidVersion.isAndroid10
import com.xy.base.utils.AndroidVersion.isAndroid11
import com.xy.base.utils.AndroidVersion.isAndroid12
import com.xy.base.utils.AndroidVersion.isAndroid13
import com.xy.base.utils.AndroidVersion.isAndroid6
import com.xy.base.utils.AndroidVersion.isAndroid8
import com.xy.base.utils.AndroidVersion.isAndroid9
import com.xy.base.utils.PhoneRomUtils

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/06/11
 * desc   : Android 6.0 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_6)
open class PermissionDelegateImplV23 : PermissionDelegateImplV21() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        // 向下兼容 Android 13 新权限
        if (!isAndroid13()) {
            if (TextUtils.equals(permission, Permission.POST_NOTIFICATIONS)) return super.isGrantedPermission(context, permission)
            if (TextUtils.equals(permission, Permission.NEARBY_WIFI_DEVICES)) return checkSelfPermission(context, Permission.ACCESS_FINE_LOCATION)
            if (TextUtils.equals(permission, Permission.BODY_SENSORS_BACKGROUND)) return checkSelfPermission(context, Permission.BODY_SENSORS)
            if (TextUtils.equals(permission, Permission.READ_MEDIA_IMAGES) ||
                TextUtils.equals(permission, Permission.READ_MEDIA_VIDEO) ||
                TextUtils.equals(permission, Permission.READ_MEDIA_AUDIO)) {
                return checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE)
            }
        }

        // 向下兼容 Android 12 新权限
        if (!isAndroid12()) {
            if (TextUtils.equals(permission, Permission.BLUETOOTH_SCAN)) return checkSelfPermission(context, Permission.ACCESS_FINE_LOCATION)
            if (TextUtils.equals(permission, Permission.BLUETOOTH_CONNECT) ||
                TextUtils.equals(permission, Permission.BLUETOOTH_ADVERTISE)) {
                return true
            }
        }

        // 向下兼容 Android 11 新权限
        if (!isAndroid11() && TextUtils.equals(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
            return checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE) &&
                    checkSelfPermission(context, Permission.WRITE_EXTERNAL_STORAGE)
        }

        // 向下兼容 Android 10 新权限
        if (!isAndroid10()) {
            if (TextUtils.equals(permission, Permission.ACCESS_BACKGROUND_LOCATION)) return checkSelfPermission(context, Permission.ACCESS_FINE_LOCATION)
            if (TextUtils.equals(permission, Permission.ACTIVITY_RECOGNITION)) return true
            if (TextUtils.equals(permission, Permission.ACCESS_MEDIA_LOCATION)) return checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE)
        }

        // 向下兼容 Android 9.0 新权限
        if (!isAndroid9() && TextUtils.equals(permission, Permission.ACCEPT_HANDOVER)) return true

        // 向下兼容 Android 8.0 新权限
        if (!isAndroid8()) {
            if (TextUtils.equals(permission, Permission.ANSWER_PHONE_CALLS)) return true
            if (TextUtils.equals(permission, Permission.READ_PHONE_NUMBERS)) return checkSelfPermission(context, Permission.READ_PHONE_STATE)
        }

        // 交给父类处理
        if (TextUtils.equals(permission, Permission.GET_INSTALLED_APPS) ||
            TextUtils.equals(permission, Permission.POST_NOTIFICATIONS)) {
            return super.isGrantedPermission(context, permission)
        }
        if (isSpecialPermission(permission)) {
            // 检测系统权限
            if (TextUtils.equals(permission, Permission.WRITE_SETTINGS)) return isGrantedSettingPermission(context)

            // 检测勿扰权限
            if (TextUtils.equals(permission, Permission.ACCESS_NOTIFICATION_POLICY)) return isGrantedNotDisturbPermission(context)

            // 检测电池优化选项权限
            return if (TextUtils.equals(permission, Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) {
                isGrantedIgnoreBatteryPermission(context)
            } else super.isGrantedPermission(context, permission)
        }
        return checkSelfPermission(context, permission)
    }

    override fun isPermissionPermanentDenied(activity: FragmentActivity, permission: String): Boolean {
        // 向下兼容 Android 13 新权限
        if (!isAndroid13()) {
            if (TextUtils.equals(permission, Permission.POST_NOTIFICATIONS)) return super.isPermissionPermanentDenied(activity, permission)
            if (TextUtils.equals(permission, Permission.NEARBY_WIFI_DEVICES))
                return !checkSelfPermission(activity, Permission.ACCESS_FINE_LOCATION) &&
                        !shouldShowRequestPermissionRationale(activity,
                            Permission.ACCESS_FINE_LOCATION)
            if (TextUtils.equals(permission, Permission.BODY_SENSORS_BACKGROUND))
                return !checkSelfPermission(activity, Permission.BODY_SENSORS) &&
                        !shouldShowRequestPermissionRationale(activity, Permission.BODY_SENSORS)
            if (TextUtils.equals(permission, Permission.READ_MEDIA_IMAGES) ||
                TextUtils.equals(permission, Permission.READ_MEDIA_VIDEO) ||
                TextUtils.equals(permission, Permission.READ_MEDIA_AUDIO))
                return !checkSelfPermission(activity, Permission.READ_EXTERNAL_STORAGE) &&
                        !shouldShowRequestPermissionRationale(activity,
                            Permission.READ_EXTERNAL_STORAGE)
        }

        // 向下兼容 Android 12 新权限
        if (!isAndroid12()) {
            if (TextUtils.equals(permission, Permission.BLUETOOTH_SCAN))
                return !checkSelfPermission(activity, Permission.ACCESS_FINE_LOCATION) &&
                        !shouldShowRequestPermissionRationale(activity,
                            Permission.ACCESS_FINE_LOCATION)
            if (TextUtils.equals(permission, Permission.BLUETOOTH_CONNECT) ||
                TextUtils.equals(permission, Permission.BLUETOOTH_ADVERTISE))
                return false
        }

        // 向下兼容 Android 10 新权限
        if (!isAndroid10()) {
            if (TextUtils.equals(permission, Permission.ACCESS_BACKGROUND_LOCATION))
                return !checkSelfPermission(activity, Permission.ACCESS_FINE_LOCATION) &&
                        !shouldShowRequestPermissionRationale(activity,
                            Permission.ACCESS_FINE_LOCATION)
            if (TextUtils.equals(permission, Permission.ACTIVITY_RECOGNITION)) return false
            if (TextUtils.equals(permission, Permission.ACCESS_MEDIA_LOCATION))
                return !checkSelfPermission(activity, Permission.READ_EXTERNAL_STORAGE) &&
                        !shouldShowRequestPermissionRationale(activity,
                            Permission.READ_EXTERNAL_STORAGE)
        }

        // 向下兼容 Android 9.0 新权限
        if (!isAndroid9() && TextUtils.equals(permission, Permission.ACCEPT_HANDOVER)) return false

        // 向下兼容 Android 8.0 新权限
        if (!isAndroid8()) {
            if (TextUtils.equals(permission, Permission.ANSWER_PHONE_CALLS)) return false
            if (TextUtils.equals(permission, Permission.READ_PHONE_NUMBERS)) {
                return !checkSelfPermission(activity, Permission.READ_PHONE_STATE) &&
                        !shouldShowRequestPermissionRationale(activity, Permission.READ_PHONE_STATE)
            }
        }

        // 交给父类处理
        if (TextUtils.equals(permission, Permission.GET_INSTALLED_APPS) ||
            TextUtils.equals(permission, Permission.POST_NOTIFICATIONS)) {
            return super.isPermissionPermanentDenied(activity, permission)
        }
        return if (isSpecialPermission(permission)) false
        else !checkSelfPermission(activity, permission) &&
                !shouldShowRequestPermissionRationale(activity, permission)
    }

    override fun getPermissionIntent(context: Context, permission: String): Intent? {
        if (TextUtils.equals(permission, Permission.WRITE_SETTINGS)) return getSettingPermissionIntent(context)
        if (TextUtils.equals(permission, Permission.ACCESS_NOTIFICATION_POLICY)) return getNotDisturbPermissionIntent(context)
        return if (TextUtils.equals(permission, Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)) {
            getIgnoreBatteryPermissionIntent(context)
        } else super.getPermissionIntent(context, permission)
    }




    /**
     * 是否有系统设置权限
     */
    private fun isGrantedSettingPermission(context: Context): Boolean = if (isAndroid6()) Settings.System.canWrite(context) else true

    /**
     * 获取系统设置权限界面意图
     */
    private fun getSettingPermissionIntent(context: Context): Intent? {
        var intent: Intent? = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent?.data = getPackageNameUri(context)
        if (!areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }

    /**
     * 是否有勿扰模式权限
     */
    private fun isGrantedNotDisturbPermission(context: Context): Boolean = context.getSystemService(NotificationManager::class.java).isNotificationPolicyAccessGranted

    /**
     * 获取勿扰模式设置界面意图
     */
    private fun getNotDisturbPermissionIntent(context: Context): Intent? {
        var intent: Intent? = null
        if (isAndroid10() && !PhoneRomUtils.isHarmonyOs()) {
            // android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS
            intent = Intent("android.settings.NOTIFICATION_POLICY_ACCESS_DETAIL_SETTINGS")
            intent.data = getPackageNameUri(context)
        }
        if (!areActivityIntent(context, intent)) intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        if (!areActivityIntent(context, intent)) intent = getApplicationDetailsIntent(context)
        return intent
    }

    /**
     * 是否忽略电池优化选项
     */
    private fun isGrantedIgnoreBatteryPermission(context: Context): Boolean =
        context.getSystemService(PowerManager::class.java).isIgnoringBatteryOptimizations(context.packageName)

    /**
     * 获取电池优化选项设置界面意图
     */
    private fun getIgnoreBatteryPermissionIntent(context: Context): Intent? {
        var intent: Intent? = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent!!.data = getPackageNameUri(context)
        if (!areActivityIntent(context, intent)) {
            intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        }
        if (!areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }
}