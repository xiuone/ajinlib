package com.xy.baselib.sp

class SPHelperSimple : SPHelperUtils() {

    companion object {
        val mInstance: SPHelperSimple = SPHelperSimple()
    }

    override fun sharedPreferencesName(): String? = "SPHelperSimple"
}