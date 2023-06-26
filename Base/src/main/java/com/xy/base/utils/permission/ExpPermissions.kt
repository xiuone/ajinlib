package com.xy.base.utils.permission

import android.app.Activity
import android.content.Context
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions

fun Context.requestPermission(rationaleDialog: PermissionDialogReason?, deniedDialog: PermissionDialogDenied?,
                              listener:PermissionCallBack?, vararg permissions:PermissionRequestMode){
    val xxPermissions = XXPermissions.with(this)
    val stringBuffer = StringBuffer()
    var isAllGranted = true
    var isAllDenied = false
    for (permission in permissions){
        val isGranted = XXPermissions.isGranted(this,permission.permission)
        if (this is Activity) {
            isAllDenied = isAllDenied || XXPermissions.isPermanentDenied(this, permission.permission)
        }
        if (!isGranted){
            isAllGranted = false
            if (stringBuffer.isNotEmpty()){
                stringBuffer.append("\n")
            }else{
                stringBuffer.append(" · ${permission.permissionInfo}")
            }
            xxPermissions.permission(permission.permission)
        }
    }
    if (isAllDenied){
        onDenied(deniedDialog,listener,*permissions)
        return
    }
    if (isAllGranted){
        listener?.onGranted()
        return
    }
    rationaleDialog?.bindActionListener(object :PermissionActionListener{
        override fun onPermissionCancelAction() {
            listener?.onDenied()
        }
        override fun onPermissionSureNextAction() = startRequestPermission(xxPermissions, deniedDialog, listener,*permissions)
    })
    if (rationaleDialog != null){
        rationaleDialog.showDialog(stringBuffer.toString())
    }else{
        startRequestPermission(xxPermissions, deniedDialog, listener,*permissions)
    }
}


private fun Context.startRequestPermission(xxPermissions:XXPermissions,
                                           deniedDialog: PermissionDialogDenied?,
                                           listener:PermissionCallBack?,
                                           vararg permissionRequestModes:PermissionRequestMode){
    xxPermissions.request(object : OnPermissionCallback {
        override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
            if (allGranted){
                listener?.onGranted()
                return
            }else{
                listener?.onDenied()
            }
        }

        override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
            if (doNotAskAgain) {
                onDenied(deniedDialog, listener, *permissionRequestModes)
            } else {
                listener?.onDenied()
            }
        }
    })
}

private fun Context.onDenied(deniedDialog: PermissionDialogDenied?,listener:PermissionCallBack?
                             ,vararg permissionRequestModes:PermissionRequestMode){
    val stringBuffer = StringBuffer()
    val permissionsList = ArrayList<String>()
    for (permission in permissionRequestModes){
        val isGranted = XXPermissions.isGranted(this,permission.permission)
        if (!isGranted) {
            permissionsList.addAll(permission.permission)
            if (stringBuffer.isNotEmpty()){
                stringBuffer.append("\n")
            }else{
                stringBuffer.append(" · ${permission.permissionInfo}")
            }
        }
    }
    deniedDialog?.bindActionListener(object :PermissionActionListener{
        override fun onPermissionCancelAction(){
            listener?.onDenied()
        }
        override fun onPermissionSureNextAction() = XXPermissions.startPermissionActivity(this@onDenied, permissionsList)
    })
    if (deniedDialog != null && !stringBuffer.toString().isNullOrEmpty()) {
        deniedDialog.showDialog(stringBuffer.toString())
    }else{
        XXPermissions.startPermissionActivity(this, permissionsList)
    }
}