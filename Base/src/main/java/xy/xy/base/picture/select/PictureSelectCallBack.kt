package xy.xy.base.picture.select

import com.luck.picture.lib.entity.LocalMedia


interface PictureSelectCallBack {
    fun onResult(result: ArrayList<LocalMedia>)
}