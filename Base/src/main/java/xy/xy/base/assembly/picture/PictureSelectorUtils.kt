package xy.xy.base.assembly.picture

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.basic.PictureSelectionModel
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import xy.xy.base.R
import xy.xy.base.utils.config.language.AppLanguageConfig
import xy.xy.base.utils.config.language.LanguageManger
import xy.xy.base.utils.exp.*
import xy.xy.base.assembly.picture.select.PictureSelectCallBack
import xy.xy.base.assembly.picture.select.camera.ImageCameraIntercept
import xy.xy.base.assembly.picture.select.compress.ImageFileCompressEngine
import xy.xy.base.assembly.picture.select.crop.ImageCropEngineCommon
import xy.xy.base.assembly.picture.select.crop.ImageCropEngineHead
import xy.xy.base.assembly.picture.select.engine.GlideEngine
import xy.xy.base.utils.ContextHolder
import java.io.File


object PictureSelectorUtils{
    private val glideEngine by lazy { GlideEngine() }

    private fun PictureSelectionModel?.requestBase(type: SelectType, max:Int = 1, list:ArrayList<LocalMedia>?= null): PictureSelectionModel?{
        return this?.setLanguage(getLanguage())
            ?.isOriginalControl(true)
            ?.setImageEngine(glideEngine)
            ?.isDisplayCamera(true)
            ?.setCompressEngine(ImageFileCompressEngine())
            ?.isOpenClickSound(false)
            ?.isPreviewImage(true)
            ?.isGif(false)
            ?.setSelectionMode(if (max == 1) SelectModeConfig.SINGLE else SelectModeConfig.MULTIPLE)
            ?.setMaxSelectNum(max)
            ?.setSelectedData(list)
            ?.setOutputCameraDir(getOutPath(type))
            ?.setOutputAudioDir(getOutPath(type))
            ?.setQuerySandboxDir(getOutPath(type))

            ?.setCameraInterceptListener(ImageCameraIntercept())
    }

    private fun PictureSelectionModel?.request(callBack: PictureSelectCallBack){
        this?.forResult(object :
            OnResultCallbackListener<LocalMedia> {
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
    fun openSelectHeadIcon(any: Any?,callBack: PictureSelectCallBack){
        getPictureSelector(any)
            ?.openGallery(SelectType.IMAGE.type)
            ?.requestBase(SelectType.IMAGE)
            ?.setOfAllCameraType(SelectType.IMAGE.type)
            ?.setSelectionMode(SelectModeConfig.SINGLE)
            ?.setMaxSelectNum(1)
            ?.setCropEngine(ImageCropEngineHead())
            ?.setOutputCameraDir(getOutPath(SelectType.IMAGE))
            ?.setOutputAudioDir(getOutPath(SelectType.IMAGE))
            ?.setQuerySandboxDir(getOutPath(SelectType.IMAGE))
            ?.request(callBack)

    }

    /**
     * 选择普通图片
     */
    fun openCommonIcon(any: Any?, type: SelectType, callBack: PictureSelectCallBack,
                       max:Int = 1, list:ArrayList<LocalMedia>?=null){
        getPictureSelector(any)
            ?.openGallery(type.type)
            ?.requestBase(type,max,list)
            ?.setCropEngine(ImageCropEngineCommon())
            ?.setOfAllCameraType(SelectType.IMAGE.type)
            ?.request(callBack)
    }


    fun openCamera(any: Any?, type: SelectType, callBack: PictureSelectCallBack){
        getPictureSelector(any)
            ?.openCamera(type.type)
            ?.setOfAllCameraType(SelectType.IMAGE.type)
            ?.setCameraInterceptListener(ImageCameraIntercept())
            ?.setLanguage(getLanguage())
            ?.isOriginalControl(true)
            ?.setOutputAudioDir(getOutPath(type))
            ?.forResultActivity(object :
                OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    if (result == null)return
                    callBack.onResult(result)
                }
                override fun onCancel() {}
            })
    }

    private fun getOutPath(type: SelectType?):String?{
        val context = ContextHolder.getContext()
        val dirName = context?.getResString(R.string.app_name)
        val path = if (type == SelectType.AUDIO) context?.getAudioDir(dirName) else context?.getSdImageDir(dirName)
        if (!path.isNullOrEmpty()){
            val file = File(path)
            if (!file.exists()){
                file.mkdirs()
            }
        }
        return path
    }

    private fun getLanguage():Int{
        val context = ContextHolder.getContext() ?: return LanguageConfig.SYSTEM_LANGUAGE
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

    private fun getPictureSelector(any: Any?): PictureSelector?{
        if (any is Fragment)return PictureSelector.create(any)
        if (any is FragmentActivity) return PictureSelector.create(any)
        if (any is AppCompatActivity) return PictureSelector.create(any)
        return null
    }

    enum class SelectType(val type:Int){
        ALL(0),
        IMAGE(1),
        VIDEO(2),
        AUDIO(3),
    }
}