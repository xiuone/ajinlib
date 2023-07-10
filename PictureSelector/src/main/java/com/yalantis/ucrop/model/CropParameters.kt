package com.yalantis.ucrop.model

import android.graphics.Bitmap
import android.net.Uri

/**
 * Created by Oleksii Shliama [https://github.com/shliama] on 6/21/16.
 */
class CropParameters(
    val maxResultImageSizeX: Int, val maxResultImageSizeY: Int,
    val compressFormat: Bitmap.CompressFormat, val compressQuality: Int,
    val imageInputPath: String, val imageOutputPath: String, val exifInfo: ExifInfo) {
    var contentImageInputUri: Uri? = null
    var contentImageOutputUri: Uri? = null

}