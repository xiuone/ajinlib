package com.xy.baselib.language

import android.content.Context
import com.xy.baselib.BaseApp
import com.xy.baselib.config.BaseObject
import java.util.*

class LanguageManger {

    /**
     * 当没有设置的时候需要先去初始化  去给他干掉
     * @param context
     * @return
     */
    fun initLanguage(context: Context): LanguageMode {
        val languageList = LanguageConfig.getAllLanguage()
        val locale = context.resources.configuration.locale
        val tag = locale.toLanguageTag()
        if (tag == LanguageConfig.zh_Cn_Tag) {
            return LanguageMode(LanguageConfig.zh_cn_name, Locale.SIMPLIFIED_CHINESE)
        }
        if (tag == LanguageConfig.zh_Tw_Tag) {
            setCurrentLanguage(context, LanguageConfig.zh_tw_nam)
            return LanguageMode(LanguageConfig.zh_tw_nam, Locale.TRADITIONAL_CHINESE)
        }
        for (mode in languageList) {
            if (mode.locales == locale) {
                return mode
            }
        }
        for (mode in languageList) {
            val listLanguage = mode.locales.language.toLowerCase(mode.locales)
            val localeLanguage = locale.language.toLowerCase(mode.locales)
            if (listLanguage == localeLanguage) {
                return mode
            }
        }
        return LanguageMode(LanguageConfig.en_name, Locale.ENGLISH)
    }

    /**
     * 获取当前语言
     */
    fun getCurrentLanguage(context: Context): LanguageMode {
        val languageHistory = BaseObject.spHelperUtils.getString(context,
            LanguageConfig.LANGUAGE_KEY, null)
        languageHistory?.run {
            val languageList = LanguageConfig.getAllLanguage()
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
        val languageList = LanguageConfig.getAllLanguage()
        for (item in languageList) {
            if (item.name == language) {
                status = true
                break
            }
        }
        if (!status) return
        BaseObject.spHelperUtils.setString(context, LanguageConfig.LANGUAGE_KEY, language)
        BaseObject.configNotify.switchRes()
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
}