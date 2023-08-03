package xy.xy.base.assembly.picture.select.assembly

import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.style.PictureSelectorStyle
import xy.xy.base.assembly.base.BaseAssemblyViewWithContext
import xy.xy.base.assembly.picture.select.audio.RecordAudioIntercept
import xy.xy.base.assembly.picture.select.crop.ImageCropEngineBase

interface PictureSelectorAssemblyView : BaseAssemblyViewWithContext {
    fun isPreviewImage():Boolean = false
    fun onGetPictureSelectorStyle(): PictureSelectorStyle? = null
    fun onCreateImageCropEngine(): ImageCropEngineBase?=null
    fun onCreateRecordAudioIntercept(): RecordAudioIntercept?=null
}