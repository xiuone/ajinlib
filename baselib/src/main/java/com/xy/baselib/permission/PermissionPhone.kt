package com.xy.baselib.permission

import com.yanzhenjie.permission.Permission
import com.yanzhenjie.permission.PermissionListener

class PermissionPhone : PermissionBase {
    constructor(view: Any?,listener: PermissionListener)
            : super(view,listener,Permission.PHONE,1000)
}