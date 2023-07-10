package com.xy.qr

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.xy.base.assembly.base.BaseAssemblyViewWithContext
import com.xy.base.permission.IPermissionInterceptor
import com.xy.base.permission.IPermissionInterceptorCreateListener

interface QrCreateAssemblyView :BaseAssemblyViewWithContext ,IPermissionInterceptorCreateListener{
    fun createSaveView():View?
    fun createQrImageViewList():MutableList<ImageView?>
    fun createSaveQrButton():View?
    fun createChangeQrButton():View?
    fun createLogoBitmap():Bitmap?
    fun createNewContent():String?
    fun createBitmapSize():Int = 600
    fun createSaveHistoryKey():String?
}