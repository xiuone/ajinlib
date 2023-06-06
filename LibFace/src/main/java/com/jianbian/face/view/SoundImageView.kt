package com.jianbian.face.view

import android.content.Context
import android.media.AudioManager
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import com.baidu.idl.face.platform.FaceSDKManager
import com.baidu.idl.face.platform.ILivenessStrategy
import com.jianbian.face.R
import com.xy.base.utils.volume.VolumeCallback
import com.xy.base.utils.volume.VolumeManger

class SoundImageView@JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr), VolumeCallback {
    private val TAG = "SoundImageView"
    private var mIsEnableSound:Boolean = false
    private var mILiveStrategy: ILivenessStrategy? = null
    private val mFaceConfig by lazy { FaceSDKManager.getInstance().faceConfig }


    fun startInitStatus(){
        val vol = getSysVolumeSize()
        mIsEnableSound = if (vol > 0)  mFaceConfig.isSound else false;
        setOnClickListener{
            setStatus(!mIsEnableSound)
        }
    }

    fun setILiveStrategy(mILiveStrategy:ILivenessStrategy?){
        this.mILiveStrategy = mILiveStrategy
    }

    fun onResume(){
        VolumeManger.instance.registerVolumeReceiver().addNotify(TAG,this)
    }

    fun onStop(){
        VolumeManger.instance.removeNotify(TAG)
    }

    fun getEnableSound() = mIsEnableSound


    private fun getSysVolumeSize():Int{
        try {
            val am = context.getSystemService(FragmentActivity.AUDIO_SERVICE)
            if (am is AudioManager){
                val cv = am.getStreamVolume(AudioManager.STREAM_MUSIC)
                return cv
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return 0
    }


    override fun volumeChanged() {
        setStatus(getSysVolumeSize() > 0)
    }


    private fun setStatus(mIsEnableSound:Boolean){
        this.mIsEnableSound = mIsEnableSound
        setImageResource(if (mIsEnableSound) R.mipmap.icon_titlebar_voice2 else R.mipmap.icon_titlebar_voice1)
        mILiveStrategy?.setLivenessStrategySoundEnable(mIsEnableSound)
    }


}