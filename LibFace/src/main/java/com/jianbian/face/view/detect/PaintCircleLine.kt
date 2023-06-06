package com.jianbian.face.view.detect

import android.graphics.Color
import android.graphics.Paint
import com.jianbian.face.R
import com.xy.base.utils.ContextHolder
import com.xy.base.utils.exp.getResDimensionFloat

class PaintCircleLine : Paint(ANTI_ALIAS_FLAG){
    init {
        color = Color.parseColor("#FFD400")
        strokeWidth = ContextHolder.getContext()?.getResDimensionFloat(R.dimen.dp_3)?:9F
        style = Style.STROKE
        isAntiAlias = true
        isDither = true
    }
}