package com.xy.baselib.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.luck.picture.lib.PictureSelectionModel
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.xy.baselib.BaseApp
import java.io.File


fun selectImg(any: Any?,listener:OnResultCallbackListener<LocalMedia>){
    selectImg(any, null, listener)
}

fun selectImg(any: Any?,data:MutableList<LocalMedia>?,listener:OnResultCallbackListener<LocalMedia>){
    selectImg(any, data, 1, listener)
}

fun selectImg(any: Any?,data:MutableList<LocalMedia>?,more:Int,listener:OnResultCallbackListener<LocalMedia>){
    selectImg(any, data,true, more, listener)
}

fun selectImg(any: Any?,data:MutableList<LocalMedia>?,camera:Boolean,more:Int,listener:OnResultCallbackListener<LocalMedia>){
    selectImg(any, PictureMimeType.ofImage(),data,camera, more, listener)
}

fun selectImg(any: Any?,chooseMode: Int,data:MutableList<LocalMedia>?,camera:Boolean,more:Int,listener:OnResultCallbackListener<LocalMedia>){
    selectMedia(any, chooseMode, more,camera, data, listener)
}


fun selectImg(any: Any?,code:Int){
    selectImg(any, null, code)
}

fun selectImg(any: Any?,data:MutableList<LocalMedia>?,code:Int){
    selectImg(any, data, 1, code)
}

fun selectImg(any: Any?,data:MutableList<LocalMedia>?,more:Int,code:Int){
    selectImg(any,data,true,more, code)
}

fun selectImg(any: Any?,data:MutableList<LocalMedia>?,camera: Boolean,more:Int,code:Int){
    selectImg(any, PictureMimeType.ofImage(),data,camera, more, code)
}

fun selectImg(any: Any?,chooseMode: Int,data:MutableList<LocalMedia>?,camera:Boolean,more:Int,code:Int){
    selectMedia(any, chooseMode, more,camera, data, code)
}


/**
 * 选择头像
 */
private fun selectMedia(any:Any?,chooseMode:Int,more: Int,camera: Boolean ,data:MutableList<LocalMedia>?,code:Int){
    getPictureSelector(any, chooseMode, more,camera, data)?.forResult(code)
}


/**
 * 选择头像
 */
private fun selectMedia(any:Any?,chooseMode:Int,more: Int,camera: Boolean ,data:MutableList<LocalMedia>?,listener: OnResultCallbackListener<LocalMedia>){
    getPictureSelector(any, chooseMode, more,camera, data)?.forResult(listener)
}



private fun getPictureSelector(any:Any?,chooseMode:Int,more: Int,camera: Boolean ,data:MutableList<LocalMedia>?): PictureSelectionModel?{
    var pictureSelector:PictureSelector?=null
    if (any == null || BaseApp.engine == null)return null
    if (any is Activity)
        pictureSelector = PictureSelector.create(any)
    else if (any is Fragment)
        pictureSelector = PictureSelector.create(any)
    return pictureSelector?.openGallery(chooseMode) // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
        ?.imageEngine(BaseApp.engine) // 外部传入图片加载引擎，必传项
        ?.selectionData(data)
        ?.isUseCustomCamera(true)
        ?.isMaxSelectEnabledMask(true) // 选择数到了最大阀值列表是否启用蒙层效果
        ?.imageSpanCount(4) // 每行显示个数
        ?.isReturnEmpty(false) // 未选择数据时点击按钮是否可以返回
        ?.closeAndroidQChangeWH(true) //如果图片有旋转角度则对换宽高,默认为true
        ?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) // 设置相册Activity方向，不设置默认使用系统
        ?.selectionMode(if (more<=1) PictureConfig.SINGLE else PictureConfig.MULTIPLE) // 多选 or 单选
        ?.isPreviewImage(true) // 是否可预览图片
        ?.isCamera(camera) // 是否显示拍照按钮
        ?.maxSelectNum(if (more<=1) 9 else more)
        ?.maxVideoSelectNum(1)
        ?.isCompress(true)
        ?.freeStyleCropEnabled(false) // 裁剪框是否可拖拽
        ?.circleDimmedLayer(false) // 是否圆形裁剪
        ?.showCropFrame(true) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
        ?.showCropGrid(false) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
}



/**
 * 打开相机
 */
fun openCamera(any: Any?,listener: OnResultCallbackListener<LocalMedia>){
    getCameraSelector(any)?.forResult(listener)
}

fun openCamera(any: Any?,code: Int){
    getCameraSelector(any)?.forResult(code)
}

private fun getCameraSelector(any: Any?):PictureSelectionModel?{
    var pictureSelector:PictureSelector?=null
    if (any == null)return null
    if (any is Activity)
        pictureSelector = PictureSelector.create(any)
    else if (any is Fragment)
        pictureSelector = PictureSelector.create(any)
    return  pictureSelector?.openCamera(PictureMimeType.ofAll())
        ?.isUseCustomCamera(true)
        ?.isCompress(true)
}



fun getPath(data:LocalMedia?):String?{
    if (data==null)return null
    if (!TextUtils.isEmpty(data.compressPath)){
        val file = File(data.compressPath)
        if (file.exists())
            return data.compressPath
    }
    if (!TextUtils.isEmpty(data.cutPath)){
        val file = File(data.cutPath)
        if (file.exists())
            return data.cutPath
    }
    if (!TextUtils.isEmpty(data.originalPath)){
        val file = File(data.originalPath)
        if (file.exists())
            return data.originalPath
    }
    if (!TextUtils.isEmpty(data.androidQToPath)){
        val file = File(data.androidQToPath)
        if (file.exists())
            return data.androidQToPath
    }
    if (!TextUtils.isEmpty(data.realPath)){
        val file = File(data.realPath)
        if (file.exists())
            return data.realPath
    }
    if (!TextUtils.isEmpty(data.path)){
        val file = File(data.path)
        if (file.exists())
            return data.path
    }
    return null
}

fun getOnePath(resultCode: Int,data: Intent?):LocalMedia?{
    val datas = getPaths(resultCode, data)
    for (item in datas){
        val path = getPath(item)
        if (path != null)return item
    }
    return null
}

fun getOnePath(data: List<LocalMedia>?):String?{
    if (data==null)return null
    for (item in data){
        val path = getPath(item)
        if (path != null)return path
    }
    return null
}

fun getPaths(resultCode:Int,data: Intent?):MutableList<LocalMedia>{
    if (resultCode != Activity.RESULT_OK || data == null)
        return ArrayList<LocalMedia>()
    val data = PictureSelector.obtainMultipleResult(data)?:ArrayList<LocalMedia>()
    val newData = ArrayList<LocalMedia>()
    for (item in data){
        val path = getPath(item)
        if (path != null)newData.add(item)
    }
    return newData
}

fun getPaths(data: List<LocalMedia>?):MutableList<LocalMedia>{
    if (data == null)return ArrayList<LocalMedia>()
    val newData = ArrayList<LocalMedia>()
    for (item in data){
        val path = getPath(item)
        if (path != null)newData.add(item)
    }
    return newData
}