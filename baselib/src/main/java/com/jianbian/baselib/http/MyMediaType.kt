package com.jianbian.baselib.http

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType

object MyMediaType {
    val JSON_TYPE: MediaType = "application/json; charset=utf-8".toMediaType()
}