package com.xy.chat.chat.content

import android.view.View
import android.widget.ImageView
import com.xy.base.utils.AppTimeUtils
import com.xy.base.utils.exp.loadImageWithCenter
import com.xy.base.widget.recycler.adapter.RecyclerMultiAdapter
import com.xy.base.widget.recycler.adapter.RecyclerMultiListener
import com.xy.base.widget.recycler.holder.BaseViewHolder
import com.xy.chat.R
import com.xy.chat.data.message.base.MessageBaseMode
import com.xy.chat.db.ImUserLoginManger
import com.xy.chat.db.user.ImUserManger

abstract class ChatHolderBaseImpl : ChatContentAssembly.ChatHolderListener {

    override fun onBindViewHolder(holder: BaseViewHolder, adapter: RecyclerMultiAdapter?,
                                  data: RecyclerMultiListener, position: Int) {
        showTime(holder, adapter, data, position)
        showContent(holder, adapter, data, position)
    }

    open fun showTime(holder: BaseViewHolder, adapter: RecyclerMultiAdapter?,
                      data: RecyclerMultiListener, position: Int){
        val timeTv = holder.getTextView(R.id.time_tv)
        if (data is MessageBaseMode){
            timeTv?.text = AppTimeUtils.getTimeShowString(data.messageTime)
            val lastPosition = position - 1
            val dataList = adapter?.data?: ArrayList()
            if (lastPosition < dataList.size){
                val lastData = dataList[lastPosition]
                if (lastData is MessageBaseMode && Math.abs(lastData.messageTime - data.messageTime) > 1000*60*5){
                    timeTv?.visibility = View.VISIBLE
                }else{
                    timeTv?.visibility = View.GONE
                }
            }else{
                timeTv?.visibility = View.VISIBLE
            }
        }
    }

    open fun showContent(holder: BaseViewHolder, adapter: RecyclerMultiAdapter?,
                          data: RecyclerMultiListener, position: Int){
        val context = holder.itemView.context
        if (data is MessageBaseMode){
            val loginToken = ImUserLoginManger.getImUserLoginToken(context)
            if (loginToken == data.form){
                showSendContent(holder.getView<View>(R.id.send_layout), adapter, data, position)
            }else{
                showReceiveContent(holder.getView<View>(R.id.receive_layout), adapter, data, position)
            }
        }
    }

    open fun showSendContent(view: View?, adapter: RecyclerMultiAdapter?,
                                 data: MessageBaseMode, position: Int){
        val userBean = ImUserManger.instance.getImUser(data.form)?:return
        view?.findViewById<ImageView>(R.id.head_img)?.loadImageWithCenter(userBean.userIcon)
    }

    open fun showReceiveContent(view: View?, adapter: RecyclerMultiAdapter?,
                                    data: MessageBaseMode, position: Int){
        val userBean = ImUserManger.instance.getImUser(data.form)?:return
        view?.findViewById<ImageView>(R.id.head_img)?.loadImageWithCenter(userBean.userIcon)
    }
}