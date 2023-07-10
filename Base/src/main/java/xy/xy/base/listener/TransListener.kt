package xy.xy.base.listener

import android.graphics.Bitmap

interface TransListener {
    fun onTransBitmap(conversationId:String, conversationType: String, bitmap:Bitmap)
    fun onTransImage(conversationId:String, conversationType: String, image:String, delPath:Boolean)
    fun onTransVideo(conversationId:String, conversationType: String, video:String, delPath:Boolean)
    fun onTransContent(conversationId:String, conversationType: String, content:String)
}