package com.xy.wechat.share

import android.graphics.Bitmap

data class WeChatContent(val shareType: WechatShareTypeEnum,
                         val res: Int,
                         val title:String = "",
                         val content:String = "",
                         val targetUlr:String? = "",
                         val defUlr:String = "http://www.baidu.com",
                         val imageUrl:String = "",
                         val bitmap: Bitmap?= null)
