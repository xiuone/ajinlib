package com.xy.base.permission

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.Nullable
import com.xy.base.permission.PermissionUtils.areActivityIntent
import com.xy.base.permission.PermissionUtils.getPackageNameUri
import com.xy.base.utils.PhoneRomUtils

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2023/03/12
 * desc   : 国内手机厂商权限设置页管理器
 */
internal object PermissionIntentManager {
    /** 华为手机管家 App 包名  */
    private const val EMUI_MOBILE_MANAGER_APP_PACKAGE_NAME = "com.huawei.systemmanager"

    /** 小米手机管家 App 包名  */
    private const val MIUI_MOBILE_MANAGER_APP_PACKAGE_NAME = "com.miui.securitycenter"

    /** OPPO 安全中心 App 包名  */
    private const val COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_1 = "com.oppo.safe"
    private const val COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_2 = "com.color.safecenter"
    private const val COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_3 = "com.oplus.safecenter"

    /** vivo 安全中心 App 包名  */
    private const val ORIGIN_OS_MOBILE_MANAGER_APP_PACKAGE_NAME = "com.iqoo.secure"

    /**
     * 获取华为悬浮窗权限设置意图
     */
    @Nullable
    fun getEmuiWindowPermissionPageIntent(context: Context): Intent? {
        // EMUI 发展史：http://www.360doc.com/content/19/1017/10/9113704_867381705.shtml
        // android 华为版本历史,一文看完华为EMUI发展史：https://blog.csdn.net/weixin_39959369/article/details/117351161
        val addViewMonitorActivityIntent = Intent()
        // emui 3.1 的适配（华为荣耀 7 Android 5.0、华为揽阅 M2 青春版 Android 5.1、华为畅享 5S Android 5.1）
        addViewMonitorActivityIntent.setClassName(EMUI_MOBILE_MANAGER_APP_PACKAGE_NAME, "$EMUI_MOBILE_MANAGER_APP_PACKAGE_NAME.addviewmonitor.AddViewMonitorActivity")
        val notificationManagementActivityIntent = Intent()
        // emui 3.0 的适配（华为麦芒 3S Android 4.4）
        notificationManagementActivityIntent.setClassName(EMUI_MOBILE_MANAGER_APP_PACKAGE_NAME, "com.huawei.notificationmanager.ui.NotificationManagmentActivity")
        // 华为手机管家主页
        val huaWeiMobileManagerAppIntent = getHuaWeiMobileManagerAppIntent(context)

        // 获取厂商版本号
        var romVersionName: String = PhoneRomUtils.romVersionName()
        var intent: Intent? = null
        if (romVersionName.startsWith("3.0")) {
            // 3.0、3.0.1
            if (areActivityIntent(context, notificationManagementActivityIntent)) intent = notificationManagementActivityIntent
            if (areActivityIntent(context, addViewMonitorActivityIntent)) intent = StartActivityManager.addSubIntentToMainIntent(intent, addViewMonitorActivityIntent)
        } else {
            // 3.1、其他的
            if (areActivityIntent(context, addViewMonitorActivityIntent)) intent = addViewMonitorActivityIntent
            if (areActivityIntent(context, notificationManagementActivityIntent)) intent = StartActivityManager.addSubIntentToMainIntent(intent, notificationManagementActivityIntent)
        }
        if (areActivityIntent(context, huaWeiMobileManagerAppIntent)) intent = StartActivityManager.addSubIntentToMainIntent(intent, huaWeiMobileManagerAppIntent)
        return intent
    }

    /**
     * 获取小米悬浮窗权限设置意图
     */
    @Nullable
    fun getMiuiWindowPermissionPageIntent(context: Context): Intent? =getMiuiPermissionPageIntent(context)

    /**
     * 获取 oppo 悬浮窗权限设置意图
     */
    @Nullable
    fun getColorOsWindowPermissionPageIntent(context: Context): Intent? {
        val permissionTopActivityActionIntent = Intent("com.oppo.safe.permission.PermissionTopActivity")
        val oppoSafeCenterAppIntent = getOppoSafeCenterAppIntent(context)
        var intent: Intent? = null
        if (areActivityIntent(context, permissionTopActivityActionIntent)) intent = permissionTopActivityActionIntent
        if (areActivityIntent(context, oppoSafeCenterAppIntent)) intent = StartActivityManager.addSubIntentToMainIntent(intent, oppoSafeCenterAppIntent)
        return intent
    }

    /**
     * 获取 vivo 悬浮窗权限设置意图
     */
    @Nullable
    fun getOriginOsWindowPermissionPageIntent(context: Context): Intent? {
        val intent = getVivoMobileManagerAppIntent(context)
        return if (areActivityIntent(context, intent)) intent else null
    }

    @Nullable
    fun getOneUiWindowPermissionPageIntent(context: Context): Intent? = getOneUiPermissionPageIntent(context)

    /* ---------------------------------------------------------------------------------------- */
    @Nullable
    fun getMiuiPermissionPageIntent(context: Context): Intent? {
        val appPermEditorActionIntent = Intent()
            .setAction("miui.intent.action.APP_PERM_EDITOR")
            .putExtra("extra_pkgname", context.packageName)
        val xiaoMiMobileManagerAppIntent = getXiaoMiMobileManagerAppIntent(context)
        var intent: Intent? = null
        if (areActivityIntent(context, appPermEditorActionIntent)) intent = appPermEditorActionIntent
        if (areActivityIntent(context, xiaoMiMobileManagerAppIntent)) intent = StartActivityManager.addSubIntentToMainIntent(intent, xiaoMiMobileManagerAppIntent)
        return intent
    }

    @Nullable
    fun getOriginOsPermissionPageIntent(context: Context): Intent? {
        // vivo iQOO 9 Pro（OriginOs 2.0 Android 12）
        val intent = Intent("permission.intent.action.softPermissionDetail")
        intent.putExtra("packagename", context.packageName)
        return if (areActivityIntent(context, intent)) intent else null
    }

    /**
     * 获取三星权限设置意图
     */
    @Nullable
    fun getOneUiPermissionPageIntent(context: Context): Intent? {
        val intent = Intent()
        intent.setClassName("com.android.settings",
            "com.android.settings.Settings\$AppOpsDetailsActivity")
        val extraShowFragmentArguments = Bundle()
        extraShowFragmentArguments.putString("package", context.packageName)
        intent.putExtra(":settings:show_fragment_args", extraShowFragmentArguments)
        intent.data = getPackageNameUri(context)
        return if (areActivityIntent(context, intent)) intent else null
    }
    /* ---------------------------------------------------------------------------------------- */
    /**
     * 返回华为手机管家 App 意图
     */
    @Nullable
    fun getHuaWeiMobileManagerAppIntent(context: Context): Intent? {
        val intent = context.packageManager.getLaunchIntentForPackage(EMUI_MOBILE_MANAGER_APP_PACKAGE_NAME)
        return if (areActivityIntent(context, intent)) intent else null
    }

    /**
     * 返回小米手机管家 App 意图
     */
    @Nullable
    fun getXiaoMiMobileManagerAppIntent(context: Context): Intent? {
        val intent = context.packageManager.getLaunchIntentForPackage(MIUI_MOBILE_MANAGER_APP_PACKAGE_NAME)
        return if (areActivityIntent(context, intent)) intent else null
    }

    /**
     * 获取 oppo 安全中心 App 意图
     */
    @Nullable
    fun getOppoSafeCenterAppIntent(context: Context): Intent? {
        var intent = context.packageManager.getLaunchIntentForPackage(COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_1)
        if (areActivityIntent(context, intent)) return intent
        intent = context.packageManager.getLaunchIntentForPackage(COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_2)
        if (areActivityIntent(context, intent)) return intent
        intent = context.packageManager.getLaunchIntentForPackage(COLOR_OS_SAFE_CENTER_APP_PACKAGE_NAME_3)
        return if (areActivityIntent(context, intent)) intent else null
    }

    /**
     * 获取 vivo 管家手机意图
     */
    @Nullable
    fun getVivoMobileManagerAppIntent(context: Context): Intent? {
        val intent = context.packageManager.getLaunchIntentForPackage(ORIGIN_OS_MOBILE_MANAGER_APP_PACKAGE_NAME)
        return if (areActivityIntent(context, intent)) intent else null
    }
    /* ---------------------------------------------------------------------------------------- */
    /**
     * 获取应用详情界面意图
     */
    fun getApplicationDetailsIntent(context: Context): Intent? {
        var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = getPackageNameUri(context)
        if (areActivityIntent(context, intent)) return intent
        intent = Intent(Settings.ACTION_APPLICATION_SETTINGS)
        if (areActivityIntent(context, intent)) return intent
        intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
        return if (areActivityIntent(context, intent)) intent else getAndroidSettingAppIntent(context)
    }

    /** 跳转到系统设置页面  */
    @Nullable
    fun getAndroidSettingAppIntent(context: Context): Intent? {
        val intent = Intent(Settings.ACTION_SETTINGS)
        return if (areActivityIntent(context, intent))  intent  else null
    }
}