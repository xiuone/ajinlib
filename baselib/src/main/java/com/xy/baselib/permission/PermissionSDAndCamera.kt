package com.xy.baselib.permission

import android.Manifest
import com.yanzhenjie.permission.PermissionListener

class PermissionSDAndCamera : PermissionBase {
    constructor(view: Any?,listener: PermissionListener)
            : super(view,listener, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE
        , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),1000)
}