package com.xy.qq

import com.tencent.tauth.Tencent
import xy.xy.base.utils.ContextHolder
import xy.xy.base.utils.exp.getResString

object QQManger {
    val qqAppId by lazy { ContextHolder.getContext()?.getResString(R.string.qq_app_id) }
    val mTencent by lazy { Tencent.createInstance(qqAppId, ContextHolder.getContext()?.applicationContext); }
}