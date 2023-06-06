package com.xy.qr

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.xy.base.assembly.base.BaseAssemblyView
import com.xy.base.utils.permission.PermissionUiListener

interface QrCreateAssemblyView :BaseAssemblyView, PermissionUiListener {
    fun createSaveView():View?
    fun createQrImageViewList():MutableList<ImageView?>
    fun createSaveQrButton():View?
    fun createChangeQrButton():View?
    fun createLogoBitmap():Bitmap?
    fun createNewContent():String?
    fun createBitmapSize():Int = 600
    fun createSaveHistoryKey():String?
}