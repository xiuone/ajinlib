package com.xy.picture.select

import picture.luck.picture.lib.entity.LocalMedia

interface PictureSelectCallBack {
    fun onResult(result: ArrayList<LocalMedia>)
}