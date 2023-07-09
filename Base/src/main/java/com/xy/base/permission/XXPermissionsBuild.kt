package com.xy.base.permission

import android.content.Context
import androidx.annotation.Nullable
import com.xy.base.BuildConfig
import com.xy.base.permission.PermissionApi.isGrantedPermissions
import com.xy.base.permission.PermissionChecker.checkActivityStatus
import com.xy.base.permission.PermissionChecker.checkBodySensorsPermission
import com.xy.base.permission.PermissionChecker.checkLocationPermission
import com.xy.base.permission.PermissionChecker.checkManifestPermissions
import com.xy.base.permission.PermissionChecker.checkMediaLocationPermission
import com.xy.base.permission.PermissionChecker.checkNearbyDevicesPermission
import com.xy.base.permission.PermissionChecker.checkNotificationListenerPermission
import com.xy.base.permission.PermissionChecker.checkPermissionArgument
import com.xy.base.permission.PermissionChecker.checkPictureInPicturePermission
import com.xy.base.permission.PermissionChecker.checkStoragePermission
import com.xy.base.permission.PermissionChecker.checkTargetSdkVersion
import com.xy.base.permission.PermissionChecker.optimizeDeprecatedPermission
import com.xy.base.permission.PermissionUtils.containsPermission
import com.xy.base.permission.PermissionUtils.findActivity
import com.xy.base.permission.PermissionUtils.getAndroidManifestInfo

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2018/06/15
 * desc   : Android 危险权限请求类
 */
class XXPermissionsBuild(private val mContext: Context?,private val permissionInterceptor: IPermissionInterceptor){

    /** 申请的权限列表  */
    private val mPermissions = ArrayList<String>()

    /**
     * 添加权限组
     */
    fun permission(vararg permissions: String): XXPermissionsBuild = permission(permissions.toList())

    fun permission(@Nullable permissions: List<String>): XXPermissionsBuild {
        if (permissions.isEmpty()) {
            return this
        }
        for (permission in permissions) {
            if (containsPermission(permission,* mPermissions.toTypedArray())) {
                continue
            }
            mPermissions.add(permission)
        }
        return this

    }

    /**
     * 请求权限
     */
    fun request(@Nullable callback: OnPermissionCallback?) {
        val context: Context = mContext?:return
        // 权限请求列表（为什么直接不用字段？因为框架要兼容新旧权限，在低版本下会自动添加旧权限申请，为了避免重复添加）
        val permissions = ArrayList(mPermissions)

        // 检查当前 Activity 状态是否是正常的，如果不是则不请求权限
        val activity = findActivity(context)?:return
        if (!checkActivityStatus(activity,  BuildConfig.DEBUG)) {
            return
        }
        // 必须要传入正常的权限或者权限组才能申请权限
        if (!checkPermissionArgument(permissions, BuildConfig.DEBUG)) {
            return
        }
        if (BuildConfig.DEBUG) {
            // 获取清单文件信息
            val androidManifestInfo = getAndroidManifestInfo(context)
            // 检查申请的读取媒体位置权限是否符合规范
            checkMediaLocationPermission(context, )
            // 检查申请的存储权限是否符合规范
            androidManifestInfo?.run {
                checkStoragePermission(context, androidManifestInfo,*permissions.toTypedArray())
                checkPictureInPicturePermission(activity,androidManifestInfo, *permissions.toTypedArray(), )
                checkNotificationListenerPermission(androidManifestInfo,*permissions.toTypedArray())
                checkNearbyDevicesPermission(androidManifestInfo,*permissions.toTypedArray())
            }
            // 检查申请的传感器权限是否符合规范
            checkBodySensorsPermission(*permissions.toTypedArray())
            // 检查申请的定位权限是否符合规范
            checkLocationPermission(*permissions.toTypedArray())
            // 检查申请的画中画权限是否符合规范
            // 检查申请的通知栏监听权限是否符合规范
            // 检查蓝牙和 WIFI 权限申请是否符合规范
            // 检查申请的权限和 targetSdk 版本是否能吻合
            checkTargetSdkVersion(context, *permissions.toTypedArray())
            // 检测权限有没有在清单文件中注册
            checkManifestPermissions(context, permissions, androidManifestInfo)
        }

        // 优化所申请的权限列表
        optimizeDeprecatedPermission(permissions)
        if (isGrantedPermissions(context, *permissions.toTypedArray())) {
            // 证明这些权限已经全部授予过，直接回调成功
            if (callback != null) {
                permissionInterceptor.grantedPermissionRequest(activity, permissions, permissions, true, callback)
                permissionInterceptor.finishPermissionRequest(activity, permissions, true, callback)
            }
            return
        }
        // 申请没有授予过的权限
        permissionInterceptor.launchPermissionRequest(activity, permissions, callback)
    }

}