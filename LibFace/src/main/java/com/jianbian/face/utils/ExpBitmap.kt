package com.jianbian.face.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.baidu.idl.face.platform.utils.Base64Utils

fun String.base64ToBitmap(): Bitmap {
    val bytes = Base64Utils.decode(this, Base64Utils.NO_WRAP)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}