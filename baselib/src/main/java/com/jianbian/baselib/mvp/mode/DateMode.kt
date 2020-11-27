package com.jianbian.baselib.mvp.mode

import androidx.annotation.Keep

@Keep
data class DateMode(
    var year:Int = 2000,
    var month:Int = 1,
    var day:Int = 1,
    var isToDay :Boolean= false,
    var isLeftMore :Boolean= false,
    var isRightMore :Boolean= false,
    var isSelect :Boolean= false
)