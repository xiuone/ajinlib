package com.luck.picture.lib.photoview

import android.widget.ImageView
import kotlin.jvm.JvmOverloads
import androidx.appcompat.widget.AppCompatImageView

/**
 * Callback when the user tapped outside of the photo
 */
interface OnOutsidePhotoTapListener {
    /**
     * The outside of the photo has been tapped
     */
    fun onOutsidePhotoTap(imageView: ImageView?)
}