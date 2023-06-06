package com.xy.chat.chat.input

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.xy.base.utils.exp.setOnClick
import com.xy.chat.chat.ChatBaseAssembly

class ChatInputStatusAssembly(view:ChatInputStatusAssemblyView,private val listener:ChatInputStatusCallBack) :
    ChatBaseAssembly<ChatInputStatusAssembly.ChatInputStatusAssemblyView>(view){
    //语音按钮
    private val voiceButtonView by lazy { this.view?.onCreateVoiceButtonView() }
    //更多按钮
    private val moreButtonView by lazy { this.view?.onCreateMoreButtonView() }
    //表情按钮
    private val emoButtonView by lazy { this.view?.onCreateEmoButtonView() }
    //发送按钮
    private val sendButtonView by lazy { this.view?.onCreateSendButton() }
    //常用语按钮
    private val commonUseButtonView by lazy { this.view?.onCreateCommonUseButton() }
    //显示的一种状态
    private val oneStatusList by lazy { HashMap<ChatInputStatusEnum,View?>() }

    private var currentStatus = ChatInputStatusEnum.Send

    override fun onCreate(owner: LifecycleOwner?) {
        super.onCreate(owner)
        oneStatusList[ChatInputStatusEnum.Voice] = voiceButtonView
        oneStatusList[ChatInputStatusEnum.More] = moreButtonView
        oneStatusList[ChatInputStatusEnum.Emo] = emoButtonView
        oneStatusList[ChatInputStatusEnum.Send] = sendButtonView
        oneStatusList[ChatInputStatusEnum.CommonUse] = commonUseButtonView
        setListener()
    }

    /**
     * 设置监听
     */
    private fun setListener(){
        voiceButtonView?.setOnClick{ changeInputStatus(ChatInputStatusEnum.Voice) }
        moreButtonView?.setOnClick{ changeInputStatus(ChatInputStatusEnum.More) }
        emoButtonView?.setOnClick{ changeInputStatus(ChatInputStatusEnum.Emo) }
        sendButtonView?.setOnClick{ changeInputStatus(ChatInputStatusEnum.Send) }
        commonUseButtonView?.setOnClick{ changeInputStatus(ChatInputStatusEnum.CommonUse) }
    }

    /**
     * 修改状态
     */
    private fun changeInputStatus(enum: ChatInputStatusEnum){
        for (entry in oneStatusList.entries){
            entry.value?.isSelected = enum == entry.key
        }
        listener.onChangeStatusCallBack(enum)
        currentStatus = enum
    }

    /**
     * 按钮回调
     */
    interface ChatInputStatusAssemblyView :ChatBaseAssemblyView{
        fun onCreateVoiceButtonView(): View?=null
        fun onCreateMoreButtonView(): View?=null
        fun onCreateEmoButtonView(): View?=null
        fun onCreateSendButton():View?=null
        fun onCreateCommonUseButton():View?=null
    }


    /**
     * 回调信息
     */
    interface ChatInputStatusCallBack{
        fun onChangeStatusCallBack(enum: ChatInputStatusEnum)
    }


    enum class ChatInputStatusEnum{
        Voice,More,Emo,Send,CommonUse
    }
}