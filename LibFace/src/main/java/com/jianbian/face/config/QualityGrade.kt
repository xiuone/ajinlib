package com.jianbian.face.config

import android.text.TextUtils
import com.baidu.idl.face.platform.utils.FileUtils
import org.json.JSONObject

enum class QualityGrade(val type:String) {
    normal("normal"),
    low("loose"),
    high("strict"),
}