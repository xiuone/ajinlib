package com.xy.picture.select.assembly

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.basic.PictureSelectionModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.style.PictureSelectorStyle
import com.xy.base.R
import com.xy.base.assembly.base.BaseAssemblyViewWithContext
import com.xy.base.assembly.base.BaseAssemblyWithContext
import com.xy.base.utils.config.language.AppLanguageConfig
import com.xy.base.utils.config.language.LanguageManger
import com.xy.base.utils.exp.*
import com.xy.picture.select.PictureSelectCallBack
import com.xy.picture.select.camera.ImageCameraIntercept
import com.xy.picture.select.compress.ImageFileCompressEngine
import com.xy.picture.select.crop.ImageCropEngineBack
import com.xy.picture.select.crop.ImageCropEngineHead
import com.xy.picture.select.engine.GlideEngine
import com.xy.picture.select.permission.ImagePermissionIntercept
import java.io.File


open class PictureSelectorAssembly(view: PictureSelectorAssemblyView?) :
    BaseAssemblyWithContext<PictureSelectorAssemblyView>(view){
    private val cropEngine by lazy { this.view?.onCreateImageCropEngine() }
    private val audioIntercept by lazy { this.view?.onCreateRecordAudioIntercept() }
    private val glideEngine by lazy { GlideEngine() }

    private fun PictureSelectionModel?.requestBase():PictureSelectionModel?{
        val max = this@PictureSelectorAssembly.view?.onMaxNumber()?:1
        val selectedList = this@PictureSelectorAssembly.view?.onGetSelectedList()?:ArrayList()
        return this?.setLanguage(getLanguage())
            ?.setSelectorUIStyle(this@PictureSelectorAssembly.view?.onGetPictureSelectorStyle())
            ?.isOriginalControl(true)
            ?.setImageEngine(glideEngine)
            ?.isDisplayCamera(true)
            ?.isOpenClickSound(false)
            ?.isPreviewImage(this@PictureSelectorAssembly.view?.isPreviewImage()?:false)
            ?.setCompressEngine(ImageFileCompressEngine())
            ?.isGif(false)
            ?.setSelectionMode(if (max == 1) SelectModeConfig.SINGLE else SelectModeConfig.MULTIPLE)
            ?.setMaxSelectNum(max)
            ?.setSelectedData(selectedList)
            ?.setOutputCameraDir(getOutPath())
            ?.setOutputAudioDir(getOutPath())
            ?.setQuerySandboxDir(getOutPath())
            ?.setPermissionDescriptionListener(ImagePermissionIntercept())
            ?.setPermissionDeniedListener(ImagePermissionIntercept())

            ?.setCropEngine(cropEngine)

            ?.setCameraInterceptListener(ImageCameraIntercept())
            ?.setRecordAudioInterceptListener(audioIntercept)
    }

    private fun PictureSelectionModel?.request(callBack: PictureSelectCallBack){
        this?.forResult(object : OnResultCallbackListener<LocalMedia>{
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    if (result == null)return
                    callBack.onResult(result)
                }

                override fun onCancel() {}
            })
    }

    /**
     * 选择头像
     */
    fun openSelectHeadIcon(callBack: PictureSelectCallBack){
        getPictureSelector()
            ?.openGallery(SelectType.IMAGE.type)
            ?.setSelectionMode(SelectModeConfig.SINGLE)
            ?.setMaxSelectNum(1)
            ?.setOutputCameraDir(getOutPath(SelectType.IMAGE))
            ?.setOutputAudioDir(getOutPath(SelectType.IMAGE))
            ?.setQuerySandboxDir(getOutPath(SelectType.IMAGE))
            ?.setPermissionDescriptionListener(ImagePermissionIntercept())
            ?.setPermissionDeniedListener(ImagePermissionIntercept())
            ?.requestBase()
            ?.request(callBack)

    }

    /**
     * 选择普通图片
     */
    fun openCommonIcon(callBack: PictureSelectCallBack){
        getPictureSelector()
            ?.openGallery(getSelectMimeType())
            ?.request(callBack)
    }


    fun openCamera(callBack: PictureSelectCallBack){
        getPictureSelector()
            ?.openCamera(getSelectMimeType())
            ?.setCameraInterceptListener(ImageCameraIntercept())
            ?.setRecordAudioInterceptListener(audioIntercept)
            ?.setCompressEngine(ImageFileCompressEngine())
            ?.setLanguage(getLanguage())
            ?.isOriginalControl(true)
            ?.setOutputAudioDir(getOutPath())
            ?.forResultActivity(object : OnResultCallbackListener<LocalMedia>{
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    if (result == null)return
                    callBack.onResult(result)
                }
                override fun onCancel() {}
            })
    }


    private fun getOutPath():String? = getOutPath(this.view?.onCreateSelectType())

    private fun getOutPath(type: SelectType?):String?{
        val dirName = getContext()?.getResString(R.string.app_name)
        val path = if (type == SelectType.AUDIO) getContext()?.getAudioDir(dirName) else getContext()?.getSdImageDir(dirName)
        if (!path.isNullOrEmpty()){
            val file = File(path)
            if (!file.exists()){
                file.mkdirs()
            }
        }
        return path
    }



    private fun getSelectMimeType():Int = (this.view?.onCreateSelectType()?: SelectType.ALL).type

    private fun getLanguage():Int{
        val context = getContext() ?: return LanguageConfig.SYSTEM_LANGUAGE
        val language = LanguageManger.instant.getCurrentLanguage(context)
        var picLanguage = LanguageConfig.UNKNOWN_LANGUAGE
        when(language.name){
            AppLanguageConfig.zh_cn_name->{
                picLanguage = LanguageConfig.CHINESE
            }
            AppLanguageConfig.zh_tw_nam->{
                picLanguage = LanguageConfig.TRADITIONAL_CHINESE
            }
            AppLanguageConfig.en_name->{
                picLanguage = LanguageConfig.ENGLISH
            }
            AppLanguageConfig.ko_name->{
                picLanguage = LanguageConfig.KOREA
            }
            AppLanguageConfig.de_name->{
                picLanguage = LanguageConfig.GERMANY
            }
            AppLanguageConfig.fr_name->{
                picLanguage = LanguageConfig.FRANCE
            }
            AppLanguageConfig.ja_name->{
                picLanguage = LanguageConfig.JAPAN
            }
            AppLanguageConfig.vi_name->{
                picLanguage = LanguageConfig.VIETNAM
            }
            AppLanguageConfig.es_name->{
                picLanguage = LanguageConfig.SPANISH
            }
            AppLanguageConfig.pt_br_name->{
                picLanguage = LanguageConfig.PORTUGAL
            }
            AppLanguageConfig.ru_name->{
                picLanguage = LanguageConfig.RU
            }
            else->{
                picLanguage = LanguageConfig.SYSTEM_LANGUAGE
            }
        }
        return picLanguage
    }


    private fun getPictureSelector():PictureSelector?{
        val fragment = getCurrentFragment()
        val activity = getCurrentAct()
        if (fragment != null)return PictureSelector.create(fragment)
        if (activity is FragmentActivity) return PictureSelector.create(activity)
        if (activity is AppCompatActivity) return PictureSelector.create(activity)
        return null
    }




    enum class SelectType(val type:Int){
        ALL(1),
        IMAGE(2),
        VIDEO(3),
        AUDIO(4),
    }
}