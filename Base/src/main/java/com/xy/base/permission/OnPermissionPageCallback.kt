package com.xy.base.permission

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/01/17
 * desc   : 权限设置页结果回调接口
 */
interface OnPermissionPageCallback {
    /**
     * 权限已经授予
     */
    fun onGranted()

    /**
     * 权限已经拒绝
     */
    fun onDenied() {}
}