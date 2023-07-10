package com.yalantis.ucrop.callback

import android.graphics.Bitmap
import android.net.Uri
import com.yalantis.ucrop.model.ExifInfo

interface BitmapLoadCallback {
    fun onBitmapLoaded(bitmap: Bitmap, exifInfo: ExifInfo, imageInputUri: Uri, imageOutputUri: Uri?)
    fun onFailure(bitmapWorkerException: Exception)
}