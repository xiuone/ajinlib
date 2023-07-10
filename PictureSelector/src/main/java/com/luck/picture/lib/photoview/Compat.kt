package com.luck.picture.lib.photoview

import android.annotation.TargetApi
import android.view.View
import kotlin.jvm.JvmOverloads
import androidx.appcompat.widget.AppCompatImageView

internal object Compat {
    fun postOnAnimation(view: View, runnable: Runnable) {
        postOnAnimationJellyBean(view, runnable)
    }

    @TargetApi(16)
    private fun postOnAnimationJellyBean(view: View, runnable: Runnable) {
        view.postOnAnimation(runnable)
    }
}