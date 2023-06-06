package com.xy.base.utils.picture

import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener

interface PictureSelectCallBack {
    fun onResult(result: ArrayList<LocalMedia>)
}