package com.xy.base.utils.config.language

import android.content.Context
import com.xy.base.utils.exp.getSpString
import com.xy.base.utils.exp.setSpString
import com.xy.base.utils.config.font.ConfigChangeListener
import java.util.*

class LanguageManger {

    /**
     * 当没有设置的时候需要先去初始化  去给他干掉
     * @param context
     * @return
     */
    fun initLanguage(context: Context): LanguageMode {
        val languageList = AppLanguageConfig.getAllLanguage()
        val locale = context.resources.configuration.locale
        val tag = locale.toLanguageTag()
        if (tag == AppLanguageConfig.zh_Cn_Tag) {
            return LanguageMode(AppLanguageConfig.zh_cn_name, Locale.SIMPLIFIED_CHINESE)
        }
        if (tag == AppLanguageConfig.zh_Tw_Tag) {
            setCurrentLanguage(context, AppLanguageConfig.zh_tw_nam)
            return LanguageMode(AppLanguageConfig.zh_tw_nam, Locale.TRADITIONAL_CHINESE)
        }
        for (mode in languageList) {
            if (mode.locales == locale) {
                return mode
            }
        }
        for (mode in languageList) {
            val listLanguage = mode.locales.language.lowercase(mode.locales)
            val localeLanguage = locale.language.lowercase(mode.locales)
            if (listLanguage == localeLanguage) {
                return mode
            }
        }
        return LanguageMode(AppLanguageConfig.en_name, Locale.ENGLISH)
    }

    /**
     * 获取当前语言
     */
    fun getCurrentLanguage(context: Context): LanguageMode {
        val languageHistory = context.getSpString(AppLanguageConfig.LANGUAGE_KEY, null)
        languageHistory?.run {
            val languageList = AppLanguageConfig.getAllLanguage()
            for (mode in languageList) {
                if (mode.name == languageHistory) {
                    return mode
                }
            }
        }
        return initLanguage(context)
    }

    /**
     * 保存当前语言
     * @param language
     */
    fun setCurrentLanguage(context: Context,language: String) {
        var status = false
        val languageList = AppLanguageConfig.getAllLanguage()
        for (item in languageList) {
            if (item.name == language) {
                status = true
                break
            }
        }
        if (!status) return
        context.setSpString(AppLanguageConfig.LANGUAGE_KEY, language)
        if (context is ConfigChangeListener)
            context.onChangeConfig()
    }

    /**
     * 是否是中国
     * @return
     */
    fun isZh(context: Context): Boolean{
        val locale = getCurrentLanguage(context)
        val tag = locale.locales.toLanguageTag()
        return tag == Locale.CHINA.toLanguageTag() || tag == Locale.TRADITIONAL_CHINESE.toLanguageTag()
    }

    companion object{
        val instant = LanguageManger()
    }
}