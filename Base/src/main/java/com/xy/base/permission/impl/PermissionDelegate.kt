package com.xy.base.permission.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/06/11
 * desc   : 权限委托接口
 */
interface PermissionDelegate {
    /**
     * 判断某个权限是否授予了
     */
    fun isGrantedPermission(context: Context, permission: String): Boolean

    /**
     * 判断某个权限是否永久拒绝了
     */
    fun isPermissionPermanentDenied(activity: FragmentActivity, permission: String): Boolean

    /**
     * 获取权限设置页的意图
     */
    fun getPermissionIntent(context: Context, permission: String): Intent?
}