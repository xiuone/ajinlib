package com.xy.base.permission

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2018/06/15
 * desc   : 权限请求结果回调接口
 */
interface OnPermissionCallback {
    /**
     * 有权限被同意授予时回调
     *
     * @param permissions           请求成功的权限组
     * @param allGranted            是否全部授予了
     */
    fun onGranted(permissions: List<String?>, allGranted: Boolean)

    /**
     * 有权限被拒绝授予时回调
     *
     * @param permissions            请求失败的权限组
     * @param doNotAskAgain          是否勾选了不再询问选项
     */
    fun onDenied(permissions: List<String?>, doNotAskAgain: Boolean) {}
}