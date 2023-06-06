package com.xy.qq.share

import android.graphics.Bitmap
import java.util.ArrayList

data class QQShareContent(val type: QQShareSceneEnum,
                          val url: String?= "http://www.baidu.com",
                          val title:String ?= null,
                          val content:String ?= null,
                          val bitmap: Bitmap ?= null,
                          val imgUrls: ArrayList<String>? = null)