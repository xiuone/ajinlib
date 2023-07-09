package com.xy.base.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity
import com.xy.base.permission.impl.*
import com.xy.base.utils.AndroidVersion

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2021/12/31
 * desc   : 权限判断类
 */
object PermissionApi {
    private val DELEGATE by lazy { getDelegate() }


    private fun getDelegate():PermissionDelegate{
        if (AndroidVersion.isAndroid13()) {
            return PermissionDelegateImplV33()
        } else if (AndroidVersion.isAndroid12()) {
            return PermissionDelegateImplV31()
        } else if (AndroidVersion.isAndroid11()) {
            return PermissionDelegateImplV30()
        } else if (AndroidVersion.isAndroid10()) {
            return PermissionDelegateImplV29()
        } else if (AndroidVersion.isAndroid9()) {
            return PermissionDelegateImplV28()
        } else if (AndroidVersion.isAndroid8()) {
            return PermissionDelegateImplV26()
        } else if (AndroidVersion.isAndroid6()) {
            return PermissionDelegateImplV23()
        } else if (AndroidVersion.isAndroid5()) {
            return PermissionDelegateImplV21()
        } else if (AndroidVersion.isAndroid4_4()) {
            return PermissionDelegateImplV19()
        } else if (AndroidVersion.isAndroid4_3()) {
            return PermissionDelegateImplV18()
        } else {
            return PermissionDelegateImplV14()
        }
    }
    /**
     * 判断某个权限是否授予
     */
    fun isGrantedPermission(context: Context, permission: String): Boolean =  DELEGATE.isGrantedPermission(context, permission)

    /**
     * 判断某个权限是否被永久拒绝
     */
    fun isPermissionPermanentDenied(activity: FragmentActivity, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (DELEGATE.isPermissionPermanentDenied(activity, permission)) {
                return true
            }
        }
        return false
    }

    /**
     * 获取权限设置页意图
     */
    fun getPermissionIntent(context: Context, permission: String): Intent?  =  DELEGATE.getPermissionIntent(context, permission)

    /**
     * 判断某个权限是否是特殊权限
     */
    fun isSpecialPermission(permission: String): Boolean = PermissionUtils.isSpecialPermission(permission)

    /**
     * 判断某个权限集合是否包含特殊权限
     */
    fun containsSpecialPermission(vararg permissions: String): Boolean {
        if (permissions.isEmpty()) return false
        for (permission in permissions) {
            if (isSpecialPermission(permission)) {
                return true
            }
        }
        return false
    }


    /**
     * 判断某些权限是否全部被授予
     */
    fun isGrantedPermissions(context: Context, vararg permissions: String): Boolean {
        if (permissions.isEmpty()) {
            return false
        }
        for (permission in permissions) {
            if (!isGrantedPermission(context, permission)) {
                return false
            }
        }
        return true
    }

    /**
     * 获取已经授予的权限
     */
    fun getGrantedPermissions(context: Context, permissions: List<String>): List<String> {
        val grantedPermission: MutableList<String> = ArrayList(permissions.size)
        for (permission in permissions) {
            if (isGrantedPermission(context, permission)) {
                grantedPermission.add(permission)
            }
        }
        return grantedPermission
    }

    /**
     * 获取已经拒绝的权限
     */
    fun getDeniedPermissions(context: Context, vararg permissions: String): ArrayList<String> {
        val deniedPermission = ArrayList<String>(permissions.size)
        for (permission in permissions) {
            if (!isGrantedPermission(context, permission)) {
                deniedPermission.add(permission)
            }
        }
        return deniedPermission
    }


    /**
     * 获取没有授予的权限
     *
     * @param permissions           需要请求的权限组
     * @param grantResults          允许结果组
     */
    fun getDeniedPermissions(permissions: MutableList<String>, grantResults: IntArray): ArrayList<String> {
        val deniedPermissions = ArrayList<String>()
        for (i in grantResults.indices) {
            // 把没有授予过的权限加入到集合中
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permissions[i])
            }
        }
        return deniedPermissions
    }

    /**
     * 获取已授予的权限
     *
     * @param permissions       需要请求的权限组
     * @param grantResults      允许结果组
     */
    fun getGrantedPermissions(permissions: MutableList<String>, grantResults: IntArray): ArrayList<String> {
        val grantedPermissions = ArrayList<String>()
        for (i in grantResults.indices) {
            // 把授予过的权限加入到集合中
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                grantedPermissions.add(permissions[i])
            }
        }
        return grantedPermissions
    }

}