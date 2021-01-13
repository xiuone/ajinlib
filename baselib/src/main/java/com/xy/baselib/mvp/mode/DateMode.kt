package com.xy.baselib.mvp.mode

import androidx.annotation.Keep

@Keep
data class DateMode(
    var year:Int = 2000,
    var month:Int = 1,
    var day:Int = 1,
    var isToDay :Boolean= false,
    var isSelect :Boolean= false,
    var nowMoth:Boolean = true

)