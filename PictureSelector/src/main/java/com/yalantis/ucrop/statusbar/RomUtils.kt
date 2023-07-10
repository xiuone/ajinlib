package com.yalantis.ucrop.statusbar

import android.os.Build
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.util.*
import java.util.regex.Pattern
import kotlin.jvm.JvmOverloads

/**
 * @author：luck
 * @data：2018/3/28 下午1:02
 * @描述: Rom版本管理
 */
object RomUtils {
    private val ROM_SAMSUNG = arrayOf("samsung")
    private const val UNKNOWN = "unknown"
    private var romType: Int? = null
    val lightStatausBarAvailableRomType: Int
        get() {
            if (romType != null) {
                return romType!!
            }
            if (isMIUIV6OrAbove) {
                romType = AvailableRomType.MIUI
                return romType!!
            }
            if (isFlymeV4OrAbove) {
                romType = AvailableRomType.FLYME
                return romType!!
            }
            if (isAndroid5OrAbove) {
                romType = AvailableRomType.ANDROID_NATIVE
                return romType!!
            }
            romType = AvailableRomType.NA
            return romType!!
        }

    //Flyme V4的displayId格式为 [Flyme OS 4.x.x.xA]
    //Flyme V5的displayId格式为 [Flyme 5.x.x.x beta]
    private val isFlymeV4OrAbove: Boolean
        private get() = flymeVersion >= 4

    //Flyme V4的displayId格式为 [Flyme OS 4.x.x.xA]
    //Flyme V5的displayId格式为 [Flyme 5.x.x.x beta]
    val flymeVersion: Int
        get() {
            var displayId = Build.DISPLAY
            if (!TextUtils.isEmpty(displayId) && displayId.contains("Flyme")) {
                displayId = displayId.replace("Flyme".toRegex(), "")
                displayId = displayId.replace("OS".toRegex(), "")
                displayId = displayId.replace(" ".toRegex(), "")
                val version = displayId.substring(0, 1)
                return stringToInt(version)
            }
            return 0
        }

    //MIUI V6对应的versionCode是4
    //MIUI V7对应的versionCode是5
    private val isMIUIV6OrAbove: Boolean
        private get() {
            val miuiVersionCodeStr = systemProperty
            if (!TextUtils.isEmpty(miuiVersionCodeStr)) {
                try {
                    val miuiVersionCode = toInt(miuiVersionCodeStr)
                    if (miuiVersionCode >= 4) {
                        return true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return false
        }
    val mIUIVersionCode: Int
        get() {
            val miuiVersionCodeStr = systemProperty
            var miuiVersionCode = 0
            if (!TextUtils.isEmpty(miuiVersionCodeStr)) {
                try {
                    miuiVersionCode = toInt(miuiVersionCodeStr)
                    return miuiVersionCode
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return miuiVersionCode
        }

    //Android Api 23以上
    private val isAndroid5OrAbove: Boolean
        private get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    private val systemProperty: String?
        private get() {
            val line: String
            var input: BufferedReader? = null
            try {
                val p = Runtime.getRuntime().exec("getprop " + "ro.miui.ui.version.code")
                input = BufferedReader(InputStreamReader(p.inputStream), 1024)
                line = input.readLine()
                input.close()
            } catch (ex: IOException) {
                return null
            } finally {
                if (input != null) {
                    try {
                        input.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            return line
        }

    /**
     * Return whether the rom is made by samsung.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    val isSamsung: Boolean
        get() {
            val brand = brand
            val manufacturer = manufacturer
            return isRightRom(brand, manufacturer, *ROM_SAMSUNG)
        }

    private fun isRightRom(brand: String, manufacturer: String, vararg names: String): Boolean {
        for (name in names) {
            if (brand.contains(name) || manufacturer.contains(name)) {
                return true
            }
        }
        return false
    }

    /**/
    private val manufacturer: String
        private get() {
            try {
                val manufacturer = Build.MANUFACTURER
                if (!TextUtils.isEmpty(manufacturer)) {
                    return manufacturer.lowercase(Locale.getDefault())
                }
            } catch (ignore: Throwable) { /**/
            }
            return UNKNOWN
        }

    /**/
    private val brand: String
        private get() {
            try {
                val brand = Build.BRAND
                if (!TextUtils.isEmpty(brand)) {
                    return brand.lowercase(Locale.getDefault())
                }
            } catch (ignore: Throwable) { /**/
            }
            return UNKNOWN
        }

    /**
     * 匹配数值
     *
     * @param str
     * @return
     */
    fun stringToInt(str: String?): Int {
        val pattern = Pattern.compile("^[-\\+]?[\\d]+$")
        return if (pattern.matcher(str).matches()) toInt(str) else 0
    }

    @JvmOverloads
    fun toInt(o: Any?, defaultValue: Int = 0): Int {
        if (o == null) {
            return defaultValue
        }
        val value: Int
        value = try {
            val s = o.toString().trim { it <= ' ' }
            if (s.contains(".")) {
                s.substring(0, s.lastIndexOf(".")).toInt()
            } else {
                s.toInt()
            }
        } catch (e: Exception) {
            defaultValue
        }
        return value
    }

    object AvailableRomType {
        const val MIUI = 1
        const val FLYME = 2
        const val ANDROID_NATIVE = 3
        const val NA = 4
    }
}