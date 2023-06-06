package com.xy.chat.chat.content

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xy.base.widget.recycler.adapter.RecyclerMultiAdapter
import com.xy.base.widget.recycler.adapter.RecyclerMultiListener
import com.xy.base.widget.recycler.holder.BaseViewHolder
import com.xy.base.widget.recycler.holder.RecyclerMultiBaseHolder
import com.xy.chat.chat.ChatBaseAssembly
import com.xy.chat.type.MessageTypeEnum

class ChatContentAssembly(view:ChatContentAssemblyView) :ChatBaseAssembly<ChatContentAssembly.ChatContentAssemblyView>(view) {
    private val recyclerView by lazy { this.view?.onCreateRecyclerView() }
    private val adapter by lazy { ChatContentAdapter() }

    private val itemTextHolder by lazy {view.onCreateTextHolderListener() }
    private val itemImageHolder by lazy {view.onCreateImageHolderListener() }
    private val itemVideoHolder by lazy {view.onCreateVideoHolderListener() }
    private val itemVoiceHolder by lazy {view.onCreateVoiceHolderListener() }
    private val itemLocationHolder by lazy {view.onCreateLocationHolderListener() }
    private val itemUnKnowHolder by lazy {view.onCreateUnKnowHolderListener() }

    override fun onCreate(owner: LifecycleOwner?) {
        super.onCreate(owner)
        recyclerView?.layoutManager = LinearLayoutManager(getContext())
        recyclerView?.adapter = adapter
    }

    private inner class ChatContentAdapter:RecyclerMultiAdapter(){
        override fun onItemCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return when(viewType){
                MessageTypeEnum.Text.type-> onItemCreateViewHolder(itemTextHolder,parent)
                MessageTypeEnum.Image.type-> onItemCreateViewHolder(itemImageHolder,parent)
                MessageTypeEnum.Video.type-> onItemCreateViewHolder(itemVideoHolder,parent)
                MessageTypeEnum.Voice.type-> onItemCreateViewHolder(itemVoiceHolder,parent)
                MessageTypeEnum.Location.type->onItemCreateViewHolder(itemLocationHolder,parent)
                else-> onItemCreateViewHolder(itemUnKnowHolder,parent)
            }
        }


        private fun onItemCreateViewHolder(listener: ChatHolderListener,parent: ViewGroup):BaseViewHolder{
            val context = parent.context
            val view  = LayoutInflater.from(context).inflate(listener.onCreateItemLayoutRes(),parent,false)
            return ChatContentHolderBase(view,this@ChatContentAdapter,listener)
        }

        private inner class ChatContentHolderBase(itemView: View, adapter: RecyclerMultiAdapter, private val listener: ChatHolderListener) :
            RecyclerMultiBaseHolder(itemView,adapter){
            override fun onBindViewHolder(data: RecyclerMultiListener, position: Int) {
                listener.onBindViewHolder(this,adapter, data, position)
            }
        }

    }


    interface ChatHolderListener{
        fun onCreateItemLayoutRes():Int
        fun onBindViewHolder(holder: BaseViewHolder,adapter: RecyclerMultiAdapter?, data: RecyclerMultiListener, position: Int)
    }

    interface ChatContentAssemblyView :ChatBaseAssemblyView{
        fun onCreateRecyclerView():RecyclerView?
        fun onCreateTextHolderListener():ChatHolderListener = ChatHolderTextImpl()
        fun onCreateImageHolderListener():ChatHolderListener = ChatHolderImageImpl()
        fun onCreateVideoHolderListener():ChatHolderListener = ChatHolderVideoImpl()
        fun onCreateVoiceHolderListener():ChatHolderListener = ChatHolderVoiceImpl()
        fun onCreateLocationHolderListener():ChatHolderListener = ChatHolderLocationImpl()
        fun onCreateUnKnowHolderListener():ChatHolderListener = ChatHolderUnKnowImpl()
    }
}