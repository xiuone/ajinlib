package com.xy.base.widget.voice

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.xy.base.utils.audio.OnPlayListener


class VoiceImageView@JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr) ,OnPlayListener{
    override fun onPrepared() {
        getAnimDrawable()?.start()
    }

    override fun onCompletion() {
        getAnimDrawable()?.stop()
    }

    override fun onInterrupt() {
        getAnimDrawable()?.stop()
    }

    override fun onError(error: String?) {
        getAnimDrawable()?.stop()
    }

    override fun onPlaying(curPosition: Long) {
        getAnimDrawable()?.stop()
    }


    private fun getAnimDrawable():AnimationDrawable?{
        val animDrawable = background
        if (animDrawable is AnimationDrawable){
            return animDrawable
        }
        return null
    }

}