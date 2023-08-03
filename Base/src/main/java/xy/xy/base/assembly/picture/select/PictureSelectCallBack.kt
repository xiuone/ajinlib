package xy.xy.base.assembly.picture.select

import com.luck.picture.lib.entity.LocalMedia


interface PictureSelectCallBack {
    fun onResult(result: ArrayList<LocalMedia>)
}