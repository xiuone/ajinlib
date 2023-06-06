package com.jianbian.face.view

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.baidu.idl.face.platform.FaceStatusNewEnum
import com.baidu.idl.face.platform.LivenessTypeEnum
import com.baidu.idl.face.platform.manager.TimeManager
import com.jianbian.face.R

class AnimImageView@JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr) {

    private var mAnimationDrawable:AnimationDrawable?=null

    fun refreshView(status: FaceStatusNewEnum?,mLiveType:LivenessTypeEnum?){
        when (status) {
            FaceStatusNewEnum.OK, FaceStatusNewEnum.FaceLivenessActionComplete,
            FaceStatusNewEnum.DetectRemindCodeTooClose, FaceStatusNewEnum.DetectRemindCodeTooFar,
            FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame, FaceStatusNewEnum.DetectRemindCodeNoFaceDetected -> {
                stopAnim()
            }
            FaceStatusNewEnum.FaceLivenessActionCodeTimeout -> {
                loadAnimSource(mLiveType)
            }
        }
    }

    // 加载动画
    private fun loadAnimSource(mLiveType:LivenessTypeEnum?) {
        mLiveType?.run {
            when (this) {
                LivenessTypeEnum.Eye -> setBackgroundResource(R.drawable.anim_eye)
                LivenessTypeEnum.HeadLeft -> setBackgroundResource(R.drawable.anim_left)
                LivenessTypeEnum.HeadRight -> setBackgroundResource(R.drawable.anim_right)
                LivenessTypeEnum.HeadDown -> setBackgroundResource(R.drawable.anim_down)
                LivenessTypeEnum.HeadUp -> setBackgroundResource(R.drawable.anim_up)
                LivenessTypeEnum.Mouth -> setBackgroundResource(R.drawable.anim_mouth)
                else -> {}
            }
            val mAnimationDrawable = background
            if (mAnimationDrawable is AnimationDrawable){
                startAnim(mAnimationDrawable)
            }else{
                stopAnim()
            }
        }
    }

    private fun startAnim(mAnimationDrawable:AnimationDrawable){
        this@AnimImageView.mAnimationDrawable = mAnimationDrawable
        var duration = 0
        var i = 0
        while (i < mAnimationDrawable.numberOfFrames) {
            // 计算动画播放的时间
            duration += mAnimationDrawable.getDuration(i)
            i++
        }
        TimeManager.getInstance().activeAnimTime = duration
        getFather()?.visibility = View.INVISIBLE
        this@AnimImageView.mAnimationDrawable?.start()
    }


    fun stopAnim() {
        getFather()?.visibility = View.INVISIBLE
        mAnimationDrawable?.stop()
        mAnimationDrawable = null
    }

    private fun getFather():View?{
        val parent = parent
        return if (parent is View) parent else null
    }
}