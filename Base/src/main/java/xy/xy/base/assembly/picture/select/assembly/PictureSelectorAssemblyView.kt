package xy.xy.base.assembly.picture.select.assembly

import picture.luck.picture.lib.entity.LocalMedia
import picture.luck.picture.lib.style.PictureSelectorStyle
import xy.xy.base.assembly.base.BaseAssemblyViewWithContext
import xy.xy.base.assembly.picture.select.audio.RecordAudioIntercept
import xy.xy.base.assembly.picture.select.crop.ImageCropEngineBase

interface PictureSelectorAssemblyView : BaseAssemblyViewWithContext {
    fun onCreateSelectType(): PictureSelectorAssembly.SelectType = PictureSelectorAssembly.SelectType.ALL
    fun onMaxNumber():Int = 1
    fun isPreviewImage():Boolean = false
    fun onGetSelectedList():MutableList<LocalMedia>? = ArrayList()
    fun onGetPictureSelectorStyle(): PictureSelectorStyle? = null
    fun onCreateImageCropEngine(): ImageCropEngineBase?=null
    fun onCreateRecordAudioIntercept(): RecordAudioIntercept?=null
}