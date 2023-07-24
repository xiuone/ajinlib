package xy.xy.base.assembly.picture.select.assembly

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import picture.luck.picture.lib.basic.PictureSelectionModel
import picture.luck.picture.lib.basic.PictureSelector
import picture.luck.picture.lib.config.SelectModeConfig
import picture.luck.picture.lib.entity.LocalMedia
import picture.luck.picture.lib.interfaces.OnResultCallbackListener
import picture.luck.picture.lib.language.LanguageConfig
import xy.xy.base.R
import xy.xy.base.assembly.base.BaseAssemblyWithContext
import xy.xy.base.assembly.picture.select.permission.PicturePermissionsIntercept
import xy.xy.base.utils.config.language.AppLanguageConfig
import xy.xy.base.utils.config.language.LanguageManger
import xy.xy.base.utils.exp.*
import com.xy.picture.select.PictureSelectCallBack
import xy.xy.base.assembly.picture.select.camera.ImageCameraIntercept
import xy.xy.base.assembly.picture.select.engine.GlideEngine
import xy.xy.base.permission.IPermissionInterceptorCreateListener
import java.io.File


open class PictureSelectorAssembly(view: PictureSelectorAssemblyView,listener: IPermissionInterceptorCreateListener) : BaseAssemblyWithContext<PictureSelectorAssemblyView>(view){
    private val cropEngine by lazy { this.view?.onCreateImageCropEngine() }
    private val audioIntercept by lazy { this.view?.onCreateRecordAudioIntercept() }
    private val glideEngine by lazy { GlideEngine() }
    private val intercept by lazy { PicturePermissionsIntercept(listener.onCreateIPermissionInterceptor()) }

    private fun PictureSelectionModel?.requestBase(): PictureSelectionModel?{
        val max = this@PictureSelectorAssembly.view?.onMaxNumber()?:1
        val selectedList = this@PictureSelectorAssembly.view?.onGetSelectedList()?:ArrayList()
        return this?.setLanguage(getLanguage())
            ?.setSelectorUIStyle(this@PictureSelectorAssembly.view?.onGetPictureSelectorStyle())
            ?.isOriginalControl(true)
            ?.setImageEngine(glideEngine)
            ?.isDisplayCamera(true)
            ?.isOpenClickSound(false)
            ?.isPreviewImage(this@PictureSelectorAssembly.view?.isPreviewImage()?:false)
            ?.isCompressEngine(true)
            ?.isGif(false)
            ?.setSelectionMode(if (max == 1) SelectModeConfig.SINGLE else SelectModeConfig.MULTIPLE)
            ?.setMaxSelectNum(max)
            ?.setSelectedData(selectedList)
            ?.setOutputCameraDir(getOutPath())
            ?.setOutputAudioDir(getOutPath())
            ?.setQuerySandboxDir(getOutPath())
            ?.setPermissionsInterceptListener(intercept)

            ?.setCropEngine(cropEngine)

            ?.setCameraInterceptListener(ImageCameraIntercept())
            ?.setRecordAudioInterceptListener(audioIntercept)
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
    fun openSelectHeadIcon(callBack: PictureSelectCallBack){
        getPictureSelector()
            ?.openGallery(SelectType.IMAGE.type)
            ?.requestBase()
            ?.setOfAllCameraType(SelectType.IMAGE.type)
            ?.setSelectionMode(SelectModeConfig.SINGLE)
            ?.setMaxSelectNum(1)
            ?.setOutputCameraDir(getOutPath(SelectType.IMAGE))
            ?.setOutputAudioDir(getOutPath(SelectType.IMAGE))
            ?.setQuerySandboxDir(getOutPath(SelectType.IMAGE))
            ?.setPermissionsInterceptListener(intercept)
            ?.request(callBack)

    }

    /**
     * 选择普通图片
     */
    fun openCommonIcon(callBack: PictureSelectCallBack){
        getPictureSelector()
            ?.openGallery(getSelectMimeType())
            ?.requestBase()
            ?.setOfAllCameraType(SelectType.IMAGE.type)
            ?.request(callBack)
    }


    fun openCamera(callBack: PictureSelectCallBack){
        getPictureSelector()
            ?.openCamera(getSelectMimeType())
            ?.setOfAllCameraType(SelectType.IMAGE.type)
            ?.setCameraInterceptListener(ImageCameraIntercept())
            ?.setRecordAudioInterceptListener(audioIntercept)
            ?.isCompressEngine(true)
            ?.setLanguage(getLanguage())
            ?.isOriginalControl(true)
            ?.setOutputAudioDir(getOutPath())
            ?.forResultActivity(object :
                OnResultCallbackListener<LocalMedia> {
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


    private fun getPictureSelector(): PictureSelector?{
        val fragment = getCurrentFragment()
        val activity = getCurrentAct()
        if (fragment != null)return PictureSelector.create(fragment)
        if (activity is FragmentActivity) return PictureSelector.create(activity)
        if (activity is AppCompatActivity) return PictureSelector.create(activity)
        return null
    }




    enum class SelectType(val type:Int){
        ALL(0),
        IMAGE(1),
        VIDEO(2),
        AUDIO(3),
    }
}