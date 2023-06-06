package com.xy.chat.chat.content

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.xy.base.utils.exp.getResDimension
import com.xy.base.utils.exp.loadImageWithCenter
import com.xy.base.widget.recycler.adapter.RecyclerMultiAdapter
import com.xy.chat.R
import com.xy.chat.data.message.MessageImageMode
import com.xy.chat.data.message.base.MessageBaseMode
import kotlin.math.max
import kotlin.math.min

open class ChatHolderImageImpl : ChatHolderBaseImpl() {
    override fun onCreateItemLayoutRes(): Int = R.layout.chat_content_item_image

    override fun showReceiveContent(view: View?, adapter: RecyclerMultiAdapter?,
        data: MessageBaseMode, position: Int) {
        super.showReceiveContent(view, adapter, data, position)
        showContent(view?.findViewById(R.id.receive_image_iv),adapter, data, position)
    }

    override fun showSendContent(view: View?, adapter: RecyclerMultiAdapter?,
                                 data: MessageBaseMode, position: Int) {
        super.showSendContent(view, adapter, data, position)
        showContent(view?.findViewById(R.id.send_image_iv),adapter, data, position)
    }

    private fun showContent(imageView: ImageView?,adapter: RecyclerMultiAdapter?,
                            data: MessageBaseMode, position: Int){
        resetLayoutParams(imageView, data)
        if (data is MessageImageMode){
            imageView?.loadImageWithCenter(data.getThumb())
        }
    }

    private fun resetLayoutParams(imageView: ImageView?, data: MessageBaseMode){
        val context = imageView?.context
        val defaultSize = context?.getResDimension(R.dimen.dp_100)?:100
        val maxSize = context?.getResDimension(R.dimen.dp_150)?:200
        if (data is MessageImageMode){
            var width = defaultSize
            var height = defaultSize
            if (data.width <=0 || data.height <=0){
                width = defaultSize
                height = defaultSize
            } else if (data.width == data.height){
                width = min(maxSize,data.width)
                height = min(maxSize,data.width)
            } else if (data.width > data.height){
                width = min(data.width,maxSize)
                height = width*data.height/data.width
                height = max(width/4,height)
            }else{
                height = min(data.height ,maxSize)
                width = height*data.width/data.height
                width = max(height/4,width)
            }
            imageView?.layoutParams = FrameLayout.LayoutParams(width, height)
        }
    }
}