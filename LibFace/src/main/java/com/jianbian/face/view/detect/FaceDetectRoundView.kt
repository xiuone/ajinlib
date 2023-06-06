/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.jianbian.face.view.detect

import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import com.baidu.idl.face.platform.FaceSDKManager
import com.baidu.idl.face.platform.FaceStatusNewEnum
import com.jianbian.face.FaceManger
import com.jianbian.face.R
import com.xy.base.utils.exp.getResString

/**
 * 人脸检测区域View
 */
class FaceDetectRoundView @JvmOverloads constructor(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val mBGPaint: Paint by lazy { PaintBG() }
    private val mFaceRoundPaint: Paint by lazy { PaintFaceRound() }
    private val mCircleLinePaint: Paint by lazy { PaintCircleLine() }
    private val mCircleLineSelectPaint: Paint by lazy { PaintCircleLineSelect() }
    private val mTextSecondPaint: Paint by lazy { PaintTextSecond() }
    private val mTextTopPaint: Paint by lazy { PaintTextTop() }


    private var mFaceRect: Rect? = null
    private var mFaceDetectRect: Rect? = null

    private var mX = 0f
    private var mY = 0f
    private var round = 0f
    private var mTotalActiveCount = 0
    private var mSuccessActiveCount = 0
    private var mIsActiveLive = false

    private var mTipSecondText: String? = null
    private var mTipTopText: String? = null

    fun setProcessCount(successActiveCount: Int, totalActiveCount: Int) {
        mSuccessActiveCount = successActiveCount
        mTotalActiveCount = totalActiveCount
        postInvalidate()
    }

    fun setIsActiveLive(isActiveLive: Boolean) {
        mIsActiveLive = isActiveLive
    }

    fun setTipTopText(tipTopText: String?) {
        mTipTopText = tipTopText
        postInvalidate()

    }

    fun setTipSecondText(tipSecondText: String?) {
        mTipSecondText = tipSecondText
        postInvalidate()
    }

    fun refreshView(status: FaceStatusNewEnum?, message: String?, currentLivenessCount: Int, ) {
        when (status) {
            FaceStatusNewEnum.OK, FaceStatusNewEnum.FaceLivenessActionComplete,
            FaceStatusNewEnum.DetectRemindCodeTooClose, FaceStatusNewEnum.DetectRemindCodeTooFar,
            FaceStatusNewEnum.FaceLivenessActionTypeLiveEye, FaceStatusNewEnum.FaceLivenessActionTypeLiveMouth,
            FaceStatusNewEnum.FaceLivenessActionTypeLivePitchUp, FaceStatusNewEnum.FaceLivenessActionTypeLivePitchDown,
            FaceStatusNewEnum.FaceLivenessActionTypeLiveYawLeft, FaceStatusNewEnum.FaceLivenessActionTypeLiveYawRight,
            FaceStatusNewEnum.FaceLivenessActionTypeLiveYaw, FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame,
            FaceStatusNewEnum.DetectRemindCodeNoFaceDetected -> {
                mTipTopText = message
                mTipSecondText = ""
            }
            FaceStatusNewEnum.FaceLivenessActionCodeTimeout -> {}
            else -> {
                mTipTopText = context.getResString(R.string.please_keep_your_face_straight)
                mTipSecondText = message
            }
        }
        mSuccessActiveCount = currentLivenessCount
        mTotalActiveCount = FaceSDKManager.getInstance().faceConfig.livenessTypeList.size
        postInvalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val canvasWidth = (right - left).toFloat()
        val canvasHeight = (bottom - top).toFloat()
        val x = canvasWidth / 2
        val y = canvasHeight / 2 - canvasHeight / 2 * FaceManger.HEIGHT_RATIO
        val r = canvasWidth / 2 - canvasWidth / 2 * FaceManger.WIDTH_SPACE_RATIO
        if (mFaceRect == null) {
            mFaceRect = Rect((x - r).toInt(),
                (y - r).toInt(),
                (x + r).toInt(),
                (y + r).toInt())
        }
        if (mFaceDetectRect == null) {
            val hr = r + r * FaceManger.HEIGHT_EXT_RATIO
            mFaceDetectRect = Rect((x - r).toInt(),
                (y - hr).toInt(),
                (x + r).toInt(),
                (y + hr).toInt())
        }
        mX = x
        mY = y
        round = r
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.TRANSPARENT)
        canvas.drawPaint(mBGPaint)
        canvas.drawCircle(mX, mY, round, mFaceRoundPaint)

        // 画文字
        if (!TextUtils.isEmpty(mTipSecondText)) {
            canvas.drawText(mTipSecondText!!, mX, mY - round - 40 - 25 - 59, mTextSecondPaint)
        }
        if (!TextUtils.isEmpty(mTipTopText)) {
            canvas.drawText(mTipTopText!!, mX, mY - round - 40 - 25 - 59 - 90, mTextTopPaint)
        }
        if (mIsActiveLive) {
            canvas.translate(mX, mY)
            // 画默认进度
            drawCircleLine(canvas)
            // 画成功进度
            drawSuccessCircleLine(canvas)
        }
    }

    // 画默认刻度线
    private fun drawCircleLine(canvas: Canvas) {
        canvas.save()
        canvas.rotate(-90f)
        var j = 0
        while (j < 360) {
            canvas.drawLine(round + 40, 0f, round + 40 + 25, 0f, mCircleLinePaint)
            canvas.rotate(6f)
            j += 6
        }
        canvas.restore()
    }

    // 画成功刻度线
    private fun drawSuccessCircleLine(canvas: Canvas) {
        val degree = (mSuccessActiveCount.toFloat() / mTotalActiveCount.toFloat() * 360.0f).toInt()
        // Log.e(TAG, "selectDegree = " + degree);
        canvas.save()
        canvas.rotate(-90f)
        var j = 0
        while (j < degree) {
            canvas.drawLine(round + 40, 0f, round + 40 + 25, 0f, mCircleLineSelectPaint)
            canvas.rotate(6f)
            j += 6
        }
        canvas.restore()
    }


    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }
}