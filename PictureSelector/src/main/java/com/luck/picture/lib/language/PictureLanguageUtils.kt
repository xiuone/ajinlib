package com.luck.picture.lib.language

import android.content.Context
import com.luck.picture.lib.utils.SpUtils
import java.lang.UnsupportedOperationException
import java.lang.ref.WeakReference
import java.util.*

/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @描述: PictureLanguageUtils
 */
class PictureLanguageUtils private constructor() {
    companion object {
        private const val KEY_LOCALE = "KEY_LOCALE"
        private const val VALUE_FOLLOW_SYSTEM = "VALUE_FOLLOW_SYSTEM"

        /**
         * init app the language
         *
         * @param context
         * @param languageId
         * @param defaultLanguageId
         */
        fun setAppLanguage(context: Context, languageId: Int, defaultLanguageId: Int) {
            val contextWeakReference = WeakReference(context)
            if (languageId >= 0) {
                applyLanguage(contextWeakReference.get()!!, LocaleTransform.getLanguage(languageId))
            } else {
                if (defaultLanguageId >= 0) {
                    applyLanguage(
                        contextWeakReference.get()!!,
                        LocaleTransform.getLanguage(defaultLanguageId)
                    )
                } else {
                    setDefaultLanguage(contextWeakReference.get())
                }
            }
        }

        /**
         * Apply the language.
         *
         * @param locale The language of locale.
         */
        private fun applyLanguage(
            context: Context, locale: Locale,
            isFollowSystem: Boolean = false
        ) {
            if (isFollowSystem) {
                SpUtils.putString(context, KEY_LOCALE, VALUE_FOLLOW_SYSTEM)
            } else {
                val localLanguage = locale.language
                val localCountry = locale.country
                SpUtils.putString(context, KEY_LOCALE, "$localLanguage$$localCountry")
            }
            updateLanguage(context, locale)
        }

        private fun updateLanguage(context: Context, locale: Locale) {
            val resources = context.resources
            val config = resources.configuration
            val contextLocale = config.locale
            if (equals(contextLocale.language, locale.language)
                && equals(contextLocale.country, locale.country)
            ) {
                return
            }
            val dm = resources.displayMetrics
            config.setLocale(locale)
            context.createConfigurationContext(config)
            resources.updateConfiguration(config, dm)
        }

        /**
         * set default language
         *
         * @param context
         */
        private fun setDefaultLanguage(context: Context?) {
            val resources = context!!.resources
            val config = resources.configuration
            val dm = resources.displayMetrics
            config.setLocale(Locale.getDefault())
            context.createConfigurationContext(config)
            resources.updateConfiguration(config, dm)
        }

        private fun equals(s1: CharSequence?, s2: CharSequence?): Boolean {
            if (s1 === s2) return true
            var length: Int
            return if (s1 != null && s2 != null && s1.length.also { length = it } == s2.length) {
                if (s1 is String && s2 is String) {
                    s1 == s2
                } else {
                    for (i in 0 until length) {
                        if (s1[i] != s2[i]) return false
                    }
                    true
                }
            } else false
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}