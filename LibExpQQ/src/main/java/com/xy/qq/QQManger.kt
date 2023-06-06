package com.xy.qq

import com.tencent.tauth.Tencent
import com.xy.base.utils.ContextHolder
import com.xy.base.utils.exp.getResString

object QQManger {
    val qqAppId by lazy { ContextHolder.getContext()?.getResString(R.string.qq_app_id) }
    val mTencent by lazy { Tencent.createInstance(qqAppId, ContextHolder.getContext()?.applicationContext); }
}