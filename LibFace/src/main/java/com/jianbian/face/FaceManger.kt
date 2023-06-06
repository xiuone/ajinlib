package com.jianbian.face

import android.graphics.Rect
import com.baidu.idl.face.platform.FaceEnvironment
import com.baidu.idl.face.platform.FaceSDKManager
import com.baidu.idl.face.platform.FaceStatusNewEnum
import com.baidu.idl.face.platform.LivenessTypeEnum
import com.baidu.idl.face.platform.listener.IInitCallback
import com.jianbian.face.config.QualityConfigManager
import com.jianbian.face.config.QualityGrade
import com.xy.base.utils.ContextHolder
import com.xy.base.utils.Logger
import com.xy.base.utils.runMain

object FaceManger {
    private val liveList by lazy { arrayListOf(LivenessTypeEnum.Eye,LivenessTypeEnum.Eye,LivenessTypeEnum.Eye) }
    private var initNumber = 0
    private var isRunInit = false
    private var isInitSuc = false;



    const val SURFACE_RATIO = 0.75f
    const val WIDTH_SPACE_RATIO = 0.33f
    const val HEIGHT_RATIO = 0.1f
    const val HEIGHT_EXT_RATIO = 0.2f


    fun init() {
        if (isInitSuc || isRunInit || initNumber >= 4)return
        val context = ContextHolder.getContext()?:return
        val config = FaceSDKManager.getInstance().faceConfig
        // 根据质量等级获取相应的质量值（注：第二个参数要与质量等级的set方法参数一致）
        QualityConfigManager.readQualityFile(context, QualityGrade.normal)
        isRunInit = true
        initNumber++
        // 设置模糊度阈值
        config.blurnessValue = QualityConfigManager.mQualityConfig.blur
        // 设置最小光照阈值（范围0-255）
        config.brightnessValue = QualityConfigManager.mQualityConfig.minIllum
        // 设置最大光照阈值（范围0-255）
        config.brightnessMaxValue = QualityConfigManager.mQualityConfig.maxIllum
        // 设置左眼遮挡阈值
        config.occlusionLeftEyeValue = QualityConfigManager.mQualityConfig.leftEyeOcclusion
        // 设置右眼遮挡阈值
        config.occlusionRightEyeValue = QualityConfigManager.mQualityConfig.rightEyeOcclusion
        // 设置鼻子遮挡阈值
        config.occlusionNoseValue = QualityConfigManager.mQualityConfig.noseOcclusion
        // 设置嘴巴遮挡阈值
        config.occlusionMouthValue = QualityConfigManager.mQualityConfig.mouseOcclusion
        // 设置左脸颊遮挡阈值
        config.occlusionLeftContourValue = QualityConfigManager.mQualityConfig.leftContourOcclusion
        // 设置右脸颊遮挡阈值
        config.occlusionRightContourValue = QualityConfigManager.mQualityConfig.rightContourOcclusion
        // 设置下巴遮挡阈值
        config.occlusionChinValue = QualityConfigManager.mQualityConfig.chinOcclusion
        // 设置人脸姿态角阈值
        config.headPitchValue = QualityConfigManager.mQualityConfig.pitch
        config.headYawValue = QualityConfigManager.mQualityConfig.yaw
        config.headRollValue = QualityConfigManager.mQualityConfig.roll
        // 设置可检测的最小人脸阈值
        config.minFaceSize = FaceEnvironment.VALUE_MIN_FACE_SIZE
        // 设置可检测到人脸的阈值
        config.notFaceValue = FaceEnvironment.VALUE_NOT_FACE_THRESHOLD
        // 设置闭眼阈值
        config.eyeClosedValue = FaceEnvironment.VALUE_CLOSE_EYES
        // 设置图片缓存数量
        config.cacheImageNum = FaceEnvironment.VALUE_CACHE_IMAGE_NUM
        // 设置活体动作，通过设置list，LivenessTypeEunm.Eye, LivenessTypeEunm.Mouth,
        // LivenessTypeEunm.HeadUp, LivenessTypeEunm.HeadDown, LivenessTypeEunm.HeadLeft,
        // LivenessTypeEunm.HeadRight
        config.livenessTypeList = liveList
        // 设置动作活体是否随机
        config.isLivenessRandom = true
        // 设置开启提示音
        config.isSound = true
        // 原图缩放系数
        config.scale = FaceEnvironment.VALUE_SCALE
        // 原图缩放系数
        config.cropHeight = FaceEnvironment.VALUE_CROP_HEIGHT
        config.cropWidth = FaceEnvironment.VALUE_CROP_WIDTH
        // 抠图人脸框与背景比例
        config.enlargeRatio = FaceEnvironment.VALUE_CROP_ENLARGERATIO
        // 加密类型，0：Base64加密，上传时image_sec传false；1：百度加密文件加密，上传时image_sec传true
        config.secType = FaceEnvironment.VALUE_SEC_TYPE
        // 检测超时设置
        config.timeDetectModule = FaceEnvironment.TIME_DETECT_MODULE
        // 检测框远近比率
        config.faceFarRatio = FaceEnvironment.VALUE_FAR_RATIO
        config.faceClosedRatio = FaceEnvironment.VALUE_CLOSED_RATIO
        FaceSDKManager.getInstance().faceConfig = config
        val licenseId = context.resources.getString(R.string.face_license_id)
        val licenseFileName = context.resources.getString(R.string.face_license_file_name)
        FaceSDKManager.getInstance().initialize(context, licenseId, licenseFileName, object : IInitCallback {
                override fun initSuccess() {
                    isInitSuc = true
                    isRunInit = false
                    Logger.d("百度人脸识别初始化成功")
                }

                override fun initFailure(i: Int, s: String) {
                    isRunInit = false
                    runMain({ init()},2000)
                    Logger.e("code:$i   info:$s")
                }
            })
    }


    fun preInit(){

        // Sound Res Id
        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected,
            R.raw.detect_face_in)
        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame,
            R.raw.detect_face_in)
        FaceEnvironment.setSoundId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected,
            R.raw.detect_face_in)
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveEye,
            R.raw.liveness_eye)
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveMouth,
            R.raw.liveness_mouth)
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchUp,
            R.raw.liveness_head_up)
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchDown,
            R.raw.liveness_head_down)
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawLeft,
            R.raw.liveness_head_left)
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawRight,
            R.raw.liveness_head_right)
        FaceEnvironment.setSoundId(FaceStatusNewEnum.FaceLivenessActionComplete,
            R.raw.face_good)
        FaceEnvironment.setSoundId(FaceStatusNewEnum.OK, R.raw.face_good)

        // Tips Res Id

        // Tips Res Id
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeNoFaceDetected,
            R.string.detect_face_in)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeBeyondPreviewFrame,
            R.string.detect_face_in)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePoorIllumination,
            R.string.detect_low_light)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeImageBlured,
            R.string.detect_keep)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionLeftEye,
            R.string.detect_occ_left_eye)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionRightEye,
            R.string.detect_occ_right_eye)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionNose,
            R.string.detect_occ_nose)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionMouth,
            R.string.detect_occ_mouth)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionLeftContour,
            R.string.detect_occ_left_check)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionRightContour,
            R.string.detect_occ_right_check)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeOcclusionChinContour,
            R.string.detect_occ_chin)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePitchOutofUpRange,
            R.string.detect_head_down)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodePitchOutofDownRange,
            R.string.detect_head_up)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeYawOutofLeftRange,
            R.string.detect_head_right)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeYawOutofRightRange,
            R.string.detect_head_left)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTooFar,
            R.string.detect_zoom_in)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTooClose,
            R.string.detect_zoom_out)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeLeftEyeClosed,
            R.string.detect_left_eye_close)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeRightEyeClosed,
            R.string.detect_right_eye_close)

        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveEye,
            R.string.liveness_eye)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveMouth,
            R.string.liveness_mouth)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchUp,
            R.string.liveness_head_up)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLivePitchDown,
            R.string.liveness_head_down)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawLeft,
            R.string.liveness_head_left)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionTypeLiveYawRight,
            R.string.liveness_head_right)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.FaceLivenessActionComplete,
            R.string.liveness_good)
        FaceEnvironment.setTipsId(FaceStatusNewEnum.OK, R.string.liveness_good)

        FaceEnvironment.setTipsId(FaceStatusNewEnum.DetectRemindCodeTimeout,
            R.string.detect_timeout)
    }


    // ----------------------------------------供调试用----------------------------------------------
    // 获取人脸检测区域
    fun getPreviewDetectRect(w: Int, pw: Int, ph: Int): Rect {
        val round = w / 2 - w / 2 * WIDTH_SPACE_RATIO
        val x = (pw / 2).toFloat()
        val y = ph / 2 - ph / 2 * HEIGHT_RATIO
        val r = if (pw / 2 > round) round else (pw / 2).toFloat()
        val hr = r + r * HEIGHT_EXT_RATIO
        return Rect((x - r).toInt(),
            (y - hr).toInt(),
            (x + r).toInt(),
            (y + hr).toInt())
    }
}