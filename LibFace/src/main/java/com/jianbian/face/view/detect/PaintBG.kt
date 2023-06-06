package com.jianbian.face.view.detect

import android.graphics.Color
import android.graphics.Paint

class PaintBG :Paint(ANTI_ALIAS_FLAG) {
    init {
        color = Color.parseColor("#FFFFFF")
        style = Style.FILL
        isAntiAlias = true
        isDither = true
    }
}