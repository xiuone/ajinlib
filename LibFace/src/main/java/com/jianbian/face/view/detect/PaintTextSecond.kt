package com.jianbian.face.view.detect

import android.graphics.Color
import android.graphics.Paint
import com.jianbian.face.R
import com.xy.base.utils.ContextHolder
import com.xy.base.utils.exp.getResDimensionFloat

class PaintTextSecond  :Paint(ANTI_ALIAS_FLAG){

    init {
        color = Color.parseColor("#666666")
        textSize = ContextHolder.getContext()?.getResDimensionFloat(R.dimen.sp_16)?:48F
        textAlign = Align.CENTER
        isAntiAlias = true
        isDither = true
    }
}