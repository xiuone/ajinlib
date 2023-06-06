package com.xy.chat.chat.content

import android.view.View
import android.widget.TextView
import com.xy.base.utils.emo.replaceEmoticons
import com.xy.base.widget.recycler.adapter.RecyclerMultiAdapter
import com.xy.chat.R
import com.xy.chat.data.message.MessageTextMode
import com.xy.chat.data.message.base.MessageBaseMode

open class ChatHolderTextImpl : ChatHolderBaseImpl() {

    override fun onCreateItemLayoutRes(): Int = R.layout.chat_content_item_text

    override fun showReceiveContent(view: View?, adapter: RecyclerMultiAdapter?,
        data: MessageBaseMode, position: Int) {
        super.showReceiveContent(view, adapter, data, position)
        showContent(view?.findViewById(R.id.receive_text_tv),adapter, data, position)
    }

    override fun showSendContent(view: View?, adapter: RecyclerMultiAdapter?,
                                 data: MessageBaseMode, position: Int) {
        super.showSendContent(view, adapter, data, position)
        showContent(view?.findViewById(R.id.send_text_tv),adapter, data, position)
    }

    private fun showContent(view: TextView?, adapter: RecyclerMultiAdapter?,
                            data: MessageBaseMode, position: Int){
        if (data is MessageTextMode){
            view?.replaceEmoticons(data.content)
        }

    }

}