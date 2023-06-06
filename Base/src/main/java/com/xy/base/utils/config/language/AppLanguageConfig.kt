package com.xy.base.utils.config.language

import java.util.*
import kotlin.collections.ArrayList

object AppLanguageConfig {
    //语言存储表示
    const val LANGUAGE_KEY = "LANGUAGE_KEY"

    //繁体tag---国际通用
    const val zh_Tw_Tag = "zh-Hant-CN"

    //简体tag---国际通用
    const val zh_Cn_Tag = "zh-Hans-CN"

    //简体中文
    const val zh_cn_name = "中文简体"

    //'中文繁体'
    const val zh_tw_nam = "中文繁體"

    //'德语'
    const val de_name = "Deutsche"

    //'英语'
    const val en_name = "English"

    //英语(美国)
    const val en_us_name = "English (United States)"

    //'英语(英国)'
    const val en_gb_name = "English (United Kingdom)"

    //'法语'
    const val fr_name = "français"

    //'意大利语'
    const val it_name = "italiano"

    //'日语'
    const val ja_name = "日本語"

    //'韩语'
    const val ko_name = "한국어"

    //捷克语
    const val cs_name = "čeština"

    //丹麦语
    const val da_name = "dansk"

    //'希腊语'
    const val el_name = "Ελληνικά"

    //'西班牙语'
    const val es_name = "Español"

    //西班牙语(拉丁美洲和加勒比)
    const val es_la_name = "Español (América Latina y el Caribe)"

    //'芬兰语'
    const val fi_name = "Suomalainen"

    //'匈牙利语'
    const val hu_name = "Magyar"

    //'马来西亚语'
    const val ms_name = "Orang Malaysia"

    //'挪威语'
    const val no_name = "norsk"

    //'荷兰语'
    const val nl_name = "Nederlands"

    //'波兰语'
    const val pl_name = "Polskie"

    //葡萄牙语(巴西)
    const val pt_br_name = "Portugues (brasil)"

    //'葡萄牙语(葡萄牙)'
    const val pt_pt_name = "Portugues (portugal)"

    //'罗马尼亚语'
    const val ro_name = "Română"

    //'俄罗斯语'
    const val ru_name = "русский язык"

    //'斯洛伐克语'
    const val sk_name = "Slovák"

    //'瑞典语'
    const val sv_name = "svenska"

    //'泰语'
    const val th_name = "ไทย"

    //'土耳其语'
    const val tr_name = "Türk"

    //'乌克兰语'
    const val uk_name = "Український"

    //'越南语'
    const val vi_name = "Tiếng Việt"

    //'印度语'
    const val hi_name = "हिंदी"

    //'印度尼西亚语'
    const val id_name = "bahasa Indonesia"

    private val allLanguage = ArrayList<LanguageMode>()

    fun getAllLanguage() :ArrayList<LanguageMode>{
        if (allLanguage.isEmpty()) {
            allLanguage.add(LanguageMode(zh_cn_name, Locale("zh", "CN")))
            allLanguage.add(LanguageMode(zh_tw_nam, Locale("zh", "TW")))
            allLanguage.add(LanguageMode(cs_name, Locale("cs")))
            allLanguage.add(LanguageMode(da_name, Locale("da")))
            allLanguage.add(LanguageMode(de_name, Locale("de")))
            allLanguage.add(LanguageMode(el_name, Locale("el")))
            allLanguage.add(LanguageMode(en_name, Locale("en")))
            allLanguage.add(LanguageMode(en_us_name, Locale("en", "US")))
            allLanguage.add(LanguageMode(en_gb_name, Locale("en", "GB")))
            allLanguage.add(LanguageMode(es_name, Locale("es")))
            allLanguage.add(LanguageMode(es_la_name, Locale("es", "LA")))
            allLanguage.add(LanguageMode(fi_name, Locale("fi")))
            allLanguage.add(LanguageMode(fr_name, Locale("fr")))
            allLanguage.add(LanguageMode(hu_name, Locale("hu")))
            allLanguage.add(LanguageMode(it_name, Locale("it")))
            allLanguage.add(LanguageMode(ja_name, Locale("ja")))
            allLanguage.add(LanguageMode(ko_name, Locale("ko")))
            allLanguage.add(LanguageMode(ms_name, Locale("ms")))
            allLanguage.add(LanguageMode(no_name, Locale("no")))
            allLanguage.add(LanguageMode(nl_name, Locale("nl")))
            allLanguage.add(LanguageMode(pl_name, Locale("pl")))
            allLanguage.add(LanguageMode(pt_br_name, Locale("pt", "BR")))
            allLanguage.add(LanguageMode(pt_pt_name, Locale("pt", "PT")))
            allLanguage.add(LanguageMode(ro_name, Locale("ro")))
            allLanguage.add(LanguageMode(ru_name, Locale("ru")))
            allLanguage.add(LanguageMode(sk_name, Locale("sk")))
            allLanguage.add(LanguageMode(sv_name, Locale("sv")))
            allLanguage.add(LanguageMode(th_name, Locale("th")))
            allLanguage.add(LanguageMode(tr_name, Locale("tr")))
            allLanguage.add(LanguageMode(uk_name, Locale("uk")))
            allLanguage.add(LanguageMode(vi_name, Locale("vi")))
            allLanguage.add(LanguageMode(hi_name, Locale("hi")))
            allLanguage.add(LanguageMode(id_name, Locale("in")))
        }
        return allLanguage
    }
}
 