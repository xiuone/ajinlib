package com.yalantis.ucrop.util

import com.yalantis.ucrop.callback.BitmapLoadCallback
import com.yalantis.ucrop.task.BitmapLoadTask
import com.yalantis.ucrop.util.BitmapLoadUtils
import com.yalantis.ucrop.util.EglUtils
import kotlin.Throws
import androidx.annotation.RequiresApi
import com.yalantis.ucrop.util.RotationGestureDetector.OnRotationGestureListener
import com.yalantis.ucrop.util.RotationGestureDetector

object CubicEasing {
    @JvmStatic
    fun easeOut(time: Float, start: Float, end: Float, duration: Float): Float {
        var time = time
        return end * ((time / duration - 1.0f.also { time = it }) * time * time + 1.0f) + start
    }

    fun easeIn(time: Float, start: Float, end: Float, duration: Float): Float {
        var time = time
        return end * duration.let { time /= it; time } * time * time + start
    }

    @JvmStatic
    fun easeInOut(time: Float, start: Float, end: Float, duration: Float): Float {
        var time = time
        return if (duration / 2.0f.let { time /= it; time } < 1.0f) end / 2.0f * time * time * time + start else end / 2.0f * (2.0f.let { time -= it; time } * time * time + 2.0f) + start
    }
}