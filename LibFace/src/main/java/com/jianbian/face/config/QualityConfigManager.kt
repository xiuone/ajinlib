package com.jianbian.face.config

import android.content.Context
import android.util.Log
import com.baidu.idl.face.platform.utils.FileUtils
import org.json.JSONObject

object QualityConfigManager {
    private const val FILE_NAME_QUALITY = "quality_config.json"

    val mQualityConfig: QualityConfig by lazy { QualityConfig() }

    fun readQualityFile(context: Context, qualityGrade: QualityGrade) {
        try {
            var json = FileUtils.readAssetFileUtf8String(context.assets, FILE_NAME_QUALITY)
            val jsonObject = JSONObject(json).optJSONObject(qualityGrade.type)
            mQualityConfig.minIllum = jsonObject.optDouble("minIllum").toFloat()
            mQualityConfig.maxIllum = jsonObject.optDouble("maxIllum").toFloat()
            mQualityConfig.blur = jsonObject.optDouble("blur").toFloat()
            mQualityConfig.leftEyeOcclusion = jsonObject.optDouble("leftEyeOcclusion").toFloat()
            mQualityConfig.rightEyeOcclusion = jsonObject.optDouble("rightEyeOcclusion").toFloat()
            mQualityConfig.noseOcclusion = jsonObject.optDouble("noseOcclusion").toFloat()
            mQualityConfig.mouseOcclusion = jsonObject.optDouble("mouseOcclusion").toFloat()
            mQualityConfig.leftContourOcclusion = jsonObject.optDouble("leftContourOcclusion").toFloat()
            mQualityConfig.rightContourOcclusion = jsonObject.optDouble("rightContourOcclusion").toFloat()
            mQualityConfig.chinOcclusion = jsonObject.optDouble("chinOcclusion").toFloat()
            mQualityConfig.pitch = jsonObject.optInt("pitch")
            mQualityConfig.yaw = jsonObject.optInt("yaw")
            mQualityConfig.roll = jsonObject.optInt("roll")
        } catch (e: java.lang.Exception) {
            Log.e(this.javaClass.name, "初始配置读取失败", e)
        }
    }
}