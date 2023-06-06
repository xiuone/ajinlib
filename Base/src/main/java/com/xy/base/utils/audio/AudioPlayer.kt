package com.xy.base.utils.audio

import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import com.xy.base.utils.ContextHolder
import com.xy.base.utils.exp.getSpInt
import com.xy.base.utils.exp.setSpInt
import java.io.File

/**
 * 和AudioRecorder对应音频播放器。<br></br>
 * AudioRecorder的录音格式可以是aac，但在低版本系统上，MediaPlayer是不支持aac格式的。这个类对aac格式做了兼容处理。<br></br>
 * 同时针对会话场景的语音播放，将MediaPlayer的回调接口做了封装，以方便使用。
 */
object AudioPlayer{
    private val audioPlayCallStatus = "audioPlayCallStatus"

    private var mPlayer: MediaPlayer? = null
    private val WHAT_COUNT_PLAY = 0x000
    private val WHAT_DECODE_SUCCEED = 0x001
    private val WHAT_DECODE_FAILED = 0x002
    private val mIntervalTime = 500L

    private var playFile :String ?= null

    private val mListenerList by lazy { ArrayList<OnPlayListener>() }

    private var currentVoiceCall  = AudioManager.STREAM_MUSIC

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                WHAT_COUNT_PLAY -> {
                    try {
                        synchronized(this@AudioPlayer){
                            for (item in mListenerList){
                                item?.onPlaying(mPlayer?.currentPosition?.toLong()?:0L)
                            }
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        com.xy.base.utils.Logger.d(  " error:${e.message}")
                    }
                    sendEmptyMessageDelayed(WHAT_COUNT_PLAY, mIntervalTime)
                }
                WHAT_DECODE_FAILED -> {
                    com.xy.base.utils.Logger.d( "convert() error: $playFile")
                }
                WHAT_DECODE_SUCCEED -> startInner()
            }
        }
    }


    private fun getAudioManager():AudioManager?{
        val context = ContextHolder.getContext()?:return null
        return context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    fun getCurrentVoiceCall() = ContextHolder.getContext()?.getSpInt(
        audioPlayCallStatus,AudioManager.STREAM_VOICE_CALL)?:AudioManager.STREAM_VOICE_CALL

    private fun setCurrentVoiceCall(voiceCall:Int) = ContextHolder.getContext()?.setSpInt(
        audioPlayCallStatus,voiceCall)

    fun setRelativeCurrentVoiceCall() = if (isMicroPhone()) AudioManager.STREAM_VOICE_CALL else AudioManager.STREAM_MUSIC

    fun isMicroPhone() = getCurrentVoiceCall() == AudioManager.STREAM_VOICE_CALL

    /**
     * 设置音频来源
     *
     * @param audioFile 待播放音频的文件路径
     */
    fun setDataSource(audioFile: String,vararg listener: OnPlayListener) = setDataSource(audioFile, getCurrentVoiceCall(),*listener)

    /**
     * 设置音频来源
     *
     * @param audioFile 待播放音频的文件路径
     */
    fun setDataMusicSource(audioFile: String,vararg listener: OnPlayListener) = setOnlyDataSource(audioFile, AudioManager.STREAM_MUSIC,*listener)

    /**
     * 设置音频来源
     *
     * @param audioFile 待播放音频的文件路径
     */
    fun setDataSource(audioFile: String,voiceCall:Int,vararg listener: OnPlayListener) {
        if (!TextUtils.equals(audioFile, playFile)) {
            if (voiceCall != getCurrentVoiceCall()){
                setCurrentVoiceCall(voiceCall)
            }
            setOnlyDataSource(audioFile, voiceCall, *listener)
        }
    }

    /**
     * 设置音频来源
     *
     * @param audioFile 待播放音频的文件路径
     */
    private fun setOnlyDataSource(audioFile: String,voiceCall:Int,vararg listener: OnPlayListener) {
        synchronized(this){
            if (!TextUtils.equals(audioFile, playFile)) {
                currentVoiceCall = voiceCall
                com.xy.base.utils.Logger.d("start play audio file$audioFile voiceCall:$voiceCall")
                playFile = audioFile
                com.xy.base.utils.Logger.d("start() called")
                stop()
                mListenerList.clear()
                mListenerList.addAll(listener)
                startInner()
            }
        }
    }

    /**
     * 停止播放
     */
    fun stop() {
        if (mPlayer != null) {
            endPlay()
            synchronized(this){
                for (item in mListenerList){
                    item.onInterrupt()
                }
            }
        }
    }

    /**
     * 查询是否正在播放
     *
     * @return 如果为true，表示正在播放，否则没有播放
     */
    fun isPlaying() :Boolean {
        try {
            return mPlayer != null && mPlayer?.isPlaying == true
        }catch (e: Throwable){
            com.xy.base.utils.Logger.e("isPlaying error : ${e.message}")
        }
        return false
    }
    /**
     * 获取音频持续时间长度
     *
     * @return 持续时间
     */
    fun getDuration(): Long{
        try {
            return mPlayer?.duration?.toLong()?:0L
        } catch (e: Throwable) {
            e.printStackTrace()
            com.xy.base.utils.Logger.e("getDuration error : ${e.message}")
        }
        return 0L
    }

    /**
     * 获取当前音频播放进度
     *
     * @return 当前播放进度
     */
    fun getCurrentPosition(): Long{
        try {
            return mPlayer?.currentPosition?.toLong()?:0L
        } catch (e: Throwable) {
            e.printStackTrace()
            com.xy.base.utils.Logger.e("getCurrentPosition error :${e.message}")
        }
        return 0L
    }

    /**
     * 让播放器跳转到指定位置继续播放
     *
     * @param msec 指定播放位置，单位为毫秒
     */
    fun seekTo(msec: Int) {
        try {
            mPlayer!!.seekTo(msec)
        } catch (e: Throwable) {
            e.printStackTrace()
            com.xy.base.utils.Logger.e("seekTo error :${e.message}")
        }
    }

    private fun setVolume(leftVolume: Float, rightVolume: Float) {
        try {
            mPlayer?.setVolume(leftVolume, rightVolume)
        } catch (e: Throwable) {
            e.printStackTrace()
            com.xy.base.utils.Logger.e("setVolume error :${e.message}")
        }
    }

    private fun endPlay() {
        getAudioManager()?.abandonAudioFocus(onAudioFocusChangeListener)
        if (mPlayer != null) {
            try {
                mPlayer?.stop()
            } catch (e: Throwable) {
                e.printStackTrace()
                com.xy.base.utils.Logger.e("endPlay error${e.message}")
            }
            try {
                mPlayer?.release()
            } catch (e: Throwable) {
                e.printStackTrace()
                com.xy.base.utils.Logger.e("release error${e.message}")
            }
            mPlayer = null
            mHandler.removeMessages(WHAT_COUNT_PLAY)
        }
    }


    private fun startInner() {
        mPlayer = MediaPlayer()
        try {
            mPlayer?.isLooping = false
            mPlayer?.setAudioStreamType(currentVoiceCall)
            getAudioManager()?.isSpeakerphoneOn = currentVoiceCall == AudioManager.STREAM_MUSIC
            getAudioManager()?.requestAudioFocus(onAudioFocusChangeListener, currentVoiceCall, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            mPlayer?.setOnPreparedListener {
                com.xy.base.utils.Logger.d( "player:onPrepared")
                mHandler.sendEmptyMessage(WHAT_COUNT_PLAY)
                synchronized(this){
                    for (item in mListenerList){
                        item.onPrepared()
                    }
                }
            }
            mPlayer?.setOnCompletionListener {
                com.xy.base.utils.Logger.d(  "player:onCompletion")
                endPlay()
                synchronized(this){
                    for (item in mListenerList){
                        item.onCompletion()
                    }
                }
            }
            mPlayer?.setOnErrorListener { mp, what, extra ->
                com.xy.base.utils.Logger.e(String.format("player:onOnError what:%d extra:%d", what, extra))
                endPlay()
                synchronized(this){
                    for (item in mListenerList){
                        item.onError(String.format("OnErrorListener what:%d extra:%d", what, extra))
                    }
                }
                true
            }
            if (playFile != null) {
                mPlayer?.setDataSource(playFile)
            } else {
                synchronized(this){
                    for (item in mListenerList){
                        item.onError("no datasource")
                    }
                }
                return
            }
            mPlayer?.prepare()
            mPlayer?.start()
            com.xy.base.utils.Logger.d("player:start ok---->$playFile")
        } catch (e: Throwable) {
            e.printStackTrace()
            com.xy.base.utils.Logger.d( "player:onOnError Exception\n$e")
            endPlay()
            synchronized(this){
                for (item in mListenerList){
                    item.onError("Exception\n$e")
                }
            }
        }
    }

    private fun deleteOnExit() {
        val converted = File(playFile)
        if (converted.exists()) {
            converted.deleteOnExit()
        }
    }

    var onAudioFocusChangeListener: OnAudioFocusChangeListener = OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN ->                 // 获得音频焦点
                    if (isPlaying()) {
                        setVolume(1.0f, 1.0f)
                    }
                AudioManager.AUDIOFOCUS_LOSS ->                 // 长久的失去音频焦点，释放MediaPlayer
                    stop()
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ->                 // 展示失去音频焦点，暂停播放等待重新获得音频焦点
                    stop()
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->                 // 失去音频焦点，无需停止播放，降低声音即可
                    if (isPlaying()) {
                        setVolume(0.1f, 0.1f)
                    }
            }
        }
}