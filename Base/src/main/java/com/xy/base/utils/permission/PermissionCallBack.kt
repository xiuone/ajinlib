package com.xy.base.utils.permission

interface PermissionCallBack {
    fun onGranted()
    fun onDenied(){}
}