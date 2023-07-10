package com.luck.picture.lib.photoview

import android.graphics.RectF
import kotlin.jvm.JvmOverloads
import androidx.appcompat.widget.AppCompatImageView

/**
 * Interface definition for a callback to be invoked when the internal Matrix has changed for
 * this View.
 */
interface OnMatrixChangedListener {
    /**
     * Callback for when the Matrix displaying the Drawable has changed. This could be because
     * the View's bounds have changed, or the user has zoomed.
     *
     * @param rect - Rectangle displaying the Drawable's new bounds.
     */
    fun onMatrixChanged(rect: RectF?)
}