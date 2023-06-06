package com.xy.chat.chat.input.voice

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.xy.chat.chat.ChatBaseAssembly

class ChatInputVoiceAssembly(view: ChatInputVoiceAssemblyView) :ChatBaseAssembly<ChatInputVoiceAssembly.ChatInputVoiceAssemblyView>(view){
    private val voiceView by lazy { this.view?.onCreateVoiceView() }
    private val animView by lazy { this.view?.onCreateVoiceAnimView() }

    override fun onCreate(owner: LifecycleOwner?) {
        super.onCreate(owner)

    }


    interface ChatInputVoiceAssemblyView:ChatBaseAssemblyView{
        fun onCreateVoiceAnimView():View?
        fun onCreateVoiceView():View?
    }
}
