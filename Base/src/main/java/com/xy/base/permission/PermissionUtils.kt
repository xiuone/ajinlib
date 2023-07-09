package com.xy.base.permission

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.Surface
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.xy.base.utils.AndroidVersion
import com.xy.base.utils.AndroidVersion.getTargetSdkVersionCode
import com.xy.base.utils.AndroidVersion.isAndroid10
import com.xy.base.utils.AndroidVersion.isAndroid11
import com.xy.base.utils.AndroidVersion.isAndroid12
import com.xy.base.utils.AndroidVersion.isAndroid13
import com.xy.base.utils.AndroidVersion.isAndroid8
import com.xy.base.utils.AndroidVersion.isAndroid9
import com.xy.base.utils.PhoneRomUtils.isEmui
import com.xy.base.utils.PhoneRomUtils.isHarmonyOs
import com.xy.base.utils.PhoneRomUtils.isMiui
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2018/06/15
 * desc   : 权限相关工具类
 */
object PermissionUtils {
    /** Handler 对象  */
    private val HANDLER = Handler(Looper.getMainLooper())

    /**
     * 判断某个权限是否是特殊权限
     */
    fun isSpecialPermission(permission: String): Boolean {
        return TextUtils.equals(permission, Permission.MANAGE_EXTERNAL_STORAGE) ||
                TextUtils.equals(permission, Permission.REQUEST_INSTALL_PACKAGES) ||
                TextUtils.equals(permission, Permission.SYSTEM_ALERT_WINDOW) ||
                TextUtils.equals(permission, Permission.WRITE_SETTINGS) ||
                TextUtils.equals(permission, Permission.NOTIFICATION_SERVICE) ||
                TextUtils.equals(permission, Permission.PACKAGE_USAGE_STATS) ||
                TextUtils.equals(permission, Permission.SCHEDULE_EXACT_ALARM) ||
                TextUtils.equals(permission, Permission.BIND_NOTIFICATION_LISTENER_SERVICE) ||
                TextUtils.equals(permission, Permission.ACCESS_NOTIFICATION_POLICY) ||
                TextUtils.equals(permission, Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) ||
                TextUtils.equals(permission, Permission.BIND_VPN_SERVICE) ||
                TextUtils.equals(permission, Permission.PICTURE_IN_PICTURE)
    }

    /**
     * 判断某个危险权限是否授予了
     */
    @RequiresApi(api = AndroidVersion.ANDROID_6)
    fun checkSelfPermission(context: Context, permission: String): Boolean = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(AndroidVersion.ANDROID_4_4)
    fun checkOpNoThrow(context: Context, opFieldName: String?, opDefaultValue: Int): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val appInfo = context.applicationInfo
        val pkg = context.applicationContext.packageName
        val uid = appInfo.uid
        return try {
            val appOpsClass = Class.forName(AppOpsManager::class.java.name)
            val opValue: Int = try {
                val opValueField = appOpsClass.getDeclaredField(opFieldName)
                opValueField[Int::class.java] as Int
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
                opDefaultValue
            }
            val checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String::class.java)
            (checkOpNoThrowMethod.invoke(appOps, opValue, uid, pkg) as Int == AppOpsManager.MODE_ALLOWED)
        } catch (e: ClassNotFoundException) {
            true
        } catch (e: NoSuchMethodException) {
            true
        } catch (e: InvocationTargetException) {
            true
        } catch (e: IllegalAccessException) {
            true
        } catch (e: RuntimeException) {
            true
        }
    }

    @RequiresApi(AndroidVersion.ANDROID_4_4)
    fun checkOpNoThrow(context: Context, opName: String): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode: Int = if (isAndroid10()) {
            appOps.unsafeCheckOpNoThrow(opName, context.applicationInfo.uid, context.packageName)
        } else {
            appOps.checkOpNoThrow(opName, context.applicationInfo.uid, context.packageName)
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    /**
     * 解决 Android 12 调用 shouldShowRequestPermissionRationale 出现内存泄漏的问题
     * Android 12L 和 Android 13 版本经过测试不会出现这个问题，证明 Google 在新版本上已经修复了这个问题
     * 但是对于 Android 12 仍是一个历史遗留问题，这是我们所有应用开发者不得不面对的一个事情
     *
     * issues 地址：https://github.com/getActivity/XXPermissions/issues/133
     */
    @RequiresApi(api = AndroidVersion.ANDROID_6)
    fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
        if (AndroidVersion.androidVersionCode() == AndroidVersion.ANDROID_12) {
            try {
                val packageManager = activity.application.packageManager
                val method = PackageManager::class.java.getMethod("shouldShowRequestPermissionRationale", String::class.java)
                return method.invoke(packageManager, permission) as Boolean
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        return activity.shouldShowRequestPermissionRationale(permission)
    }

    /**
     * 延迟一段时间执行 OnActivityResult，避免有些机型明明授权了，但还是回调失败的问题
     */
    fun postActivityResult(runnable: Runnable,vararg permissions: String) {
        var delayMillis : Long =  if (isAndroid11()) 200 else 300
        if (isEmui() || isHarmonyOs()) {
            delayMillis = if (isAndroid8()) 300 else 500
        } else if (isMiui()) {
            if (isAndroid11() && containsPermission(Permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,*permissions)) {
                delayMillis = 1000
            }
        }
        postDelayed(runnable, delayMillis)
    }

    /**
     * 延迟一段时间执行
     */
    fun postDelayed(runnable: Runnable, delayMillis: Long) = HANDLER.postDelayed(runnable, delayMillis)

    /**
     * 当前是否处于 debug 模式
     */
    fun isDebugMode(context: Context): Boolean = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

    @Nullable
    fun getAndroidManifestInfo(context: Context): AndroidManifestInfo? {
        val apkPathCookie = findApkPathCookie(context, context.applicationInfo.sourceDir)
        // 如果 cookie 为 0，证明获取失败
        if (apkPathCookie == 0) {
            return null
        }
        var androidManifestInfo: AndroidManifestInfo? = null
        try {
            androidManifestInfo = AndroidManifestParser.parseAndroidManifest(context, apkPathCookie)
            // 如果读取到的包名和当前应用的包名不是同一个的话，证明这个清单文件的内容不是当前应用的
            // 具体案例：https://github.com/getActivity/XXPermissions/issues/102
            if (!TextUtils.equals(context.packageName, androidManifestInfo.packageName)) {
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        }
        return androidManifestInfo
    }

    /**
     * 优化权限回调结果
     */
    fun optimizePermissionResults(
        activity: Activity?,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        for (i in permissions.indices) {
            var recheck = false
            val permission = permissions[i]

            // 如果这个权限是特殊权限，那么就重新进行权限检测
            if (PermissionApi.isSpecialPermission(permission)) {
                recheck = true
            }
            if (isAndroid13() && getTargetSdkVersionCode(activity!!) >= AndroidVersion.ANDROID_13 &&
                TextUtils.equals(permission, Permission.WRITE_EXTERNAL_STORAGE)) {
                // 在 Android 13 不能申请 WRITE_EXTERNAL_STORAGE，会被系统直接拒绝
                recheck = true
            }
            if (!isAndroid13() && (TextUtils.equals(permission, Permission.POST_NOTIFICATIONS) ||
                        TextUtils.equals(permission, Permission.NEARBY_WIFI_DEVICES) ||
                        TextUtils.equals(permission, Permission.BODY_SENSORS_BACKGROUND) ||
                        TextUtils.equals(permission, Permission.READ_MEDIA_IMAGES) ||
                        TextUtils.equals(permission, Permission.READ_MEDIA_VIDEO) ||
                        TextUtils.equals(permission, Permission.READ_MEDIA_AUDIO))) {
                recheck = true
            }

            // 重新检查 Android 12 的三个新权限
            if (!isAndroid12() && (TextUtils.equals(permission, Permission.BLUETOOTH_SCAN) ||
                        TextUtils.equals(permission, Permission.BLUETOOTH_CONNECT) ||
                        TextUtils.equals(permission, Permission.BLUETOOTH_ADVERTISE))) {
                recheck = true
            }

            // 重新检查 Android 10.0 的三个新权限
            if (!isAndroid10() && (TextUtils.equals(permission, Permission.ACCESS_BACKGROUND_LOCATION) ||
                        TextUtils.equals(permission, Permission.ACTIVITY_RECOGNITION) ||
                        TextUtils.equals(permission, Permission.ACCESS_MEDIA_LOCATION))) {
                recheck = true
            }

            // 重新检查 Android 9.0 的一个新权限
            if (!isAndroid9() && TextUtils.equals(permission, Permission.ACCEPT_HANDOVER)) {
                recheck = true
            }

            // 重新检查 Android 8.0 的两个新权限
            if (!isAndroid8() && (TextUtils.equals(permission, Permission.ANSWER_PHONE_CALLS) ||
                        TextUtils.equals(permission, Permission.READ_PHONE_NUMBERS))) {
                recheck = true
            }

            // 如果是读取应用列表权限（国产权限），则需要重新检查
            if (TextUtils.equals(permission, Permission.GET_INSTALLED_APPS)) {
                recheck = true
            }
            if (recheck) {
                grantResults[i] = if (PermissionApi.isGrantedPermission(activity!!, permission)) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
            }
        }
    }

    /**
     * 寻找上下文中的 Activity 对象
     */
    @Nullable
    fun findActivity(context: Context): FragmentActivity? {
        var context:Context? = context
        do {
            context = when (context) {
                is FragmentActivity -> return context
                is ContextWrapper -> context.baseContext
                else -> return null
            }
        } while (context != null)
        return null
    }

    /**
     * 获取当前应用 Apk 在 AssetManager 中的 Cookie，如果获取失败，则为 0
     */
    @SuppressLint("PrivateApi")
    fun findApkPathCookie(context: Context, apkPath: String): Int {
        val assets = context.assets
        var cookie: Int
        try {
            if (getTargetSdkVersionCode(context) >= AndroidVersion.ANDROID_9 &&
                AndroidVersion.androidVersionCode() >= AndroidVersion.ANDROID_9 &&
                AndroidVersion.androidVersionCode() < AndroidVersion.ANDROID_11) {
                // 反射套娃操作：实测这种方式只在 Android 9.0 和 Android 10.0 有效果，在 Android 11 上面就失效了
                val metaGetDeclaredMethod = Class::class.java.getDeclaredMethod(
                    "getDeclaredMethod", String::class.java, Array::class.java)
                metaGetDeclaredMethod.isAccessible = true
                // 注意 AssetManager.findCookieForPath 是 Android 9.0（API 28）的时候才添加的方法
                // 而 Android 9.0 用的是 AssetManager.addAssetPath 来获取 cookie
                // 具体可以参考 PackageParser.parseBaseApk 方法源码的实现
                val findCookieForPathMethod = metaGetDeclaredMethod.invoke(AssetManager::class.java,
                    "findCookieForPath", arrayOf<Class<*>>(String::class.java)) as Method
                findCookieForPathMethod.isAccessible = true
                cookie = findCookieForPathMethod.invoke(context.assets, apkPath) as Int
                return cookie
            }
            val addAssetPathMethod = assets.javaClass.getDeclaredMethod("addAssetPath", String::class.java)
            cookie = addAssetPathMethod.invoke(assets, apkPath) as Int
            return cookie
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

        // 获取失败直接返回 0
        // 为什么不直接返回 Integer，而是返回 int 类型？
        // 去看看 AssetManager.findCookieForPath 获取失败会返回什么就知道了
        return 0
    }

    /**
     * 判断是否适配了分区存储
     */
    fun isScopedStorage(context: Context): Boolean {
        try {
            val metaKey = "ScopedStorage"
            val metaData = context.packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA).metaData
            if (metaData != null && metaData.containsKey(metaKey)) {
                return java.lang.Boolean.parseBoolean(metaData[metaKey].toString())
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 锁定当前 Activity 的方向
     */
    @SuppressLint("SwitchIntDef")
    fun lockActivityOrientation(activity: Activity) {
        try {
            // 兼容问题：在 Android 8.0 的手机上可以固定 Activity 的方向，但是这个 Activity 不能是透明的，否则就会抛出异常
            // 复现场景：只需要给 Activity 主题设置 <item name="android:windowIsTranslucent">true</item> 属性即可
            when (activity.resources.configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> activity.requestedOrientation =
                    if (isActivityReverse(activity)) ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                Configuration.ORIENTATION_PORTRAIT -> activity.requestedOrientation =
                    if (isActivityReverse(activity)) ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                else -> {}
            }
        } catch (e: IllegalStateException) {
            // java.lang.IllegalStateException: Only fullscreen activities can request orientation
            e.printStackTrace()
        }
    }

    /**
     * 判断 Activity 是否反方向旋转了
     */
    fun isActivityReverse(activity: Activity): Boolean {
        // 获取 Activity 旋转的角度
        val activityRotation: Int = if (isAndroid11()) {
            activity.display!!.rotation
        } else {
            activity.windowManager.defaultDisplay.rotation
        }
        return when (activityRotation) {
            Surface.ROTATION_180, Surface.ROTATION_270 -> true
            Surface.ROTATION_0, Surface.ROTATION_90 -> false
            else -> false
        }
    }

    /**
     * 判断这个意图的 Activity 是否存在
     */
    fun areActivityIntent(context: Context, @Nullable intent: Intent?): Boolean {
        if (intent == null) {
            return false
        }
        // 这里为什么不用 Intent.resolveActivity(intent) != null 来判断呢？
        // 这是因为在 OPPO R7 Plus （Android 5.0）会出现误判，明明没有这个 Activity，却返回了 ComponentName 对象
        val packageManager = context.packageManager
        return if (isAndroid13()) {
            !packageManager.queryIntentActivities(intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()))
                .isEmpty()
        } else !packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .isEmpty()
    }

    /**
     * 根据传入的权限自动选择最合适的权限设置页
     *
     * @param permissions                 请求失败的权限
     */
    fun getSmartPermissionIntent(context: Context, vararg permissions: String): Intent? {
        // 如果失败的权限里面不包含特殊权限
        if (permissions == null || permissions.isEmpty()) {
            return PermissionIntentManager.getApplicationDetailsIntent(context)
        }

        // 危险权限统一处理
        if (!PermissionApi.containsSpecialPermission(*permissions)) {
            return if (permissions.size == 1) PermissionApi.getPermissionIntent(context, permissions[0]) 
            else PermissionIntentManager.getApplicationDetailsIntent(context)
        }
        when (permissions.size) {
            1 ->                 // 如果当前只有一个权限被拒绝了
                return PermissionApi.getPermissionIntent(context, permissions[0])
            2 -> if (!isAndroid13() &&
                containsPermission(Permission.NOTIFICATION_SERVICE,*permissions) &&
                containsPermission(Permission.POST_NOTIFICATIONS,*permissions)
            ) {
                return PermissionApi.getPermissionIntent(context, Permission.NOTIFICATION_SERVICE)
            }
            3 -> if (isAndroid11() &&
                containsPermission(Permission.MANAGE_EXTERNAL_STORAGE,*permissions, ) &&
                containsPermission(Permission.READ_EXTERNAL_STORAGE,*permissions ) &&
                containsPermission(Permission.WRITE_EXTERNAL_STORAGE,*permissions )) {
                return PermissionApi.getPermissionIntent(context,
                    Permission.MANAGE_EXTERNAL_STORAGE)
            }
            else -> {}
        }
        return PermissionIntentManager.getApplicationDetailsIntent(context)
    }
    
    /**
     * 判断权限集合中是否包含某个权限
     */
    fun containsPermission(permission: String,vararg permissions: String): Boolean = permissions.contains(permission)

    /**
     * 获取包名 uri
     */
    fun getPackageNameUri(context: Context): Uri = Uri.parse("package:" + context.packageName)
}