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

//// 进入相册 以下是例子：不需要的api可以不写
//
//// 进入相册 以下是例子：不需要的api可以不写
//PictureSelector.create(this@MainActivity )
//.openGallery(chooseMode) // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//.imageEngine(GlideEngine.createGlideEngine()) // 外部传入图片加载引擎，必传项
////.theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
//.setPictureUIStyle(mSelectorUIStyle) //.setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
////.setPictureCropStyle(mCropParameterStyle)// 动态自定义裁剪主题
//.setPictureWindowAnimationStyle(mWindowAnimationStyle) // 自定义相册启动退出动画
//.isWeChatStyle(isWeChatStyle) // 是否开启微信图片选择风格
//.isUseCustomCamera(cb_custom_camera.isChecked()) // 是否使用自定义相机
//.setLanguage(language) // 设置语言，默认中文
//.isPageStrategy(cbPage.isChecked()) // 是否开启分页策略 & 每页多少条；默认开启
//.setRecyclerAnimationMode(animationMode) // 列表动画效果
//.isWithVideoImage(true) // 图片和视频是否可以同选,只在ofAll模式下有效
////.isSyncCover(true)// 是否强制从MediaStore里同步相册封面，如果相册封面没显示异常则没必要设置
////.isCameraAroundState(false) // 是否开启前置摄像头，默认false，如果使用系统拍照 可能部分机型会有兼容性问题
////.isCameraRotateImage(false) // 拍照图片旋转是否自动纠正
////.isAutoRotating(false)// 压缩时自动纠正有旋转的图片
//.isMaxSelectEnabledMask(cbEnabledMask.isChecked()) // 选择数到了最大阀值列表是否启用蒙层效果
////.isAutomaticTitleRecyclerTop(false)// 连续点击标题栏RecyclerView是否自动回到顶部,默认true
////.loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())// 获取图片资源缓存，主要是解决华为10部分机型在拷贝文件过多时会出现卡的问题，这里可以判断只在会出现一直转圈问题机型上使用
////.setOutputCameraPath(createCustomCameraOutPath())// 自定义相机输出目录
////.setButtonFeatures(CustomCameraView.BUTTON_STATE_BOTH)// 设置自定义相机按钮状态
//.setCaptureLoadingColor(ContextCompat.getColor(getContext(), R.color.app_color_blue))
//.maxSelectNum(maxSelectNum) // 最大图片选择数量
//.minSelectNum(1) // 最小选择数量
//.maxVideoSelectNum(1) // 视频最大选择数量
////.minVideoSelectNum(1)// 视频最小选择数量
////.closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 关闭在AndroidQ下获取图片或视频宽高相反自动转换
//.imageSpanCount(4) // 每行显示个数
////.queryFileSize() // 过滤最大资源,已废弃
////.filterMinFileSize(5)// 过滤最小资源，单位kb
////.filterMaxFileSize()// 过滤最大资源，单位kb
//.isReturnEmpty(false) // 未选择数据时点击按钮是否可以返回
//.closeAndroidQChangeWH(true) //如果图片有旋转角度则对换宽高,默认为true
//.closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q()) // 如果视频有旋转角度则对换宽高,默认为false
//.isAndroidQTransform(true) // 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
//.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) // 设置相册Activity方向，不设置默认使用系统
//.isOriginalImageControl(cb_original.isChecked()) // 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，裁剪功能将会失效
//.isDisplayOriginalSize(true) // 是否显示原文件大小，isOriginalImageControl true有效
//.isEditorImage(cbEditor.isChecked()) //是否编辑图片
////.isAutoScalePreviewImage(true)// 如果图片宽度不能充满屏幕则自动处理成充满模式
////.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
////.bindCustomPreviewCallback(new MyCustomPreviewInterfaceListener())// 自定义图片预览回调接口
////.bindCustomCameraInterfaceListener(new MyCustomCameraInterfaceListener())// 提供给用户的一些额外的自定义操作回调
////.bindCustomPermissionsObtainListener(new MyPermissionsObtainCallback())// 自定义权限拦截
////.bindCustomChooseLimitListener(new MyChooseLimitCallback()) // 自定义选择限制条件Dialog
////.cameraFileName(System.currentTimeMillis() +".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
////.renameCompressFile(System.currentTimeMillis() +".jpg")// 重命名压缩文件名、 如果是多张压缩则内部会自动拼上当前时间戳防止重复
////.renameCropFileName(System.currentTimeMillis() + ".jpg")// 重命名裁剪文件名、 如果是多张裁剪则内部会自动拼上当前时间戳防止重复
//.selectionMode(if (cb_choose_mode.isChecked())PictureConfig.MULTIPLE else PictureConfig.SINGLE) // 多选 or 单选
//.isSingleDirectReturn(cb_single_back.isChecked()) // 单选模式下是否直接返回，PictureConfig.SINGLE模式下有效
//.isPreviewImage(cb_preview_img.isChecked()) // 是否可预览图片
//.isPreviewVideo(cb_preview_video.isChecked()) // 是否可预览视频
////.querySpecifiedFormatSuffix(PictureMimeType.ofJPEG())// 查询指定后缀格式资源
////.queryMimeTypeConditions(PictureMimeType.ofWEBP())
//.isEnablePreviewAudio(cb_preview_audio.isChecked()) // 是否可播放音频
//.isCamera(cb_isCamera.isChecked()) // 是否显示拍照按钮
////.isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
////.isMultipleRecyclerAnimation(false)// 多图裁剪底部列表显示动画效果
//.isZoomAnim(true) // 图片列表点击 缩放效果 默认true
////.imageFormat(PictureMimeType.PNG) // 拍照保存图片格式后缀,默认jpeg,Android Q使用PictureMimeType.PNG_Q
//.setCameraImageFormat(PictureMimeType.JPEG) // 相机图片格式后缀,默认.jpeg
//.setCameraVideoFormat(PictureMimeType.MP4) // 相机视频格式后缀,默认.mp4
//.setCameraAudioFormat(PictureMimeType.AMR) // 录音音频格式后缀,默认.amr
//.isEnableCrop(cb_crop.isChecked()) // 是否裁剪
////.basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
//.isCompress(cb_compress.isChecked()) // 是否压缩
////.compressFocusAlpha(true)// 压缩时是否开启透明通道
////.compressEngine(ImageCompressEngine.createCompressEngine()) // 自定义压缩引擎
////.compressQuality(80)// 图片压缩后输出质量 0~ 100
//.synOrAsy(false) //同步true或异步false 压缩 默认同步
////.queryMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
////.compressSavePath(getPath())//压缩图片保存地址
////.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
////.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
//.withAspectRatio(aspect_ratio_x, aspect_ratio_y) // 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
//.hideBottomControls(!cb_hide.isChecked()) // 是否显示uCrop工具栏，默认不显示
//.isGif(cb_isGif.isChecked()) // 是否显示gif图片
////.isWebp(false)// 是否显示webp图片,默认显示
////.isBmp(false)//是否显示bmp图片,默认显示
//.freeStyleCropEnabled(cb_styleCrop.isChecked()) // 裁剪框是否可拖拽
////.freeStyleCropMode(OverlayView.DEFAULT_FREESTYLE_CROP_MODE)// 裁剪框拖动模式
//.isCropDragSmoothToCenter(true) // 裁剪框拖动时图片自动跟随居中
//.circleDimmedLayer(cb_crop_circular.isChecked()) // 是否圆形裁剪
////.setCropDimmedColor(ContextCompat.getColor(getContext(), R.color.app_color_white))// 设置裁剪背景色值
////.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
////.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
//.showCropFrame(cb_showCropFrame.isChecked()) // 是否显示裁剪矩形边框 圆形裁剪时建议设为false
//.showCropGrid(cb_showCropGrid.isChecked()) // 是否显示裁剪矩形网格 圆形裁剪时建议设为false
//.isOpenClickSound(cb_voice.isChecked()) // 是否开启点击声音
//.selectionData(mAdapter.getData()) // 是否传入已选图片
////.isDragFrame(false)// 是否可拖动裁剪框(固定)
////.videoMinSecond(10)// 查询多少秒以内的视频
////.videoMaxSecond(15)// 查询多少秒以内的视频
////.recordVideoSecond(10)//录制视频秒数 默认60s
////.isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
////.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
//.cutOutQuality(90) // 裁剪输出质量 默认100
////.cutCompressFormat(Bitmap.CompressFormat.PNG.name())//裁剪图片输出Format格式，默认JPEG
//.minimumCompressSize(100) // 小于多少kb的图片不压缩
////.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
////.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
////.rotateEnabled(false) // 裁剪是否可旋转图片
////.scaleEnabled(false)// 裁剪是否可放大缩小图片
////.videoQuality()// 视频录制质量 0 or 1
////.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
////.forResult(new MyResultCallback(mAdapter));
//.forResult(launcherResult)