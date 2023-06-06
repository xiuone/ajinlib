package com.xy.chat.chat

import android.animation.AnimatorSet
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.EditText
import androidx.core.view.isVisible
import com.xy.base.assembly.base.BaseAssembly
import com.xy.base.assembly.base.BaseAssemblyView
import com.xy.base.utils.anim.ViewAnimHelper
import com.xy.base.utils.anim.WrapperView
import com.xy.base.utils.exp.getIdStr
import com.xy.base.utils.exp.getResDimension
import com.xy.base.utils.exp.getTypeInt
import com.xy.base.utils.softkey.OnSoftKeyBoardChangeListener
import com.xy.base.utils.softkey.SoftKeyBoardDetector
import com.xy.chat.R
import com.xy.chat.type.ChatSessionEnum

abstract class ChatBaseAssembly<T : ChatBaseAssembly.ChatBaseAssemblyView>(view: T) : BaseAssembly<T>(view) {
    protected val currentAct by lazy { this.view?.getCurrentAct() }
    protected val sessionEnum by lazy { getChatType() }
    protected val conversationId by lazy { this.view?.getInputIntent()?.getIdStr() }

    private fun getChatType():ChatSessionEnum{
        return when(view?.getInputIntent()?.getTypeInt(ChatSessionEnum.P2P.type)){
            ChatSessionEnum.Team.type-> ChatSessionEnum.Team
            ChatSessionEnum.System.type-> ChatSessionEnum.System
            ChatSessionEnum.ChatRoom.type-> ChatSessionEnum.ChatRoom
            else-> ChatSessionEnum.P2P
        }
    }

    /**
     * 显示View
     */
    protected fun createShowViewAnim(builder: AnimatorSet.Builder?, view: View?, height:Int):Boolean{
        if (view == null)return false
        if (view.isVisible){
            if (view.height != height){
                ViewAnimHelper.setHeight(builder,height)
                return true
            }
        }else{
            val viewWrapper = WrapperView(view)
            viewWrapper.height = 0
            view.visibility = View.VISIBLE
            ViewAnimHelper.setHeight(builder,height)
            return true
        }
        return false
    }

    /**
     * 隐藏view
     */
    protected fun createHindViewAnim(context: Context?, builder: AnimatorSet.Builder?, vararg views: View?):Boolean{
        var showAnim = false
        for (view in views){
            if (view != null && view.isVisible){
                val hintMaxSize = context?.getResDimension(R.dimen.dp_80)?:0
                if (view.height > hintMaxSize){
                    val viewWrapper = WrapperView(view)
                    viewWrapper.height = hintMaxSize
                    ViewAnimHelper.setHeight(builder,0)
                    showAnim = true
                }
            }
        }
        return showAnim
    }

    /**
     * 隐藏软键盘
     */
    protected fun hideSoft(currentAct: Activity?, editView: EditText?, runnable: Runnable){
        if (currentAct == null){
            runnable.run()
            return
        }
        if (!SoftKeyBoardDetector.isSoftShowing(currentAct)){
            runnable.run()
        }
        SoftKeyBoardDetector.register(currentAct,object : OnSoftKeyBoardChangeListener {
            override fun keyBoardShow(context: Context, height: Int) {
                super.keyBoardShow(context, height)
                SoftKeyBoardDetector.removeListener(currentAct,this)
            }

            override fun keyBoardHide(context: Context, height: Int) {
                super.keyBoardHide(context, height)
                SoftKeyBoardDetector.removeListener(currentAct,this)
                runnable.run()
            }
        })
        SoftKeyBoardDetector.closeKeyBord(editView)
    }

    interface ChatBaseAssemblyView : BaseAssemblyView {
        fun getInputIntent(): Intent?
    }
}

