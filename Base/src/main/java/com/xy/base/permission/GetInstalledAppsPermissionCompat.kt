package com.xy.base.permission

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.xy.base.permission.PermissionUtils.checkOpNoThrow
import com.xy.base.permission.PermissionUtils.checkSelfPermission
import com.xy.base.permission.PermissionUtils.shouldShowRequestPermissionRationale
import com.xy.base.utils.AndroidVersion
import com.xy.base.utils.PhoneRomUtils.isMiui
import com.xy.base.utils.PhoneRomUtils.isMiuiOptimization

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2023/03/12
 * desc   : 读取应用列表权限兼容类
 */
internal object GetInstalledAppsPermissionCompat {
    private const val MIUI_OP_GET_INSTALLED_APPS_FIELD_NAME = "OP_GET_INSTALLED_APPS"
    private const val MIUI_OP_GET_INSTALLED_APPS_DEFAULT_VALUE = 10022

    fun isGrantedPermission(context: Context): Boolean {
        if (!AndroidVersion.isAndroid4_4()) {
            return true
        }
        if (AndroidVersion.isAndroid6() && isSupportGetInstalledAppsPermission(context)) {
            return checkSelfPermission(context, Permission.GET_INSTALLED_APPS)
        }
        return if (isMiui() && isMiuiSupportGetInstalledAppsPermission()) {
            if (!isMiuiOptimization()) true
            else checkOpNoThrow(context, MIUI_OP_GET_INSTALLED_APPS_FIELD_NAME, MIUI_OP_GET_INSTALLED_APPS_DEFAULT_VALUE)
        } else true
    }

    fun isPermissionPermanentDenied(activity: Activity): Boolean {
        if (!AndroidVersion.isAndroid4_4()) return false
        if (AndroidVersion.isAndroid6() && isSupportGetInstalledAppsPermission(activity)) {
            return !checkSelfPermission(activity, Permission.GET_INSTALLED_APPS) &&
                    !shouldShowRequestPermissionRationale(activity, Permission.GET_INSTALLED_APPS)
        }
        return if (isMiui() && isMiuiSupportGetInstalledAppsPermission()) {
            if (!isMiuiOptimization()) false
            else !isGrantedPermission(activity)
        } else false
    }

    fun getPermissionIntent(context: Context): Intent? {
        if (isMiui()) {
            var intent: Intent? = null
            if (isMiuiOptimization()) {
                intent = PermissionIntentManager.getMiuiPermissionPageIntent(context)
            }
            intent = StartActivityManager.addSubIntentToMainIntent(intent, PermissionIntentManager.getApplicationDetailsIntent(context))
            return intent
        }
        return PermissionIntentManager.getApplicationDetailsIntent(context)
    }

    /**
     * 判断是否支持获取应用列表权限
     */
    @RequiresApi(api = AndroidVersion.ANDROID_6)
    private fun isSupportGetInstalledAppsPermission(context: Context): Boolean {
        try {
            val permissionInfo =
                context.packageManager.getPermissionInfo(Permission.GET_INSTALLED_APPS, 0)
            if (permissionInfo != null) {
                return if (AndroidVersion.isAndroid9()) {
                    permissionInfo.protection == PermissionInfo.PROTECTION_DANGEROUS
                } else {
                    permissionInfo.protectionLevel and PermissionInfo.PROTECTION_MASK_BASE == PermissionInfo.PROTECTION_DANGEROUS
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        try {
            return Settings.Secure.getInt(context.contentResolver, "oem_installed_apps_runtime_permission_enable") == 1
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 判断当前 miui 版本是否支持申请读取应用列表权限
     */
    private fun isMiuiSupportGetInstalledAppsPermission(): Boolean{
            if (!AndroidVersion.isAndroid4_4()) {
                return true
            }
            try {
                val appOpsClass = Class.forName(AppOpsManager::class.java.name)
                appOpsClass.getDeclaredField(MIUI_OP_GET_INSTALLED_APPS_FIELD_NAME)
                // 证明有这个字段，返回 true
                return true
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            }
            return true
        }
}