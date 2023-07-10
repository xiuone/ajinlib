package com.xy.picture.select.assembly

import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.style.PictureSelectorStyle
import com.xy.base.assembly.base.BaseAssemblyViewWithContext
import com.xy.base.permission.IPermissionInterceptorCreateListener
import com.xy.picture.select.audio.RecordAudioIntercept
import com.xy.picture.select.crop.ImageCropEngineBase

interface PictureSelectorAssemblyView : BaseAssemblyViewWithContext,IPermissionInterceptorCreateListener {
    fun onCreateSelectType(): PictureSelectorAssembly.SelectType = PictureSelectorAssembly.SelectType.ALL
    fun onMaxNumber():Int = 1
    fun isPreviewImage():Boolean = false
    fun onGetSelectedList():MutableList<LocalMedia>? = ArrayList()
    fun onGetPictureSelectorStyle(): PictureSelectorStyle? = null
    fun onCreateImageCropEngine(): ImageCropEngineBase?=null
    fun onCreateRecordAudioIntercept(): RecordAudioIntercept? = RecordAudioIntercept(onCreateIPermissionInterceptor())
}