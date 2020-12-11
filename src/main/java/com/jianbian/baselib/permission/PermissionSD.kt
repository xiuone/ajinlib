package com.jianbian.baselib.permission

import com.yanzhenjie.permission.Permission
import com.yanzhenjie.permission.PermissionListener

class PermissionSD : PermissionBase {
    constructor(view: Any?,listener: PermissionListener)
            : super(view,listener,Permission.STORAGE,1000)
}