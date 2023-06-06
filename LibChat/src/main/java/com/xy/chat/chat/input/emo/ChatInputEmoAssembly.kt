package com.xy.chat.chat.input.emo

import android.view.View
import android.widget.RadioGroup
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager.widget.ViewPager
import com.xy.base.utils.exp.bindViewPager
import com.xy.chat.chat.ChatBaseAssembly

class ChatInputEmoAssembly(view: ChatInputEmpAssemblyView) :ChatBaseAssembly<ChatInputEmoAssembly.ChatInputEmpAssemblyView>(view){
    private val radioGroup by lazy { this.view?.onCreateRadioGroup() }
    private val viewPager by lazy { this.view?.onCreateViewPager() }

    override fun onCreate(owner: LifecycleOwner?) {
        super.onCreate(owner)
        radioGroup?.bindViewPager(viewPager)
    }

    interface ChatInputEmpAssemblyView:ChatBaseAssemblyView{
        fun onCreateRadioGroup():RadioGroup?
        fun onCreateViewPager():ViewPager?
        fun onCreateViewData():MutableList<View>
    }

}