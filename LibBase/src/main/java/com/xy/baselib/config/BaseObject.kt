package com.xy.baselib.config

import com.xy.baselib.sp.SPHelperUtils
import com.xy.baselib.font.FontManger
import com.xy.baselib.language.LanguageManger
import com.xy.baselib.life.ActivityController
import com.xy.baselib.notify.config.ConfigNotify
import com.xy.baselib.receiver.NetStateChangeReceiver
import com.xy.baselib.softkey.SoftKeyBoardDetectorHeightController

object BaseObject {
    val actController = ActivityController()
    val keyHeightController = SoftKeyBoardDetectorHeightController()
    val spHelperUtils = SPHelperUtils()
    val languageManger = LanguageManger()
    val fontManger = FontManger()
    val netStateManager = NetStateChangeReceiver()
    val configNotify = ConfigNotify()
}