package com.xy.chat.chat.content

import android.view.View
import android.widget.TextView
import com.xy.base.widget.recycler.adapter.RecyclerMultiAdapter
import com.xy.chat.R
import com.xy.chat.data.message.MessageVideoMode
import com.xy.chat.data.message.base.MessageBaseMode

open class ChatHolderVoiceImpl : ChatHolderBaseImpl() {

    override fun onCreateItemLayoutRes(): Int = R.layout.chat_content_item_voice

    override fun showReceiveContent(view: View?, adapter: RecyclerMultiAdapter?,
                                    data: MessageBaseMode, position: Int) {
        super.showReceiveContent(view, adapter, data, position)
        showContent(view?.findViewById(R.id.receive_voice_time_tv),adapter, data, position)
    }

    override fun showSendContent(view: View?, adapter: RecyclerMultiAdapter?,
                                 data: MessageBaseMode, position: Int) {
        super.showSendContent(view, adapter, data, position)
        showContent(view?.findViewById(R.id.send_voice_time_tv),adapter, data, position)
    }

    private fun showContent(timeTv:TextView?,adapter: RecyclerMultiAdapter?,
                            data: MessageBaseMode, position: Int){
        if (data is MessageVideoMode){
            timeTv?.text = "${data.duration/1000}"
        }
    }
}