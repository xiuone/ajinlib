package com.xy.base.permission

import android.app.Activity
import androidx.fragment.app.FragmentActivity

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2020/12/26
 * desc   : 权限请求拦截器
 */
interface IPermissionInterceptor {
    /**
     * 发起权限申请（可在此处先弹 Dialog 再申请权限，如果用户已经授予权限，则不会触发此回调）
     *
     * @param allPermissions            申请的权限
     * @param callback                  权限申请回调
     */
    fun launchPermissionRequest(activity: FragmentActivity, allPermissions: ArrayList<String>, callback: OnPermissionCallback?) {
        PermissionFragment.launch(activity, allPermissions, this, callback)
    }

    /**
     * 用户授予了权限（注意需要在此处回调 [OnPermissionCallback.onGranted]）
     *
     * @param allPermissions             申请的权限
     * @param grantedPermissions         已授予的权限
     * @param allGranted                 是否全部授予
     * @param callback                   权限申请回调
     */
    fun grantedPermissionRequest(activity: FragmentActivity, allPermissions: ArrayList<String>, grantedPermissions: ArrayList<String>,
                                 allGranted: Boolean, callback: OnPermissionCallback?, ) {
        if (callback == null) {
            return
        }
        callback.onGranted(grantedPermissions, allGranted)
    }

    /**
     * 用户拒绝了权限（注意需要在此处回调 [OnPermissionCallback.onDenied]）
     *
     * @param allPermissions            申请的权限
     * @param deniedPermissions         已拒绝的权限
     * @param doNotAskAgain             是否勾选了不再询问选项
     * @param callback                  权限申请回调
     */
    fun deniedPermissionRequest(activity: FragmentActivity, allPermissions: ArrayList<String>, deniedPermissions: ArrayList<String>,
                                doNotAskAgain: Boolean, callback: OnPermissionCallback?) {
        if (callback == null) return
        callback.onDenied(deniedPermissions, doNotAskAgain)
    }

    /**
     * 权限请求完成
     *
     * @param allPermissions            申请的权限
     * @param skipRequest               是否跳过了申请过程
     * @param callback                  权限申请回调
     */
    fun finishPermissionRequest(activity: FragmentActivity, allPermissions: ArrayList<String>,
        skipRequest: Boolean, callback: OnPermissionCallback?) {
    }
}