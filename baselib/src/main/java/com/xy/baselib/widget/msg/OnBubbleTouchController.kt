package com.xy.baselib.widget.msg

import android.graphics.PointF
import android.graphics.drawable.AnimationDrawable
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import com.xy.baselib.R
import com.xy.utils.getViewBitmap
import com.xy.utils.getViewPosRect

class OnBubbleTouchController(private val mView: View,private var onDisappearListener: OnDisappearListener?)
    : View.OnTouchListener ,OnBubbleTouchListener{
    var mBethelView: BubbleTipView? = null
    var mBombFrameLayout: FrameLayout? = null
    var mBombImageView: ImageView? = null

    var mLayoutParams: ViewGroup.LayoutParams? = null

    init {
        val  wrapper = FrameLayout.LayoutParams.WRAP_CONTENT
        val  match = FrameLayout.LayoutParams.MATCH_PARENT
        mLayoutParams = ViewGroup.LayoutParams(match,match)
        mBombFrameLayout = FrameLayout(mView.context)
        mBethelView = BubbleTipView(mView.context)
        mBombImageView = ImageView(mView.context)
        mBombImageView?.setImageResource(R.drawable.anim_bubble_pop)
        mBombFrameLayout?.addView(mBombImageView, FrameLayout.LayoutParams(wrapper, wrapper))
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mBethelView?.mBitmap = mView.getViewBitmap()
                getRootView()?.addView(mBethelView, mLayoutParams)
                val rect = mView.getViewPosRect()
                mBethelView?.initPoint(rect.left+rect.width()/2F, rect.top + rect.height()/2F)
                mView.visibility = View.INVISIBLE
            }
            MotionEvent.ACTION_MOVE -> mBethelView?.updateDragPoint(event.rawX, event.rawY)
            MotionEvent.ACTION_UP -> mBethelView?.actionUp(this)
        }
        return true
    }


    private fun getRootView():ViewGroup?{
        val rootView = mView.rootView
        if (rootView is ViewGroup)
            return rootView
        return null
    }

    private fun getAnimationDrawable():AnimationDrawable?{
        val animationDrawable = mBombImageView?.drawable
        if (animationDrawable is AnimationDrawable)
            return animationDrawable
        return null
    }

    private fun getAnimationTime():Long{
        var time: Long = 0
        getAnimationDrawable()?.run {
            for (i in 0 until numberOfFrames) {
                time += getDuration(i).toLong()
            }
        }
        return time;
    }


    override fun springBack() {
        getRootView()?.removeView(mBethelView)
        mView.visibility = View.VISIBLE
    }

    override fun dismiss(pointF: PointF?) {
        getRootView()?.removeView(mBethelView)

        mBombImageView?.x = (pointF?.x ?: 0F) - ( mBombImageView?.width?.toFloat()?:0F) / 2
        mBombImageView?.y = (pointF?.x ?: 0F) - (mBombImageView?.height?.toFloat()?:0F) / 2

        getRootView()?.addView(mBombFrameLayout, mLayoutParams)

        getAnimationDrawable()?.start()

        mBombImageView?.postDelayed({
            getRootView()?.removeView(mBombFrameLayout)
            mView.visibility = View.GONE
            onDisappearListener?.onDismiss()
        }, getAnimationTime())
    }
}