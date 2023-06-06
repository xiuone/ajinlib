package com.jianbian.face.view.detect

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode

class PaintFaceRound : Paint(ANTI_ALIAS_FLAG){
    init {
        color = Color.parseColor("#FFA800")
        style = Style.FILL
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
        isDither = true
    }
}