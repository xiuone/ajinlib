package com.xy.wechat.share

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import com.tencent.mm.opensdk.modelmsg.*
import xy.xy.base.utils.ContextHolder
import xy.xy.base.utils.exp.getResBitmap
import xy.xy.base.utils.runBackThread
import xy.xy.base.utils.runMain
import com.xy.wechat.WxManger
import java.io.ByteArrayOutputStream
import java.net.URL

object WechatShareUtils {


    fun share(weChatContent: WeChatContent, scene: WechatShareSceneEnum) {
        runBackThread({
            val msg = weChatContent.getMessageObject()
            val req = SendMessageToWX.Req()
            req.transaction = buildTransaction("weixin")
            req.message = msg
            req.scene = when(scene){
                WechatShareSceneEnum.Friend ->SendMessageToWX.Req.WXSceneSession
                WechatShareSceneEnum.Zone ->SendMessageToWX.Req.WXSceneTimeline
                else ->SendMessageToWX.Req.WXSceneFavorite
            }
            runMain({
                WxManger.wxApi.sendReq(req)
            })
        })
    }


    private fun WeChatContent.getMessageObject(): WXMediaMessage {
        val msg = WXMediaMessage()
        when(shareType){
            WechatShareTypeEnum.text->{
                if (!TextUtils.isEmpty(content)) {
                    val textObj = WXTextObject()
                    textObj.text = content
                    msg.mediaObject = textObj
                }
            }
            WechatShareTypeEnum.image->{
                val imgObj = WXImageObject()
                val result = bmpToByteArray()
                if (result != null) {
                    imgObj.imageData = result
                    msg.mediaObject = imgObj
                }
            }
            WechatShareTypeEnum.webPath-> {
                val webpage = WXWebpageObject()
                webpage.webpageUrl = if (!TextUtils.isEmpty(targetUlr)) targetUlr else defUlr
                msg.mediaObject = webpage
            }
            WechatShareTypeEnum.music-> {
                if (!TextUtils.isEmpty(targetUlr)) {
                    val music = WXMusicObject()
                    music.musicUrl = targetUlr
                    msg.mediaObject = music
                }
            }
            WechatShareTypeEnum.video-> {
                if (!TextUtils.isEmpty(targetUlr)) {
                    val video = WXVideoObject()
                    video.videoUrl = targetUlr
                    msg.mediaObject = video
                }
            }
        }
        msg.title = title
        msg.description = content
        msg.messageExt = content
        val result = bmpToByteArray()
        if (result != null) {
            msg.thumbData = result
        }
        return msg
    }


    private fun WeChatContent.bmpToByteArray(): ByteArray? {
        var bmp = bitmap
        try {
            if (bmp == null && !TextUtils.isEmpty(imageUrl)) {
                bmp = BitmapFactory.decodeStream(URL(imageUrl).openStream())
            }
            if (bmp == null || bmp.byteCount <= 0) {
                bmp = ContextHolder.getContext()?.getResBitmap(res)
            }
            val bytes = bitmapBytes(bmp)
            if (bytes.size > WxManger.maxSize) {
                bmp = ContextHolder.getContext()?.getResBitmap(res)
                return bitmapBytes(bmp)
            }
            return bytes
        } catch (e: Exception) {
            e.printStackTrace()
        }
        bmp = ContextHolder.getContext()?.getResBitmap(res)
        return bitmapBytes(bmp)
    }

    private fun bitmapBytes(bitmap: Bitmap?): ByteArray {
        val output = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, output)
        var options = 100
        while (output.toByteArray().size > WxManger.maxSize && options > 0) {
            output.reset() //清空output
            bitmap?.compress(Bitmap.CompressFormat.JPEG, options, output) //这里压缩options%，把压缩后的数据存放到output中
            if (options != 1) {
                options -= 10
                if (options <= 0) {
                    options = 1
                }
            } else {
                options = -1
            }
        }
        return output.toByteArray()
    }


    private fun buildTransaction(type: String?): String? {
        return if (type == null) System.currentTimeMillis()
            .toString() else type + System.currentTimeMillis()
    }
}