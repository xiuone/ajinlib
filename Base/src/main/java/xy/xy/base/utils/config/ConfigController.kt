package xy.xy.base.utils.config

import android.content.Context
import android.content.res.Configuration
import xy.xy.base.utils.config.font.FontManger
import xy.xy.base.utils.config.language.LanguageManger
import java.util.*

class ConfigController(val needReCreate:()->Unit={}) {
    private var languageMode: Locale ?=null
    private var fontScaleSize :Float= 1f

    fun changeConfig(newBase: Context): Configuration {
        val res = newBase.resources
        val config = res.configuration
        fontScaleSize = FontManger.instant.getFontScaleSize(newBase)
        languageMode = LanguageManger.instant.getCurrentLanguage(newBase).locales
        config.setLocale(languageMode)
        config.fontScale = fontScaleSize
        return config
    }

    fun checkChangeConfig(context: Context){
        val fontScaleSize = FontManger.instant.getFontScaleSize(context)
        val languageMode = LanguageManger.instant.getCurrentLanguage(context).locales
        val needReCreate = (fontScaleSize != this.fontScaleSize ||
                languageMode.toLanguageTag() != this.languageMode?.toLanguageTag()) &&
                this.languageMode != null
        onChangeConfig(context)
        if (needReCreate){
            needReCreate()
        }
    }

    private fun onChangeConfig(context: Context) {
        val res = context.resources
        val config = changeConfig(context)
        res.updateConfiguration(config, null)
        res.flushLayoutCache()
        res.updateConfiguration(config, res.displayMetrics)
    }

    fun onResume(context: Context){
        val fontScaleSize = FontManger.instant.getFontScaleSize(context)
        val languageMode = LanguageManger.instant.getCurrentLanguage(context).locales
        val needReCreate = (fontScaleSize != this.fontScaleSize ||
                languageMode.toLanguageTag() != this.languageMode?.toLanguageTag()) &&
                this.languageMode != null
        this.fontScaleSize = fontScaleSize
        this.languageMode = languageMode
        if (needReCreate){
            needReCreate()
        }
    }
}