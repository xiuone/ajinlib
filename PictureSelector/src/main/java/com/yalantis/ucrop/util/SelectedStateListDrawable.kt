package com.yalantis.ucrop.util

import android.R
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import com.yalantis.ucrop.callback.BitmapLoadCallback
import com.yalantis.ucrop.task.BitmapLoadTask
import com.yalantis.ucrop.util.BitmapLoadUtils
import com.yalantis.ucrop.util.EglUtils
import kotlin.Throws
import androidx.annotation.RequiresApi
import com.yalantis.ucrop.util.RotationGestureDetector.OnRotationGestureListener
import com.yalantis.ucrop.util.RotationGestureDetector

/**
 * Hack class to properly support state drawable back to Android 1.6
 */
class SelectedStateListDrawable(drawable: Drawable?, private val mSelectionColor: Int) :
    StateListDrawable() {
    override fun onStateChange(states: IntArray): Boolean {
        var isStatePressedInArray = false
        for (state in states) {
            if (state == R.attr.state_selected) {
                isStatePressedInArray = true
            }
        }
        if (isStatePressedInArray) {
            super.setColorFilter(mSelectionColor, PorterDuff.Mode.SRC_ATOP)
        } else {
            super.clearColorFilter()
        }
        return super.onStateChange(states)
    }

    override fun isStateful(): Boolean {
        return true
    }

    init {
        addState(intArrayOf(R.attr.state_selected), drawable)
        addState(intArrayOf(), drawable)
    }
}