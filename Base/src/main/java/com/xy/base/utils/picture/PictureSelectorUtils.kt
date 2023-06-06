package com.xy.base.utils.picture

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureSelectionConfig.selectorStyle
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.xy.base.utils.config.language.AppLanguageConfig
import com.xy.base.utils.config.language.LanguageManger
import java.util.ArrayList


object PictureSelectorUtils {
    private val glideEngine by lazy { GlideEngine() }

    /**
     * 选择头像
     */
    fun openSelectHeadIcon(context: Context?,any: Any?,callBack: PictureSelectCallBack){
        getPictureSelector(any)
            ?.openGallery(SelectMimeType.ofImage())
            ?.setLanguage(getLanguage(context))
            ?.setSelectorUIStyle(selectorStyle)
            ?.setImageEngine(glideEngine)
            ?.setSelectionMode(SelectModeConfig.SINGLE)
            ?.isDisplayCamera(true)
            ?.isOpenClickSound(false)
            ?.isPreviewImage(true)
            ?.setCropEngine(ImageCropEngineHead())
            ?.setCompressEngine(ImageFileCompressEngine())
            ?.isGif(false)
            ?.forResult(object : OnResultCallbackListener<LocalMedia>{
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    if (result == null)return
                    callBack.onResult(result)
                }

                override fun onCancel() {}
            })

    }

    /**
     * 选择背景图片   就是个人中心显示得
     */
    fun openSelectBack(context: Context?,any: Any?,callBack: PictureSelectCallBack){
        getPictureSelector(any)
            ?.openGallery(SelectMimeType.ofImage())
            ?.setLanguage(getLanguage(context))
            ?.setSelectorUIStyle(selectorStyle)
            ?.setImageEngine(glideEngine)
            ?.setSelectionMode(SelectModeConfig.SINGLE)
            ?.isDisplayCamera(true)
            ?.isOpenClickSound(false)
            ?.isPreviewImage(true)
            ?.setCropEngine(ImageCropEngineBack())
            ?.setCompressEngine(ImageFileCompressEngine())
            ?.isGif(false)
            ?.forResult(object : OnResultCallbackListener<LocalMedia>{
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    if (result == null)return
                    callBack.onResult(result)
                }

                override fun onCancel() {}
            })
    }

    /**
     * 选择图片 用于发送到聊天信息
     */
    fun openSelectSendChat(context: Context?,any: Any?,chooseMode: Int,callBack: PictureSelectCallBack){
        getPictureSelector(any)
            ?.openGallery(chooseMode)
            ?.setLanguage(getLanguage(context))
            ?.setSelectorUIStyle(selectorStyle)
            ?.setImageEngine(glideEngine)
            ?.setSelectionMode(SelectModeConfig.MULTIPLE)
            ?.setMaxSelectNum(9)
            ?.isDisplayCamera(false)
            ?.isOpenClickSound(false)
            ?.isPreviewImage(true)
            ?.setCropEngine(ImageCropEngineBack())
            ?.setCompressEngine(ImageFileCompressEngine())
            ?.isGif(false)
            ?.forResult(object : OnResultCallbackListener<LocalMedia>{
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    if (result == null)return
                    callBack.onResult(result)
                }

                override fun onCancel() {}
            })
    }


    fun openCamera(context: Context?,any: Any?,callBack: PictureSelectCallBack){
        getPictureSelector(any)
            ?.openCamera(SelectMimeType.ofAll())
            ?.setLanguage(getLanguage(context))
            ?.setCropEngine(ImageCropEngineBack())
            ?.setCompressEngine(ImageFileCompressEngine())
            ?.setRecordVideoMinSecond(2)
            ?.setSelectMinDurationSecond(2)
            ?.setRecordVideoMaxSecond(15)
            ?.setSelectMaxDurationSecond(15)
            ?.isQuickCapture(true)
            ?.forResult(object : OnResultCallbackListener<LocalMedia>{
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    if (result == null)return
                    callBack.onResult(result)
                }

                override fun onCancel() {}
            })
    }


    private fun getLanguage(context: Context?):Int{
        if (context == null)return LanguageConfig.SYSTEM_LANGUAGE
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


    private fun getPictureSelector(any: Any?):PictureSelector?{
        if (any is FragmentActivity)
            return PictureSelector.create(any)
        if (any is Fragment)
            return PictureSelector.create(any)
        if (any is AppCompatActivity)
            return PictureSelector.create(any)
        return null
    }
}