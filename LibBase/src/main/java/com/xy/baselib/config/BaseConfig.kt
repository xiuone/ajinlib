package com.xy.baselib.config

import com.infinitybrowser.baselib.sp.SPHelperUtils
import com.xy.baselib.font.FontManger
import com.xy.baselib.language.LanguageManger
import com.xy.baselib.life.ActivityController

object BaseConfig {
    val actController = ActivityController()
    val spHelperUtils = SPHelperUtils()
    val languageManger = LanguageManger()
    val fontManger = FontManger()
}